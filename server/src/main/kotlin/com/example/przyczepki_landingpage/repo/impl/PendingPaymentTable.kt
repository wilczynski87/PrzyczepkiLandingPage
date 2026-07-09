package com.example.przyczepki_landingpage.repo.impl

import com.example.przyczepki_landingpage.data.PaymentSessionStatus
import com.example.przyczepki_landingpage.data.ReservationDto
import kotlinx.serialization.Contextual
import org.bson.types.ObjectId

data class PendingPaymentTable(
    @Contextual
    val _id: ObjectId? = null,
    val sessionId: String,
    val reservation: ReservationDto,
    val customerId: String?,
    val amount: Int,
    val status: PaymentSessionStatus = PaymentSessionStatus.PENDING,
    val orderId: Long? = null,
    val reservationId: String? = null,
    val errorMessage: String? = null,
)
