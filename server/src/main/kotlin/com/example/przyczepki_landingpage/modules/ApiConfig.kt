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
    val googleClientId: String,
    val claim: String,
)
@Serializable
data class ApiConfig(
    val env: String,
    val email: EmailConfig,
    val db: DbConfig,
    val auth: AuthConfig,
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

        email = EmailConfig(
            host = System.getenv("EMAIL_HOST") ?: "localhost",
            port = System.getenv("EMAIL_PORT")?.toIntOrNull() ?: 8200
        ),

        db = DbConfig(
            host = System.getenv("DB_HOST") ?: "localhost", //""przyczepki_db",
            port = System.getenv("DB_PORT")?.toIntOrNull() ?: 27017,
            name = System.getenv("DB_NAME") ?: "przyczepki",
            user = System.getenv("DB_USER") ?: "admin",
            password = System.getenv("DB_PASSWORD") ?: "admin",
            authSource = System.getenv("DB_AUTH_SOURCE") ?: "admin"
        ),

        auth = AuthConfig(
            secretAuth = System.getenv("AUTH_SECRET_LOGIN") ?: "secretAuth",
            secretRefresh = System.getenv("AUTH_SECRET_REFRESH") ?: "secretRefresh",
            issuer = System.getenv("AUTH_ISSUER") ?: "ktor",
            audience = System.getenv("AUTH_AUDIENCE") ?: "ktor",
            realm = System.getenv("AUTH_REALM") ?: "ktor",
            accessTokenExpiry = System.getenv("AUTH_ACCESS_TOKEN_EXPIRY")?.toLongOrNull() ?: 3600000,
            refreshTokenExpiry = System.getenv("AUTH_REFRESH_TOKEN_EXPIRY")?.toLongOrNull() ?: 2592000000,
            googleClientId = System.getenv("AUTH_GOOGLE_CLIENT_ID") ?: "",
            claim = System.getenv("AUTH_CLAIM") ?: "userId"
        )
    )
}