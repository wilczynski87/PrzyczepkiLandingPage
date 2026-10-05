package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.OpenGateRequest
import com.example.przyczepki_landingpage.data.OpenGateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class GateController(private val client: HttpClient) {

    suspend fun openGate(reservationId: String? = null): Result<OpenGateResponse> {
        return try {
            val response = client.post("$base_url/gate/open") {
                setBody(OpenGateRequest(reservationId = reservationId))
            }
            when {
                response.status.isSuccess() -> Result.success(response.body())
                response.status.value == 401 || response.status.value == 403 -> {
                    val body = runCatching { response.bodyAsText() }.getOrDefault("")
                    val message = parseError(body)
                    if (response.status.value == 401) {
                        Result.failure(InvalidLoginCredentialsException())
                    } else {
                        Result.failure(Exception(message))
                    }
                }
                else -> Result.failure(Exception(parseError(runCatching { response.bodyAsText() }.getOrDefault(""))))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseError(body: String): String =
        Regex(""""error"\s*:\s*"([^"]+)"""").find(body)?.groupValues?.get(1)
            ?: body.ifBlank { "Nie udało się otworzyć bramy" }
}
