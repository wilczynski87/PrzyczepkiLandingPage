package com.example.przyczepki_landingpage.data

import kotlinx.serialization.Serializable

@Serializable
data class ReservationDto(
    val id: Long? = null,
    val customerId: Long? = null,
    val trailerId: Long? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val reservationPrice: ReservationPrice? = null,
)

@Serializable
data class ReservationPrice(
    val trailerId: Long? = null,
    val firstDay: Double? = null,
    val otherDays: Double? = null,
    val halfDay: Double? = null,
    val reservation: Double? = null,
    val daysNumber: Long? = null,
    val sum: Double? = null,
)