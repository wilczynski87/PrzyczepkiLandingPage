package com.example.przyczepki_landingpage.modules

import kotlinx.serialization.Serializable

@Serializable
data class EmailConfig(
    val host: String,
    val port: Int
)
@Serializable
data class DbConfig(
    val host: String,
    val port: Int,
    val name: String,
    val user: String,
    val password: String,
    val authSource: String,
) {
    fun toConnectionString(): String {
        return "mongodb://$user:$password@$host:$port/$name?authSource=$authSource"
    }
}
@Serializable
data class AuthConfig(
    val secretAuth: String?,
    val secretRefresh: String?,
    val issuer: String,
    val audience: String,
    val realm: String,
    val accessTokenExpiry: Long = 3600000,
    val refreshTokenExpiry: Long = 2592000000,
    val claim: String,
    val internalApiKey: String,
)

@Serializable
data class GateConfig(
    val openUrl: String,
    val method: String = "PATCH",
    /** Stały access token (PAT) lub początkowy OAuth access token. */
    val accessToken: String? = null,
    /** OAuth refresh token – backend odświeża access token automatycznie. */
    val refreshToken: String? = null,
    val clientId: String? = null,
    val clientSecret: String? = null,
    val tokenUrl: String = "https://svr111.supla.org/oauth/v2/token",
    val redirectUri: String = "",
    /** Włącza POST /gate/supla/exchange-code (tylko do jednorazowego setupu). */
    val oauthHelperEnabled: Boolean = false,
    /** Opcjonalny JSON body, np. {"action":"OPEN_CLOSE"} dla SUPLA. */
    val requestBody: String? = null,
    val mockMode: Boolean = false,
    val cooldownSeconds: Long = 60,
)

@Serializable
data class PaymentConfig(
    val merchantId: Int,
    val posId: Int,
    val secretId: String,
    val crc: String,
    val urlReturn: String,
    val urlStatus: String,
    val apiBaseUrl: String,
    val redirectBaseUrl: String,
    val mockMode: Boolean = false,
)

@Serializable
data class ApiConfig(
    val env: String,
    val apiPort: Int,
    val apiHost: String,
    val email: EmailConfig,
    val db: DbConfig,
    val auth: AuthConfig,
    val paymentConfig: PaymentConfig,
    val gateConfig: GateConfig,
)

/**
 * Źródło konfiguracji: zmienne środowiskowe (patrz docker-compose.yaml).
 * API_ENV, EMAIL_HOST, EMAIL_PORT, DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD, DB_AUTH_SOURCE,
 * AUTH_SECRET_LOGIN, AUTH_SECRET_REFRESH, AUTH_ISSUER, AUTH_AUDIENCE, AUTH_REALM,
 * AUTH_ACCESS_TOKEN_EXPIRY, AUTH_REFRESH_TOKEN_EXPIRY, AUTH_GOOGLE_CLIENT_ID, AUTH_CLAIM
 */
fun toApiConfig(): ApiConfig {

    return ApiConfig(
        env = System.getenv("API_ENV") ?: "DEV",
        apiPort = System.getenv("API_PORT")?.toIntOrNull() ?: 8090,
        apiHost = System.getenv("API_HOST") ?: "localhost",

        email = EmailConfig(
            host = System.getenv("EMAIL_HOST") ?: "localhost",
            port = System.getenv("EMAIL_PORT")?.toIntOrNull() ?: 8091
        ),

        db = DbConfig(
            host = System.getenv("DB_HOST") ?: "localhost", //""przyczepki_db",
            port = System.getenv("DB_PORT")?.toIntOrNull() ?: 27017,
            name = System.getenv("DB_NAME") ?: "przyczepki",
            user = System.getenv("DB_USER") ?: "admin",
            password = System.getenv("DB_PASSWORD") ?: throw NullPointerException("DB_PASSWORD is missing"),
            authSource = System.getenv("DB_AUTH_SOURCE") ?: throw NullPointerException("DB_AUTH_SOURCE is missing"),
        ),

        auth = AuthConfig(
            secretAuth = System.getenv("AUTH_SECRET_LOGIN") ?: throw NullPointerException("AUTH_SECRET_LOGIN is missing"),
            secretRefresh = System.getenv("AUTH_SECRET_REFRESH") ?: throw NullPointerException("AUTH_SECRET_REFRESH is missing"),
            issuer = System.getenv("AUTH_ISSUER") ?: throw NullPointerException("AUTH_ISSUER is missing"),
            audience = System.getenv("AUTH_AUDIENCE") ?: throw NullPointerException("AUTH_AUDIENCE is missing"),
            realm = System.getenv("AUTH_REALM") ?: throw NullPointerException("AUTH_REALM is missing"),
            accessTokenExpiry = System.getenv("AUTH_ACCESS_TOKEN_EXPIRY")?.toLongOrNull() ?: 3600000,
            refreshTokenExpiry = System.getenv("AUTH_REFRESH_TOKEN_EXPIRY")?.toLongOrNull() ?: 2592000000,
            internalApiKey = System.getenv("INTERNAL_API_KEY") ?: throw NullPointerException("INTERNAL_API_KEY is missing"),
            claim = System.getenv("AUTH_CLAIM") ?: throw NullPointerException("AUTH_CLAIM is missing"),
        ),

        paymentConfig = readPaymentConfig(),
        gateConfig = readGateConfig(),
    )
}

