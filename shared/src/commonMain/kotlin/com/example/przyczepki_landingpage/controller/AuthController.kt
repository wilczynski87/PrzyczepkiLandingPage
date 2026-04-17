package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.LoginRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import kotlinx.serialization.Serializable

class AuthController(private val client: HttpClient) {

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> = client.post("/auth/login") {
        setBody(loginRequest)
    }.body()
}

@Serializable
data class LoginResponse(
    val token: String,
    val refreshToken: String? = null,
    val customerId: String,
)