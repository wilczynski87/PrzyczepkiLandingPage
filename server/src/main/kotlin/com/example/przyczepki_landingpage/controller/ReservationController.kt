package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.ReservationDto
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
        post {
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

fun todayStartMillis(): Long {
    val now = Clock.System.now().toEpochMilliseconds()

    // obliczamy liczbę milisekund od początku dnia w lokalnym czasie
    val dayInMillis = 24 * 60 * 60 * 1000
    val offset = now % dayInMillis

    return now - offset
}
