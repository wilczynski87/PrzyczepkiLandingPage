package com.example.przyczepki_landingpage.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String,
)
