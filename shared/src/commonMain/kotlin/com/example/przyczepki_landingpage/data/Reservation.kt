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
    val trailerName: String? = null,
    val customerName: String? = null,
    val customerEmail: String? = null,
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
    val paymentToken: String? = null,
) {
    fun toDto(): ReservationDto = toAdminDto()

    fun toAdminDto(): ReservationDto = ReservationDto(
        id = id,
        customerId = customer?.id,
        trailerId = trailer?.id,
        startDate = startDate,
        endDate = endDate,
        reservationPrice = reservationPrice,
        trailerName = trailer?.name,
        customerName = customer.displayLabel(),
        customerEmail = customer?.getEmail(),
    )
}

private fun Customer?.displayLabel(): String? {
    val company = this?.company?.name?.trim().orEmpty()
    if (company.isNotBlank()) return company
    val person = listOfNotNull(this?.private?.firstName, this?.private?.lastName)
        .joinToString(" ")
        .trim()
    if (person.isNotBlank()) return person
    return this?.getEmail()
}