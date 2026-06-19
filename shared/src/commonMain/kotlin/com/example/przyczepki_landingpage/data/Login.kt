package com.example.przyczepki_landingpage.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)


@Serializable
data class LoginResponse(
    val token: String,
    val refreshToken: String? = null,
    val customerId: String,
)