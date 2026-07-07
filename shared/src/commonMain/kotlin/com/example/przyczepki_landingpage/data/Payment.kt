package com.example.przyczepki_landingpage.data

import kotlinx.serialization.Serializable

@Serializable
data class P24TransactionRegisterRequest(
    val merchantId: Int,
    val posId: Int,
    val sessionId: String,
    val amount: Int,
    val currency: String = "PLN",
    val description: String = "Rezerwacja przyczepki",
    val email: String,
    val client: String? = null,
    val address: String? = null,
    val country: String = "PL",
    val phone: String? = null,
    val language: String = "pl",
    val method: Int? = null,
    val urlReturn: String = "https://przyczepki.pl/reservation/summary",
    val urlStatus: String = "https://przyczepki.pl/payment/notification",
    val timeLimit: Int? = 90,
    val channel: Int? = 16,
    val regulationAccept: Boolean? = true,
    val transferLabel: String = "$email, $sessionId",
    val sign: String,
    val encoding: String = "UTF-8",
)

@Serializable
data class P24TransactionRegisterResponse(
    val data: P24RegisterData? = null,
    val responseCode: Int,
    val error: String? = null
)

@Serializable
data class P24RegisterData(
    val token: String
)

@Serializable
data class P24TransactionVerifyRequest(
    val merchantId: Int,
    val posId: Int,
    val sessionId: String,
    val amount: Int,
    val currency: String = "PLN",
    val orderId: Long,
    val sign: String
)

@Serializable
data class P24Notification(
    val merchantId: Int,
    val posId: Int,
    val sessionId: String,
    val amount: Int,
    val originAmount: Int? = null,
    val currency: String,
    val orderId: Long,
    val methodId: Int? = null,
    val statement: String? = null,
    val sign: String
)

@Serializable
data class PaymentRegisterRequest(
    val amount: Int,
    val email: String,
    val description: String = "Kaucja rezerwacyjna",
)

@Serializable
data class PaymentRegisterResponse(
    val token: String,
    val redirectUrl: String,
)