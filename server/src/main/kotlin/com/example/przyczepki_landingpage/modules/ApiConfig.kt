package com.example.przyczepki_landingpage.modules

import io.ktor.server.config.ApplicationConfig
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

fun ApplicationConfig.toApiConfig(): ApiConfig {
    fun str(path: String) = propertyOrNull(path)?.getString()
    fun int(path: String) = str(path)?.toIntOrNull()
    fun long(path: String) = str(path)?.toLongOrNull()

    return ApiConfig(
        env = str("api.env") ?: "DEV",

        email = EmailConfig(
            host = str("api.email.host") ?: "localhost",
            port = int("api.email.port") ?: 8200
        ),

        db = DbConfig(
            host = str("db.host") ?: "localhost", //""przyczepki_db",
            port = int("db.port") ?: 27017,
            name = str("db.name") ?: "przyczepki",
            user = str("db.user") ?: "admin",
            password = str("db.password") ?: "admin",
            authSource = str("db.authSource") ?: "admin"

        ),

        auth = AuthConfig(
            secretAuth = str("auth.secretAuth") ?: "secretAuth",
            secretRefresh = str("auth.secretRefresh") ?: "secretRefresh",
            issuer = str("auth.issuer") ?: "ktor",
            audience = str("auth.audience") ?: "ktor",
            realm = str("auth.realm") ?: "ktor",
            accessTokenExpiry = long("auth.accessTokenExpiry") ?: 3600000,
            refreshTokenExpiry = long("auth.refreshTokenExpiry") ?: 2592000000,
            googleClientId = str("auth.googleClientId") ?: "",
            claim = str("auth.claim") ?: "userId"
        )
    )
}