package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.modules.ApiConfig
import com.example.przyczepki_landingpage.service.SuplaTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

/**
 * Jednorazowa wymiana authorization code → tokens.
 * Tokeny trafiają od razu do MongoDB (kolekcja supla_token).
 * Włącz: SUPLA_OAUTH_HELPER=true. Po setupie wyłącz.
 */
fun Route.suplaOAuth() {
    val client by inject<HttpClient>()
    val apiConfig by inject<ApiConfig>()
    val tokenProvider by inject<SuplaTokenProvider>()

    route("/gate/supla") {
        post("/exchange-code") {
            val gate = apiConfig.gateConfig
            if (!gate.oauthHelperEnabled) {
                return@post call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "Endpoint wyłączony (ustaw SUPLA_OAUTH_HELPER=true)"),
                )
            }

            val request = call.receive<SuplaExchangeCodeRequest>()
            require(!gate.clientId.isNullOrBlank() && !gate.clientSecret.isNullOrBlank()) {
                "Brak SUPLA_CLIENT_ID / SUPLA_CLIENT_SECRET"
            }
            require(gate.redirectUri.isNotBlank()) { "Brak SUPLA_REDIRECT_URI" }

            val response = client.post(gate.tokenUrl) {
                contentType(ContentType.Application.Json)
                setBody(
                    SuplaAuthorizationCodeRequest(
                        grantType = "authorization_code",
                        clientId = gate.clientId,
                        clientSecret = gate.clientSecret,
                        redirectUri = gate.redirectUri,
                        code = request.code,
                    )
                )
            }

            if (!response.status.isSuccess()) {
                val errorBody = runCatching { response.bodyAsText() }.getOrDefault("")
                return@post call.respond(
                    HttpStatusCode.BadGateway,
                    mapOf("error" to "SUPLA exchange failed: ${response.status} $errorBody".trim()),
                )
            }

            val tokens: SuplaExchangeCodeResponse = response.body()
            tokenProvider.storeTokens(
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken,
                expiresInSeconds = tokens.expiresIn,
            )

            call.respond(
                SuplaExchangeCodeResult(
                    message = "Tokeny zapisane w MongoDB. Ustaw SUPLA_OAUTH_HELPER=false. " +
                        "SUPLA_REFRESH_TOKEN w .env jest tylko fallbackiem przy pustej bazie.",
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken,
                    expiresIn = tokens.expiresIn,
                    scope = tokens.scope,
                    targetUrl = tokens.targetUrl,
                )
            )
        }
    }
}

@Serializable
data class SuplaExchangeCodeRequest(
    val code: String,
)

@Serializable
data class SuplaAuthorizationCodeRequest(
    @SerialName("grant_type") val grantType: String,
    @SerialName("client_id") val clientId: String,
    @SerialName("client_secret") val clientSecret: String,
    @SerialName("redirect_uri") val redirectUri: String,
    val code: String,
)

@Serializable
data class SuplaExchangeCodeResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("expires_in") val expiresIn: Long = 3600,
    val scope: String? = null,
    @SerialName("target_url") val targetUrl: String? = null,
)

@Serializable
data class SuplaExchangeCodeResult(
    val message: String,
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("expires_in") val expiresIn: Long,
    val scope: String? = null,
    @SerialName("target_url") val targetUrl: String? = null,
)
