package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.ReservationPrice
import com.example.przyczepki_landingpage.service.ReservationService
import com.example.przyczepki_landingpage.service.startOfTheDay
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import kotlin.getValue
import kotlin.time.Clock

fun Route.reservation() {
    val reservationService by inject<ReservationService>()

    route("/reservation") {
        // zwraca listę rezerwacji (dni w których rezerwować nie można!
        get("/current") {
            val from: LocalDate = call.request.queryParameters["from"]?.let { LocalDate.parse(it) } ?: startOfTheDay()
            val to: LocalDate? = call.request.queryParameters["to"]?.let { LocalDate.parse(it) }

            val reservations = reservationService.getReservations(from, to)
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

        post("/create") {
            println("reservation, create")
            try {
                val reservationDto: ReservationDto = call.receive()
                println("reservationDto: $reservationDto")
                val reservation = reservationService.createReservation(reservationDto) ?: throw Exception("Reservation not created")

                call.respond(reservation)
            } catch (e: Exception) {
                println("reservation, create error: $e")
                call.respond(HttpStatusCode.Conflict ,e.message ?: "Reservation not created")
            }
        }

    }

}

val testReservations: List<ReservationDto> = listOf(
    ReservationDto(
        id = "1",
        trailerId = "1",
        startDate = startOfTheDay(),
        endDate = startOfTheDay().plus(2, DateTimeUnit.DAY)
    )
)

val testReservations2: ReservationDto = ReservationDto(
        id = "2",
        trailerId = "2",
        startDate = startOfTheDay().plus(2, DateTimeUnit.DAY),
        endDate = startOfTheDay().plus(3, DateTimeUnit.DAY),
        reservationPrice = ReservationPrice(
            trailerId = "2",
            reservation = 50.00,
            daysNumber = 1,
            sum = 70.0,
        )
)


