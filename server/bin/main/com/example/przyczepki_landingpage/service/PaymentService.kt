package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.P24Notification
import com.example.przyczepki_landingpage.data.PaymentStatusResponse
import com.example.przyczepki_landingpage.data.ReservationDto

interface PaymentService {
    suspend fun registerTransaction(
        amount: Int,
        customer: Customer,
        reservation: ReservationDto,
        description: String = "Wynajem przyczepki",
        regulationAccept: Boolean,
    ): PaymentRegistration

    suspend fun verifyTransaction(
        sessionId: String,
        orderId: Long,
        amount: Int,
        currency: String = "PLN",
    )

    fun paymentRedirectUrl(token: String): String

    suspend fun handlePaymentNotification(notification: P24Notification)

    suspend fun getPaymentStatus(sessionId: String): PaymentStatusResponse
}

data class PaymentRegistration(
    val token: String,
    val sessionId: String,
)
