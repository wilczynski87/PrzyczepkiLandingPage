package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.ReservationPrice
import com.example.przyczepki_landingpage.service.CustomerService
import com.example.przyczepki_landingpage.service.ReservationService
import com.example.przyczepki_landingpage.service.TrailersService
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
import org.koin.ktor.ext.inject

fun Route.reservation() {
    val reservationService by inject<ReservationService>()
    val customerService by inject<CustomerService>()
    val trailersService by inject<TrailersService>()

    route("/reservation") {
        // zwraca listę rezerwacji (dni w których rezerwować nie można!
        get("/current") {
            val from: LocalDate = call.request.queryParameters["from"]?.let { LocalDate.parse(it) } ?: startOfTheDay()
            val to: LocalDate? = call.request.queryParameters["to"]?.let { LocalDate.parse(it) }

            val reservations = reservationService.getReservations(from, to)
            call.respond(reservations)
        }

        // oblicza koszt wynajmu (ale jeszcze nie rezerwuje)
        post("/check") {
            val reservationDto: ReservationDto = call.receive()
            println("reservationDto: $reservationDto")

            trailersService.getTrailer(reservationDto.trailerId!!)
                ?: return@post call.respond(HttpStatusCode.NotAcceptable, "Nie znaleziono sprzętu o takim id: ${reservationDto.trailerId}")

            val reservationToConfirm = reservationService.checkReservation(reservationDto)

            val finalReservation = reservationService.calculatePrice(reservationToConfirm)

            call.respond(finalReservation)
        }

        post("/create") {
            println("reservation, create")

            val reservationDto: ReservationDto = call.receive()
            println("reservationDto: $reservationDto")

            if(reservationDto.customerId.isNullOrBlank()) throw NullPointerException("CustomerId is wrong: ${reservationDto.customerId}")
            if(reservationDto.trailerId.isNullOrBlank()) throw NullPointerException("TrailerId is wrong: ${reservationDto.trailerId}")

            customerService.get(reservationDto.customerId!!) ?: call.respond(HttpStatusCode.NotAcceptable, "Nie znaleziono Klienta o takim id: ${reservationDto.customerId}")
            trailersService.getTrailer(reservationDto.trailerId!!) ?: call.respond(HttpStatusCode.NotAcceptable, "Nie znaleziono sprzętu o takim id: ${reservationDto.trailerId}")

            val reservation = reservationService.createReservation(reservationDto) ?: throw Exception("Reservation not created")

            call.respond(reservation)
        }
    }
}


