package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.data.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class InvalidLoginCredentialsException : Exception()

class AuthController(private val client: HttpClient) {

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response = client.post("$base_url/auth/login") {
                setBody(loginRequest)
            }
            when (response.status.value) {
                in 200..299 -> Result.success(response.body())
                401, 403 -> Result.failure(InvalidLoginCredentialsException())
                else -> Result.failure(Exception("Server error: ${response.status}"))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}
