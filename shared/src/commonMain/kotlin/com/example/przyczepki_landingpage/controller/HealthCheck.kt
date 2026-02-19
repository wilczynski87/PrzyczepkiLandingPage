package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.model.ServerResponse
import com.example.przyczepki_landingpage.model.ServerStatus
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class HealthCheck( private val client: HttpClient) {

    suspend fun healthCheck(): ServerResponse {
        try {
            val response = client.get(base_url)
//            println("healthCheck: $response")

            return when (response.status.value) {
                200 -> ServerResponse(ServerStatus.OK, "✅ Server is running")

                401 -> ServerResponse(ServerStatus.UNAUTHORIZED,"🔐 Unauthorized (401)")

                403 -> ServerResponse(ServerStatus.FORBIDDEN,"⛔ Forbidden (403)")

                in 500..599 -> ServerResponse(ServerStatus.SERVER_ERROR,"💥 Server error: ${response.status}")

                else -> ServerResponse(ServerStatus.UNEXPECTED_STATUS,"⚠️ Unexpected status: ${response.status}")
            }

        } catch (e: Exception) {
            return ServerResponse(ServerStatus.UNEXPECTED_STATUS,"❌ Server unreachable: ${e.message}")
        }
    }
}