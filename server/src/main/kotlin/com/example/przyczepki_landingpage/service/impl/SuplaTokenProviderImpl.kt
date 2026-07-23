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
import java.io.File
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Automatyczne odświeżanie tokenów SUPLA OAuth:
 * 1. access token w cache (~1h), odnawiany przed wygaśnięciem
 * 2. refresh token rotowany przez SUPLA – zapisywany w MongoDB (przeżywa restart)
 * 3. przy invalid_grant: reload z Mongo → live odczyt .env → GATE_ACCESS_TOKEN
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
            !refreshToken.isNullOrBlank() && canRefresh() -> refreshWithFallbackLocked()
            !gateConfig.accessToken.isNullOrBlank() -> useStaticAccessTokenLocked()
            else -> throw IllegalStateException(
                "Brak SUPLA tokenu. Ustaw SUPLA_REFRESH_TOKEN (OAuth z offline_access) " +
                    "lub użyj POST /gate/supla/exchange-code."
            )
        }
    }

    override suspend fun ensureFreshToken() {
        if (!canRefresh() && readLiveRefreshToken().isNullOrBlank() && refreshToken.isNullOrBlank()) {
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

    private suspend fun refreshWithFallbackLocked(): String {
        val firstError = runCatching { refreshAccessTokenLocked() }.exceptionOrNull()
        if (firstError == null) {
            return cachedAccessToken
                ?: throw IllegalStateException("SUPLA refresh OK, ale brak access_token w cache")
        }

        // 1) Mongo mogło zostać zaktualizowane poza procesem (np. ręczna naprawa)
        val previousRefresh = refreshToken
        reloadFromStoreLocked()
        if (!refreshToken.isNullOrBlank() && refreshToken != previousRefresh && canRefresh()) {
            println("SUPLA: retry z refresh_token przeładowanym z Mongo")
            runCatching { return refreshAccessTokenLocked() }
        }

        // 2) Ważny access z Mongo – użyj go zamiast padać na wygasłym refresh
        val cached = cachedAccessToken
        if (cached != null && Clock.System.now() < expiresAt) {
            println("SUPLA: refresh nieudany, używam wciąż ważnego access tokenu z Mongo")
            return cached
        }

        // 3) Live .env / getenv (gateConfig jest zamrożony od startu procesu)
        val envRefresh = readLiveRefreshToken()
        if (!envRefresh.isNullOrBlank() && envRefresh != refreshToken && canRefresh()) {
            println("SUPLA: retry z SUPLA_REFRESH_TOKEN z .env/getenv")
            refreshToken = envRefresh
            runCatching { return refreshAccessTokenLocked() }
        }

        if (!gateConfig.accessToken.isNullOrBlank()) {
            println("SUPLA: refresh nieudany (${firstError.message}), używam GATE_ACCESS_TOKEN")
            return useStaticAccessTokenLocked()
        }

        throw firstError
    }

    private fun useStaticAccessTokenLocked(): String {
        cachedAccessToken = gateConfig.accessToken
        expiresAt = Instant.DISTANT_FUTURE
        return gateConfig.accessToken!!
    }

    private suspend fun ensureLoadedFromStore() {
        if (loadedFromStore) return
        reloadFromStoreLocked()
        loadedFromStore = true
    }

    private suspend fun reloadFromStoreLocked() {
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
        } else {
            val envRefresh = readLiveRefreshToken()
            if (!envRefresh.isNullOrBlank()) {
                refreshToken = envRefresh
                println("SUPLA: używam SUPLA_REFRESH_TOKEN z env (brak dokumentu w DB)")
            }
        }
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
                    "SUPLA refresh_token wygasł lub został zrotowany. " +
                        "Zrestartuj API po aktualizacji tokenów w Mongo/.env, " +
                        "albo wygeneruj nowy (SUPLA_OAUTH_HELPER + exchange-code). " +
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

        /**
         * gateConfig/System.getenv są zamrożone od startu procesu (Gradle wstrzykuje .env raz).
         * Przy awarii czytamy plik .env z dysku, żeby podjąć świeży SUPLA_REFRESH_TOKEN bez restartu.
         */
        fun readLiveRefreshToken(): String? {
            val fromFile = sequenceOf(
                File(".env"),
                File("../.env"),
                File("../../.env"),
            ).mapNotNull { file ->
                if (!file.isFile) return@mapNotNull null
                file.readLines()
                    .map { it.trim() }
                    .firstOrNull { it.startsWith("SUPLA_REFRESH_TOKEN=") }
                    ?.substringAfter("=", "")
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }
            }.firstOrNull()

            return fromFile
                ?: System.getenv("SUPLA_REFRESH_TOKEN")?.takeIf { it.isNotBlank() }
        }
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
