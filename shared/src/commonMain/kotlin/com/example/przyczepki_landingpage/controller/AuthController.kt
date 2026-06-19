package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.data.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthController(private val client: HttpClient) {

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response = client.post("$base_url/auth/login") {
                setBody(loginRequest)
            }.body<LoginResponse>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
