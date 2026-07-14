package com.example.przyczepki_landingpage.repo

import com.example.przyczepki_landingpage.data.PaymentSessionStatus
import com.example.przyczepki_landingpage.repo.impl.PendingPaymentTable

interface PendingPaymentRepo {
    suspend fun save(pending: PendingPaymentTable): PendingPaymentTable
    suspend fun findBySessionId(sessionId: String): PendingPaymentTable?
    suspend fun updateStatus(
        sessionId: String,
        status: PaymentSessionStatus,
        orderId: Long? = null,
        reservationId: String? = null,
        errorMessage: String? = null,
    ): PendingPaymentTable?
}
