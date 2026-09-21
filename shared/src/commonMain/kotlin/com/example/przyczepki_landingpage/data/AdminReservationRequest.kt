package com.example.przyczepki_landingpage.data

import com.example.przyczepki_landingpage.data.serializer.KotlinxLocalDateSerializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class AdminReservationRequest(
    val customerId: String,
    val trailerId: String,
    @Serializable(with = KotlinxLocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = KotlinxLocalDateSerializer::class)
    val endDate: LocalDate,
)
