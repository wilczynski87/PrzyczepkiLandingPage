package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.ReservationPrice
import com.example.przyczepki_landingpage.service.ReservationService
import com.example.przyczepki_landingpage.service.startOfTheDay
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import kotlin.getValue
import kotlin.time.Clock

fun Route.reservation() {
    val reservationService by inject<ReservationService>()

    fun todayStartMillis(): Long = Clock.System.now().toEpochMilliseconds()

    route("/reservation") {
        // zwraca listę rezerwacji (dni w których rezerwować nie można!
        get("/current") {
            val reservations = reservationService.getReservations()
            call.respond(reservations)
        }

        // oblicza koszt wynajmu ( ale jeszcze nie rezerwuje )
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
        id = "1",
        trailerId = "1",
        startDate = startOfTheDay(),
        endDate = startOfTheDay() + 24 * 60 * 60 * 1000,
    ),
)

val testReservations2: ReservationDto = ReservationDto(
        id = "2",
        trailerId = "2",
        startDate = startOfTheDay() + 2 * 24 * 60 * 60 * 1000,
        endDate = startOfTheDay() + 4 * 24 * 60 * 60 * 1000,
        reservationPrice = ReservationPrice(
            trailerId = "2",
            reservation = 50.00,
            daysNumber = 1,
            sum = 70.0,
        )
)


