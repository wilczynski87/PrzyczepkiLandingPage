package com.example.przyczepki_landingpage.service.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.przyczepki_landingpage.data.Customer
import java.util.Date

object JwtService {
    private const val secret = "secret"
    private const val issuer = "ktor-app"

    fun generate(customer: Customer): String {
        return JWT.create()
            .withIssuer(issuer)
            .withClaim("id", customer.id)
            .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 15))
            .sign(Algorithm.HMAC256(secret))
    }
    fun generateRefreshToken(customer: Customer): String {
        return JWT.create()
            .withIssuer(issuer)
            .withClaim("id", customer.id)
            .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
            .sign(Algorithm.HMAC256(secret))
    }
}