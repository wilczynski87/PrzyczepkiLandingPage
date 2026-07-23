package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.modules.GateConfig
import com.example.przyczepki_landingpage.repo.SuplaStoredTokens
import com.example.przyczepki_landingpage.repo.SuplaTokenRepo
import com.example.przyczepki_landingpage.service.SuplaTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Automatyczne odświeżanie tokenów SUPLA OAuth:
 * 1. access token w cache (~1h), odnawiany przed wygaśnięciem
 * 2. refresh token rotowany przez SUPLA – zapisywany w MongoDB (przeżywa restart)
 * 3. fallback do SUPLA_REFRESH_TOKEN z env przy pierwszym starcie
 */
class SuplaTokenProviderImpl(
    private val client: HttpClient,
    private val gateConfig: GateConfig,
    private val tokenRepo: SuplaTokenRepo,
) : SuplaTokenProvider {

    private val mutex = Mutex()

    @Volatile
    private var cachedAccessToken: String? = null

    @Volatile
    private var expiresAt: Instant = Instant.DISTANT_PAST

    @Volatile
    private var refreshToken: String? = gateConfig.refreshToken

    @Volatile
    private var loadedFromStore = false

    override suspend fun getAccessToken(forceRefresh: Boolean): String = mutex.withLock {
        ensureLoadedFromStore()

        if (!forceRefresh) {
            val cached = cachedAccessToken
            if (cached != null && Clock.System.now() < expiresAt) {
                return cached
            }
        }

        return when {
            !refreshToken.isNullOrBlank() && canRefresh() -> {
                runCatching { refreshAccessTokenLocked() }
                    .recoverCatching { firstError ->
                        // Mongo może mieć stary/zrotowany refresh – spróbuj fallback z .env
                        val envRefresh = gateConfig.refreshToken
                        if (!envRefresh.isNullOrBlank() && envRefresh != refreshToken) {
                            println("SUPLA: refresh z DB nieudany, próbuję SUPLA_REFRESH_TOKEN z env")
                            refreshToken = envRefresh
                            refreshAccessTokenLocked()
                        } else {
                            throw firstError
                        }
                    }
                    .getOrElse { e ->
                        if (!gateConfig.accessToken.isNullOrBlank()) {
                            println("SUPLA: refresh nieudany (${e.message}), używam GATE_ACCESS_TOKEN")
                            cachedAccessToken = gateConfig.accessToken
                            expiresAt = Instant.DISTANT_FUTURE
                            gateConfig.accessToken!!
                        } else {
                            throw e
                        }
                    }
            }
            !gateConfig.accessToken.isNullOrBlank() -> {
                cachedAccessToken = gateConfig.accessToken
                expiresAt = Instant.DISTANT_FUTURE
                gateConfig.accessToken
            }
            else -> throw IllegalStateException(
                "Brak SUPLA tokenu. Ustaw SUPLA_REFRESH_TOKEN (OAuth z offline_access) " +
                    "lub użyj POST /gate/supla/exchange-code."
            )
        }
    }

    override suspend fun ensureFreshToken() {
        if (!canRefresh() && gateConfig.refreshToken.isNullOrBlank() && refreshToken.isNullOrBlank()) {
            return
        }
        val needsRefresh = mutex.withLock {
            ensureLoadedFromStore()
            val now = Clock.System.now()
            cachedAccessToken.isNullOrBlank() ||
                now >= expiresAt ||
                (expiresAt - now) < REFRESH_AHEAD
        }
        if (needsRefresh) {
            println("SUPLA: proaktywne odświeżenie access tokenu")
            // getAccessToken ma fallback env / GATE_ACCESS_TOKEN – w przeciwieństwie do samego refreshAccessTokenLocked
            getAccessToken(forceRefresh = true)
        }
    }

    override suspend fun storeTokens(
        accessToken: String,
        refreshToken: String?,
        expiresInSeconds: Long,
    ) = mutex.withLock {
        cachedAccessToken = accessToken
        expiresAt = Clock.System.now() + (expiresInSeconds - SKEW_SECONDS).coerceAtLeast(60).seconds
        if (!refreshToken.isNullOrBlank()) {
            this.refreshToken = refreshToken
        }
        persistLocked()
        loadedFromStore = true
    }

    private suspend fun ensureLoadedFromStore() {
        if (loadedFromStore) return
        val stored = runCatching { tokenRepo.load() }.getOrNull()
        if (stored != null) {
            if (!stored.accessToken.isNullOrBlank()) {
                cachedAccessToken = stored.accessToken
            }
            if (!stored.refreshToken.isNullOrBlank()) {
                refreshToken = stored.refreshToken
            }
            stored.accessTokenExpiresAtEpochMs?.let {
                expiresAt = Instant.fromEpochMilliseconds(it)
            }
            println("SUPLA: wczytano tokeny z bazy (refresh=${!stored.refreshToken.isNullOrBlank()})")
        } else if (!gateConfig.refreshToken.isNullOrBlank()) {
            refreshToken = gateConfig.refreshToken
            println("SUPLA: używam SUPLA_REFRESH_TOKEN z env (pierwszy start)")
        }
        loadedFromStore = true
    }

    private fun canRefresh(): Boolean =
        !gateConfig.clientId.isNullOrBlank() &&
            !gateConfig.clientSecret.isNullOrBlank() &&
            gateConfig.tokenUrl.isNotBlank()

    private suspend fun refreshAccessTokenLocked(): String {
        val currentRefresh = refreshToken
            ?: throw IllegalStateException("Brak refresh_token SUPLA")

        val response = client.post(gateConfig.tokenUrl) {
            contentType(ContentType.Application.Json)
            setBody(
                SuplaTokenRequest(
                    grantType = "refresh_token",
                    clientId = gateConfig.clientId!!,
                    clientSecret = gateConfig.clientSecret!!,
                    refreshToken = currentRefresh,
                )
            )
        }

        if (!response.status.isSuccess()) {
            val errorBody = runCatching { response.bodyAsText() }.getOrDefault("")
            val expired = errorBody.contains("invalid_grant", ignoreCase = true) ||
                errorBody.contains("expired", ignoreCase = true)
            throw IllegalStateException(
                if (expired) {
                    "SUPLA refresh_token wygasł (~30 dni bez odświeżenia). " +
                        "Wygeneruj nowy: włącz SUPLA_OAUTH_HELPER=true, " +
                        "otwórz /oauth/v2/auth, potem POST /gate/supla/exchange-code. " +
                        "Szczegóły: ${response.status} $errorBody".trim()
                } else {
                    "SUPLA token refresh failed ${response.status}: $errorBody".trim()
                }
            )
        }

        val body: SuplaTokenResponse = response.body()
        cachedAccessToken = body.accessToken
        expiresAt = Clock.System.now() + (body.expiresIn - SKEW_SECONDS).coerceAtLeast(60).seconds

        // SUPLA rotuje refresh_token – bez zapisu po restarcie stary przestaje działać
        if (!body.refreshToken.isNullOrBlank()) {
            refreshToken = body.refreshToken
        }

        persistLocked()
        println(
            "SUPLA: access token odświeżony, wygasa za ~${body.expiresIn}s" +
                if (!body.refreshToken.isNullOrBlank()) ", refresh_token zaktualizowany w DB" else ""
        )
        return body.accessToken
    }

    private suspend fun persistLocked() {
        tokenRepo.save(
            SuplaStoredTokens(
                accessToken = cachedAccessToken,
                refreshToken = refreshToken,
                accessTokenExpiresAtEpochMs = expiresAt.toEpochMilliseconds()
                    .takeIf { expiresAt != Instant.DISTANT_FUTURE && expiresAt != Instant.DISTANT_PAST },
            )
        )
    }

    companion object {
        private const val SKEW_SECONDS = 60L
        private val REFRESH_AHEAD = 5.minutes
    }
}

@Serializable
private data class SuplaTokenRequest(
    @SerialName("grant_type") val grantType: String,
    @SerialName("client_id") val clientId: String,
    @SerialName("client_secret") val clientSecret: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
)

@Serializable
private data class SuplaTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("expires_in") val expiresIn: Long = 3600,
    @SerialName("token_type") val tokenType: String? = null,
    val scope: String? = null,
    @SerialName("target_url") val targetUrl: String? = null,
)
