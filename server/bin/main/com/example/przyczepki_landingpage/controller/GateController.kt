package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.OpenGateRequest
import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.service.GateAccessDeniedException
import com.example.przyczepki_landingpage.service.GateService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.gate() {
    val gateService by inject<GateService>()

    authenticate {
        route("/gate") {
            post("/open") {
                val jwtUserId = call.principal<JWTPrincipal>()
                    ?.payload
                    ?.getClaim("userId")
                    ?.asString()

                val request = runCatching { call.receive<OpenGateRequest>() }
                    .getOrElse { OpenGateRequest() }

                try {
                    val customerId = gateService.checkUserAuthenticated(jwtUserId)
//                    val reservation = gateService.checkUserHasReservation(
//                        customerId = customerId,
//                        reservationId = request.reservationId,
//                    )
                    val reservation = Reservation("id")
                    gateService.ensureCooldown(customerId)

                    val response = gateService.openGate().copy(
                        reservationId = reservation.id,
                    )

                    gateService.logOpenEvent(
                        customerId = customerId,
                        reservationId = reservation.id
                            ?: throw IllegalStateException("Rezerwacja bez id"),
                    )

                    call.respond(response)
                } catch (e: GateAccessDeniedException) {
                    val status = if (e.message?.contains("zalogowany", ignoreCase = true) == true) {
                        HttpStatusCode.Unauthorized
                    } else {
                        HttpStatusCode.Forbidden
                    }
                    call.respond(status, mapOf("error" to (e.message ?: "Brak dostępu")))
                }
            }

            get("/testGate") {
                gateService.openGate()
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
