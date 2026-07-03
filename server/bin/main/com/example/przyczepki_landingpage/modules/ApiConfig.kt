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
            claim = System.getenv("AUTH_CLAIM") ?: throw NullPointerException("AUTH_CLAIM is missing"),
        )
    )
}