package com.example.przyczepki_landingpage.modules

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.przyczepki_landingpage.service.auth.TokenType
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import org.koin.ktor.ext.getKoin

fun Application.configureSecurity() {

    val koin = getKoin()
    val config = koin.get<ApiConfig>().auth

    val algorithm = Algorithm.HMAC256(config.secretAuth)

    install(Authentication) {
        jwt {
            verifier(
                JWT
                    .require(algorithm)
                    .withIssuer(config.issuer)
//                    .withAudience(config.audience)
                    .withClaim("type", TokenType.ACCESS.name)
                    .build()
            )

            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()

                if (!userId.isNullOrBlank()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}

