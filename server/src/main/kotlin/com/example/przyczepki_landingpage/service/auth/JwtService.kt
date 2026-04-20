package com.example.przyczepki_landingpage.service.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.JWTVerifier
import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.modules.AuthConfig
import kotlinx.serialization.Serializable
import java.util.Date

class JwtService(
    private val config: AuthConfig
) {
    private val algorithm = Algorithm.HMAC256(config.secretAuth!!)
    private val refreshAlgorithm = Algorithm.HMAC256(config.secretRefresh!!)

    val accessTokenVerifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(config.issuer)
        .withClaim("type", TokenType.ACCESS.name)
        .build()

    val refreshTokenVerifier: JWTVerifier = JWT.require(refreshAlgorithm)
        .withIssuer(config.issuer)
        .withClaim("type", TokenType.REFRESH.name)
        .build()

    fun generateToken(customer: Customer): String =
        JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withClaim("userId", customer.id)
            .withClaim("type", TokenType.ACCESS.name)
            .withExpiresAt(Date(System.currentTimeMillis() + config.accessTokenExpiry))
            .sign(algorithm)

    fun generateRefreshToken(customer: Customer): String =
        JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withClaim("userId", customer.id)
            .withClaim("type", TokenType.REFRESH.name)
            .withExpiresAt(Date(System.currentTimeMillis() + config.refreshTokenExpiry))
            .sign(refreshAlgorithm)

    fun verifyRefreshToken(token: String): TokenValidationResult =
        try {
            val decoded = refreshTokenVerifier.verify(token)

            TokenValidationResult(
                isValid = true,
                userId = decoded.getClaim("userId").asString()
            )
        } catch (ex: JWTVerificationException) {
            TokenValidationResult(false, error = ex.message)
        }
}

//object JwtService {
//    private const val secret = "secret"
//    private const val secretRefresh = "secretRefresh"
//    private const val issuer = "ktor-app"
//
//    private val algorithm = Algorithm.HMAC256(secret)
//    private val refreshAlgorithm = Algorithm.HMAC256(secret)
//
//    val refreshTokenVerifier: JWTVerifier = JWT.require(refreshAlgorithm)
//        .withIssuer(issuer)
//        .withClaimPresence("userId")
//        .build()
//
//    fun generateToken(customer: Customer): String {
//        return JWT.create()
//            .withIssuer(issuer)
//            .withClaim("userId", customer.id)
//            .withClaim("type", TokenType.ACCESS.name)
//            .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 15))
//            .sign(algorithm)
//    }
//    fun generateRefreshToken(customer: Customer): String {
//        return JWT.create()
//            .withIssuer(issuer)
//            .withClaim("userId", customer.id)
//            .withClaim("type", TokenType.REFRESH.name)
//            .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
//            .sign(algorithm)
//    }
//
//    fun verifyRefreshToken(token: String): TokenValidationResult {
//        return try {
//            val decodedJWT = refreshTokenVerifier.verify(token)
//            // Dodatkowa walidacja business logic
//            val tokenType = decodedJWT.getClaim("type").asString()
//            println("tokenType: $tokenType")
//            if (tokenType != TokenType.REFRESH.name) {
//                return TokenValidationResult(false, error = "Invalid token type")
//            }
//
//            TokenValidationResult(
//                isValid = true,
//                userId = decodedJWT.getClaim("userId").asString()
//            )
//        } catch (ex: JWTVerificationException) {
//            TokenValidationResult(
//                isValid = false,
//                error = ex.message
//            )
//        }
//    }
//}

@Serializable
data class TokenValidationResult(
    val isValid: Boolean,
    val userId: String? = null,
    val error: String? = null
)

enum class TokenType {
    ACCESS, REFRESH
}

