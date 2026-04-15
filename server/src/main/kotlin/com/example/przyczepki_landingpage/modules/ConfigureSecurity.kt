package com.example.przyczepki_landingpage.modules

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity() {
    install(Authentication) {
        jwt {
            verifier(
                JWT
                    .require(Algorithm.HMAC256("secret"))
                    .withIssuer("ktor-app")
                    .build()
            )

            validate { credential ->
                if (credential.payload.getClaim("id").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}