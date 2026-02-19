package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.ReservationPrice
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlin.time.Clock

fun Route.reservation() {
    route("/reservation") {
        get("/current") {

            call.respond(testReservations)
        }

        post("/check") {
            try {
                val reservationDto: ReservationDto = call.receive()
                println("reservationDto: $reservationDto")

                // reservation validation

                // calculate prices
                val reservationToConfirm = testReservations2

                call.respond(reservationToConfirm)
            } catch (e: Exception) {
                println("reservation, check error: $e")
            }

        }

        post("/save") {
//            call.respond(reservationService.setReservation())
        }

    }

}

val testReservations: List<ReservationDto> = listOf(
    ReservationDto(
        id = 1,
        trailerId = 1,
        startDate = todayStartMillis(),
        endDate = todayStartMillis() + 24 * 60 * 60 * 1000,
    ),
)

val testReservations2: ReservationDto = ReservationDto(
        id = 2,
        trailerId = 2,
        startDate = todayStartMillis() + 2 * 24 * 60 * 60 * 1000,
        endDate = todayStartMillis() + 4 * 24 * 60 * 60 * 1000,
        reservationPrice = ReservationPrice(
            trailerId = 2,
            firstDay = 50.00,
            otherDays = 70.00,
            halfDay = null,
            reservation = 40.00,
            daysNumber = 2,
            sum = 120.00,
        )
)

fun todayStartMillis(): Long {
    val now = Clock.System.now().toEpochMilliseconds()

    // obliczamy liczbę milisekund od początku dnia w lokalnym czasie
    val dayInMillis = 24 * 60 * 60 * 1000
    val offset = now % dayInMillis

    return now - offset
}
