package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.PaymentRegisterRequest
import com.example.przyczepki_landingpage.data.PaymentRegisterResponse
import com.example.przyczepki_landingpage.data.PaymentStatusResponse
import com.example.przyczepki_landingpage.data.ReservationDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class PaymentController(private val client: HttpClient) {

    suspend fun registerPayment(
        amount: Int,
        customer: Customer,
        reservation: ReservationDto,
        description: String = "Kaucja rezerwacyjna",
        regulationAccept: Boolean,
    ): Result<PaymentRegisterResponse> {
        return try {
            val response = client.post("$base_url/payment/register") {
                setBody(
                    PaymentRegisterRequest(
                        amount = amount,
                        customer = customer,
                        reservation = reservation,
                        description = description,
                        regulationAccept = regulationAccept,
                    )
                )
            }
            if (!response.status.isSuccess()) {
                val errorBody = runCatching { response.bodyAsText() }.getOrDefault("")
                return Result.failure(Exception(parseApiError(errorBody, response.status.value)))
            }
            Result.success(response.body<PaymentRegisterResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPaymentStatus(sessionId: String): Result<PaymentStatusResponse> {
        return try {
            val response = client.get("$base_url/payment/status/$sessionId")
            if (!response.status.isSuccess()) {
                val errorBody = runCatching { response.bodyAsText() }.getOrDefault("")
                return Result.failure(Exception(parseApiError(errorBody, response.status.value)))
            }
            Result.success(response.body<PaymentStatusResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseApiError(body: String, statusCode: Int): String {
        val jsonError = Regex(""""error"\s*:\s*"([^"]+)"""").find(body)?.groupValues?.get(1)
        return jsonError ?: body.ifBlank { "Błąd płatności (HTTP $statusCode)" }
    }
}
