package com.example.przyczepki_landingpage.support

import com.example.przyczepki_landingpage.data.Prices
import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.Trailer
import kotlinx.datetime.LocalDate

object ReservationTestFixtures {
    const val TRAILER_ID = "trailer-1"

    val samplePrices = Prices(
        trailerId = TRAILER_ID,
        firstDay = 100.0,
        secondDay = 80.0,
        otherDays = 50.0,
        halfDay = 60.0,
        reservation = 30.0,
    )

    fun trailer(
        id: String = TRAILER_ID,
        prices: Prices? = samplePrices,
    ): Trailer = Trailer(
        id = id,
        name = "Test trailer",
        prices = prices,
    )

    fun reservationDto(
        trailerId: String = TRAILER_ID,
        startDate: LocalDate = LocalDate(2025, 6, 10),
        endDate: LocalDate = LocalDate(2025, 6, 10),
    ): ReservationDto = ReservationDto(
        trailerId = trailerId,
        startDate = startDate,
        endDate = endDate,
    )

    fun existingReservation(
        trailerId: String = TRAILER_ID,
        startDate: LocalDate = LocalDate(2025, 6, 10),
        endDate: LocalDate = LocalDate(2025, 6, 12),
    ): Reservation = Reservation(
        id = "existing-1",
        trailer = trailer(trailerId),
        startDate = startDate,
        endDate = endDate,
    )
}
