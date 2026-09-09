package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.dto.SendEmailResponse
import pl.przyczepki.email.api.dto.ReservationConfirmationData

interface ReservationConfirmationService {
    suspend fun reservationConfirmationData(
        reservationId: String,
        paidAmountGrosze: Int? = null,
        orderId: Long? = null,
    ): ReservationConfirmationData

    suspend fun sendReservationConfirmation(
        reservationId: String,
        paidAmountGrosze: Int? = null,
        orderId: Long? = null,
    ): SendEmailResponse
}
