package com.example.przyczepki_landingpage.data

import com.example.przyczepki_landingpage.data.serializer.KotlinxLocalDateSerializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ReservationDto(
    val id: String? = null,
    val customerId: String? = null,
    val trailerId: String? = null,
    @Serializable(with = KotlinxLocalDateSerializer::class)
    val startDate: LocalDate? = null,
    @Serializable(with = KotlinxLocalDateSerializer::class)
    val endDate: LocalDate? = null,
    val reservationPrice: ReservationPrice? = null,
)

@Serializable
data class ReservationPrice(
    val trailerId: String? = null,
    val reservation: Double? = null,
    val daysNumber: Long? = null,
    val sum: Double? = null,
)

@Serializable
data class Reservation(
    val id: String? = null,
    val customer: Customer? = null,
    val trailer: Trailer? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val reservationPrice: ReservationPrice? = null,
) {
    fun toDto(): ReservationDto = ReservationDto(
        id = id,
        customerId = customer?.id,
        trailerId = trailer?.id,
        startDate = startDate,
        endDate = endDate,
        reservationPrice = reservationPrice,
    )

}