package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.P24Notification

interface PaymentService {
    suspend fun registerTransaction(
        amount: Int,
        customer: Customer,
        description: String = "Wynajem przyczepki",
        regulationAccept: Boolean,
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
