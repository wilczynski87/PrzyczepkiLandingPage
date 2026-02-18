package com.example.przyczepki_landingpage.data

import kotlinx.serialization.Serializable

@Serializable
data class ReservationDto(
    val id: Long,
    val trailerId: Long,
    val startDate: Long,
    val endDate: Long,

)