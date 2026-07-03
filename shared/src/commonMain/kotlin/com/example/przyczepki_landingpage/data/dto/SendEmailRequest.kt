package com.example.przyczepki_landingpage.data.dto

import kotlinx.serialization.Serializable
import pl.przyczepki.email.api.dto.AccountConfirmationData

@Serializable
data class SendEmailRequest(
    val to: String,
    val subject: String,
    val template: String,
    val data: AccountConfirmationData,
)
