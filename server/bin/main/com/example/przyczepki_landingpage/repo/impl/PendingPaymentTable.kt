package com.example.przyczepki_landingpage.repo.impl

import com.example.przyczepki_landingpage.data.PaymentSessionStatus
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.ReservationPrice
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.serialization.Contextual
import org.bson.types.ObjectId

data class PendingPaymentTable(
    @Contextual
    val _id: ObjectId? = null,
    val sessionId: String,
    val reservation: PendingReservationDocument,
    val customerId: String?,
    val amount: Int,
    val status: PaymentSessionStatus = PaymentSessionStatus.PENDING,
    val orderId: Long? = null,
    val reservationId: String? = null,
    val errorMessage: String? = null,
) {
    constructor(
        sessionId: String,
        reservation: ReservationDto,
        customerId: String?,
        amount: Int,
        status: PaymentSessionStatus = PaymentSessionStatus.PENDING,
    ) : this(
        sessionId = sessionId,
        reservation = PendingReservationDocument.from(reservation),
        customerId = customerId,
        amount = amount,
        status = status,
    )
}

data class PendingReservationDocument(
    val id: String? = null,
    val customerId: String? = null,
    val trailerId: String? = null,
    val startDate: java.time.LocalDate? = null,
    val endDate: java.time.LocalDate? = null,
    val reservationPrice: ReservationPrice? = null,
) {
    fun toDto(): ReservationDto = ReservationDto(
        id = id,
        customerId = customerId,
        trailerId = trailerId,
        startDate = startDate?.toKotlinLocalDate(),
        endDate = endDate?.toKotlinLocalDate(),
        reservationPrice = reservationPrice,
    )

    companion object {
        fun from(dto: ReservationDto): PendingReservationDocument = PendingReservationDocument(
            id = dto.id,
            customerId = dto.customerId,
            trailerId = dto.trailerId,
            startDate = dto.startDate?.toJavaLocalDate(),
            endDate = dto.endDate?.toJavaLocalDate(),
            reservationPrice = dto.reservationPrice,
        )
    }
}
