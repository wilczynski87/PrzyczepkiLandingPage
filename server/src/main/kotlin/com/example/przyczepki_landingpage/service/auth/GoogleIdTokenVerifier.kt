package com.example.przyczepki_landingpage.service.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class GoogleIdTokenVerifier(
    private val httpClient: HttpClient,
    private val allowedClientIds: List<String>,
) {
    suspend fun verifyAndExtractEmail(idToken: String): String {
        if (idToken.isBlank()) {
            throw IllegalArgumentException("Brak tokenu Google")
        }
        if (allowedClientIds.isEmpty()) {
            throw IllegalStateException("Logowanie Google nie jest skonfigurowane")
        }

        val response = httpClient.get("https://oauth2.googleapis.com/tokeninfo") {
            parameter("id_token", idToken)
        }
        if (!response.status.isSuccess()) {
            throw IllegalArgumentException("Invalid Google token")
        }

        val info = response.body<GoogleTokenInfo>()
        val email = info.email?.trim()?.lowercase().orEmpty()
        if (email.isBlank() || !info.isEmailVerified()) {
            throw IllegalArgumentException("Konto Google nie ma zweryfikowanego adresu e-mail")
        }

        val audience = info.aud?.trim().orEmpty()
        if (audience.isBlank() || allowedClientIds.none { it == audience }) {
            throw IllegalArgumentException("Invalid Google token")
        }

        val issuer = info.iss?.trim().orEmpty()
        if (issuer != "https://accounts.google.com" && issuer != "accounts.google.com") {
            throw IllegalArgumentException("Invalid Google token")
        }

        return email
    }
}

@Serializable
private data class GoogleTokenInfo(
    val aud: String? = null,
    val iss: String? = null,
    val email: String? = null,
    @SerialName("email_verified")
    val emailVerified: JsonElement? = null,
) {
    fun isEmailVerified(): Boolean {
        val value = emailVerified ?: return false
        val primitive = runCatching { value.jsonPrimitive }.getOrNull() ?: return false
        return primitive.booleanOrNull == true ||
            primitive.contentOrNull.equals("true", ignoreCase = true)
    }
}
