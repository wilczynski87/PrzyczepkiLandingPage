package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.PaymentRegisterRequest
import com.example.przyczepki_landingpage.data.PaymentRegisterResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class PaymentController(private val client: HttpClient) {

    suspend fun registerPayment(
        amount: Int,
        customer: Customer,
        description: String = "Kaucja rezerwacyjna",
        regulationAccept: Boolean,
    ): Result<PaymentRegisterResponse> {
        return try {
            val response = client.post("$base_url/payment/register") {
                setBody(PaymentRegisterRequest(amount = amount, customer = customer, description = description, regulationAccept = regulationAccept))
            }.body<PaymentRegisterResponse>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
