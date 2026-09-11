package com.example.przyczepki_landingpage.data

import kotlinx.serialization.Serializable

@Serializable
data class CustomerRegisterRequest(
    val customer: Customer,
    val password: String,
)
