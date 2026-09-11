package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.GoogleAuthConfigResponse
import com.example.przyczepki_landingpage.data.GoogleOAuthRequest
import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.data.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText

class InvalidLoginCredentialsException : Exception()

class GoogleAccountNotFoundException : Exception()

class GoogleAuthException(message: String) : Exception(message)

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

    suspend fun getGoogleConfig(): Result<GoogleAuthConfigResponse> {
        return try {
            val response = client.get("$base_url/auth/google-config")
            if (response.status.value in 200..299) {
                Result.success(response.body())
            } else {
                Result.failure(GoogleAuthException("Logowanie Google nie jest dostępne"))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<LoginResponse> {
        return try {
            val response = client.post("$base_url/auth/google") {
                setBody(GoogleOAuthRequest(idToken = idToken))
            }
            when (response.status.value) {
                in 200..299 -> Result.success(response.body())
                404 -> Result.failure(GoogleAccountNotFoundException())
                401, 403 -> Result.failure(
                    GoogleAuthException("Nie udało się zweryfikować konta Google. Spróbuj ponownie.")
                )
                else -> {
                    val details = runCatching { response.bodyAsText() }.getOrNull()
                    Result.failure(
                        GoogleAuthException(
                            details?.takeIf { it.isNotBlank() }
                                ?: "Błąd logowania Google: ${response.status}",
                        )
                    )
                }
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}