private fun readGateConfig(): GateConfig {
    val isDev = (System.getenv("API_ENV") ?: "DEV").equals("DEV", ignoreCase = true)
    val openUrl = System.getenv("GATE_OPEN_URL") ?: ""
    val mockMode = System.getenv("GATE_MOCK")?.toBooleanStrictOrNull()
        ?: (isDev || openUrl.isBlank())

    val accessToken = System.getenv("GATE_ACCESS_TOKEN")
        ?: System.getenv("GATE_API_KEY")

    val clientId = System.getenv("SUPLA_CLIENT_ID")
        ?: System.getenv("SUPLA_ID")
    val clientSecret = System.getenv("SUPLA_CLIENT_SECRET")
        ?: System.getenv("SUPLA_SECRET")

    val oauthHelperEnabled = System.getenv("SUPLA_OAUTH_HELPER")?.toBooleanStrictOrNull()
        ?: isDev

    return GateConfig(
        openUrl = openUrl,
        method = System.getenv("GATE_OPEN_METHOD") ?: "PATCH",
        accessToken = accessToken,
        refreshToken = System.getenv("SUPLA_REFRESH_TOKEN"),
        clientId = clientId,
        clientSecret = clientSecret,
        tokenUrl = System.getenv("SUPLA_TOKEN_URL")
            ?: "https://svr111.supla.org/oauth/v2/token",
        redirectUri = System.getenv("SUPLA_REDIRECT_URI") ?: "",
        oauthHelperEnabled = oauthHelperEnabled,
        requestBody = System.getenv("GATE_REQUEST_BODY") ?: """{"action":"OPEN_CLOSE"}""",
        mockMode = mockMode,
        cooldownSeconds = System.getenv("GATE_COOLDOWN_SECONDS")?.toLongOrNull() ?: 60,
    )
}

private fun readPaymentConfig(): PaymentConfig {
    val isDev = (System.getenv("API_ENV") ?: "DEV").equals("DEV", ignoreCase = true)

    fun envOrDev(name: String, devDefault: String): String =
        System.getenv(name) ?: if (isDev) devDefault else throw NullPointerException("$name is missing")

    fun intEnvOrDev(name: String, devDefault: Int): Int =
        System.getenv(name)?.toIntOrNull()
            ?: if (isDev) devDefault else throw NullPointerException("$name is missing")

    val paymentEnv = System.getenv("PAYMENT_ENV")
        ?: if (isDev) "sandbox" else "production"
    val paymentHost = when (paymentEnv.lowercase()) {
        "sandbox", "dev", "test" -> "https://sandbox.przelewy24.pl"
        "production", "prod", "live" -> "https://secure.przelewy24.pl"
        else -> throw IllegalArgumentException("Nieprawidłowe PAYMENT_ENV: $paymentEnv (użyj: sandbox lub production)")
    }

    val merchantId = intEnvOrDev("PAYMENT_MERCHANT_ID", 0)
    val posId = intEnvOrDev("PAYMENT_POS_ID", 0)
    val secretId = envOrDev("PAYMENT_SECRET_ID", "dev-secret")
    val crc = envOrDev("PAYMENT_CRC", "dev-crc")
    val mockMode = System.getenv("PAYMENT_MOCK")?.toBooleanStrictOrNull()
        ?: (isDev && merchantId == 0 && secretId == "dev-secret" && crc == "dev-crc")

    return PaymentConfig(
        merchantId = merchantId,
        posId = posId,
        secretId = secretId,
        crc = crc,
        urlReturn = envOrDev("PAYMENT_URL_RETURN", "/podsumowanieRezerwacji"),
        urlStatus = envOrDev("PAYMENT_URL_STATUS", "https://przyczepkifat.pl/api/payment/notification"),
        apiBaseUrl = "$paymentHost/api/v1",
        redirectBaseUrl = "$paymentHost/trnRequest/",
        mockMode = mockMode,
    )
}