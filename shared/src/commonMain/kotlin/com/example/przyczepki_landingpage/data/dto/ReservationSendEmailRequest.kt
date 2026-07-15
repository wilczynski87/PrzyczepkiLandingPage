package com.example.przyczepki_landingpage.data.dto

import kotlinx.serialization.Serializable
import pl.przyczepki.email.api.dto.ReservationConfirmationData

@Serializable
data class ReservationSendEmailRequest(
    val to: String,
    val subject: String,
    val template: String,
    val data: ReservationConfirmationData,
)
