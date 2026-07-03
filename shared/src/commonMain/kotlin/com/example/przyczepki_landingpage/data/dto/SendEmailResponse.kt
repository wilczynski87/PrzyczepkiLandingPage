package com.example.przyczepki_landingpage.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SendEmailResponse(
    val status: String,
    val to: String,
)
