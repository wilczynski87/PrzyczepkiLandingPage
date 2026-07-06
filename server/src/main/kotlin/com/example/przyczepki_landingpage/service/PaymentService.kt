package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.P24Notification

interface PaymentService {
    suspend fun registerTransaction(
        amount: Int,
        email: String,
        description: String = "Wynajem przyczepki",
    ): String

    suspend fun verifyTransaction(
        sessionId: String,
        orderId: Long,
        amount: Int,
        currency: String = "PLN",
    )

    fun paymentRedirectUrl(token: String): String

    suspend fun handlePaymentNotification(notification: P24Notification)
}
