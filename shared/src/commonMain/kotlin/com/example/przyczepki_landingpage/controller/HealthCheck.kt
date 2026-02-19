package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.model.ServerResponse
import com.example.przyczepki_landingpage.model.ServerStatus
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.util.network.UnresolvedAddressException

class HealthCheck( private val client: HttpClient) {

    suspend fun healthCheck(): ServerResponse {
        return try {

            val response = client.get(base_url)

            when (response.status.value) {

                200 -> ServerResponse(
                    ServerStatus.OK,
                    "✅ Server is running"
                )

                401 -> ServerResponse(
                    ServerStatus.UNAUTHORIZED,
                    "🔐 Unauthorized"
                )

                403 -> ServerResponse(
                    ServerStatus.FORBIDDEN,
                    "⛔ Forbidden"
                )

                404 -> ServerResponse(
                    ServerStatus.NOT_FOUND,
                    "📭 Endpoint not found"
                )

                in 500..599 -> ServerResponse(
                    ServerStatus.SERVER_ERROR,
                    "💥 Server error: ${response.status}"
                )

                else -> ServerResponse(
                    ServerStatus.UNEXPECTED_STATUS,
                    "⚠️ Unexpected status: ${response.status}"
                )
            }

        } catch (e: HttpRequestTimeoutException) {
            println("HealthCheck error: ${e.message}")

            ServerResponse(
                ServerStatus.TIMEOUT,
                "⏳ Request timeout"
            )

        } catch (e: Exception) {

            val message = e.message ?: "Unknown error"
            println("HealthCheck error: $message")

            // fetch API – brak połączenia / CORS / backend off
            if (message.contains("Failed to fetch", ignoreCase = true)) {

                ServerResponse(
                    ServerStatus.UNREACHABLE,
                    "❌ Server unreachable (connection refused / CORS)"
                )

            } else {

                ServerResponse(
                    ServerStatus.UNEXPECTED_STATUS,
                    "⚠️ Network error: $message"
                )
            }
        }
    }

}