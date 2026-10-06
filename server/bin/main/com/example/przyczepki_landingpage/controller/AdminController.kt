package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.AdminReservationRequest
import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.CustomerRegisterRequest
import com.example.przyczepki_landingpage.data.dto.Coupon
import com.example.przyczepki_landingpage.modules.ApiConfig
import com.example.przyczepki_landingpage.service.CouponService
import com.example.przyczepki_landingpage.service.CustomerService
import com.example.przyczepki_landingpage.service.PushNotificationService
import com.example.przyczepki_landingpage.service.ReservationService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.koin.ktor.ext.inject
import com.example.przyczepki_landingpage.service.startOfTheDay

fun Route.adminController() {
    val customerService by inject<CustomerService>()
    val reservationService by inject<ReservationService>()
    val couponService by inject<CouponService>()
    val pushNotificationService by inject<PushNotificationService>()
    val apiConfig by inject<ApiConfig>()
    val adminEmails = apiConfig.adminEmails.toSet()

    post("/push/test") {
        val key = call.request.headers["X-Internal-Api-Key"]
        if (key.isNullOrBlank() || key != apiConfig.auth.internalApiKey) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
            return@post
        }
        val result = pushNotificationService.sendTest()
        call.respond(
            if (result.sent) HttpStatusCode.OK else HttpStatusCode.BadGateway,
            result,
        )
    }

    authenticate {
        route("/admin") {
            post("/push/test") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@post
                val result = pushNotificationService.sendTest()
                call.respond(
                    if (result.sent) HttpStatusCode.OK else HttpStatusCode.BadGateway,
                    result,
                )
            }
            get("/reservations") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@get
                val from = call.request.queryParameters["from"]?.let { LocalDate.parse(it) }
                    ?: startOfTheDay().minus(30, DateTimeUnit.DAY)
                val to = call.request.queryParameters["to"]?.let { LocalDate.parse(it) }
                    ?: startOfTheDay().plus(180, DateTimeUnit.DAY)
                val customerId = call.request.queryParameters["customerId"]
                val trailerId = call.request.queryParameters["trailerId"]
                call.respond(
                    reservationService.getAdminReservations(from, to, customerId, trailerId)
                )
            }

            post("/reservations") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@post
                val request = call.receive<AdminReservationRequest>()
                try {
                    call.respond(HttpStatusCode.Created, reservationService.createAdminReservation(request))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.Conflict, e.message ?: "Nie można utworzyć rezerwacji")
                }
            }

            put("/reservations/{id}") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@put
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Brak id")
                val request = call.receive<AdminReservationRequest>()
                try {
                    val updated = reservationService.updateAdminReservation(id, request)
                        ?: return@put call.respond(HttpStatusCode.NotFound, "Nie znaleziono rezerwacji")
                    call.respond(updated)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.Conflict, e.message ?: "Nie można zaktualizować rezerwacji")
                }
            }

            delete("/reservations/{id}") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@delete
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Brak id")
                val deleted = reservationService.deleteReservation(id)
                if (deleted) call.respond(HttpStatusCode.OK, true)
                else call.respond(HttpStatusCode.NotFound, false)
            }

            get("/customers") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@get
                val search = call.request.queryParameters["search"]
                call.respond(customerService.list(search))
            }

            get("/customers/{id}") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@get
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Brak id")
                val customer = customerService.get(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Nie znaleziono klienta")
                call.respond(customer)
            }

            post("/customers") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@post
                val request = call.receive<CustomerRegisterRequest>()
                val password = request.password.trim()
                if (password.length < 6) {
                    throw BadRequestException("Hasło musi mieć co najmniej 6 znaków")
                }
                val saved = customerService.saveAndConfirm(request.customer, password)
                    ?: throw BadRequestException("Nie udało się zapisać klienta")
                call.respond(HttpStatusCode.Created, saved)
            }

            put("/customers/{id}") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@put
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Brak id")
                val customer = call.receive<Customer>().copy(id = id)
                val updated = customerService.update(customer)
                    ?: return@put call.respond(HttpStatusCode.NotFound, "Nie znaleziono klienta")
                call.respond(updated)
            }

            delete("/customers/{id}") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@delete
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Brak id")
                val reservations = reservationService.getCustomerReservations(id)
                if (reservations.isNotEmpty()) {
                    return@delete call.respond(
                        HttpStatusCode.Conflict,
                        "Najpierw usuń rezerwacje klienta (${reservations.size})",
                    )
                }
                val deleted = customerService.delete(id)
                if (deleted) call.respond(HttpStatusCode.OK, true)
                else call.respond(HttpStatusCode.NotFound, false)
            }

            get("/coupons") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@get
                call.respond(couponService.list())
            }

            post("/coupons") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@post
                val coupon = call.receive<Coupon>()
                call.respond(HttpStatusCode.Created, couponService.create(coupon))
            }

            put("/coupons/{id}") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@put
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Brak id")
                val updated = couponService.update(call.receive<Coupon>().copy(id = id))
                    ?: return@put call.respond(HttpStatusCode.NotFound, "Nie znaleziono kuponu")
                call.respond(updated)
            }

            delete("/coupons/{id}") {
                if (call.requireAdmin(customerService, adminEmails) == null) return@delete
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Brak id")
                val deleted = couponService.delete(id)
                if (deleted) call.respond(HttpStatusCode.OK, true)
                else call.respond(HttpStatusCode.NotFound, false)
            }
        }
    }
}

private suspend fun ApplicationCall.requireAdmin(
    customerService: CustomerService,
    adminEmails: Set<String>,
): Customer? {
    val userId = principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("userId")
        ?.asString()
    if (userId.isNullOrBlank()) {
        respond(HttpStatusCode.Unauthorized, "Brak dostępu")
        return null
    }
    val customer = customerService.get(userId)
    if (customer == null || !customer.isAdmin(adminEmails)) {
        respond(HttpStatusCode.Forbidden, "Brak uprawnień administratora")
        return null
    }
    return customer
}
