package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.service.CustomerService
import com.example.przyczepki_landingpage.service.auth.JwtService
import com.example.przyczepki_landingpage.service.auth.PasswordUtil.verify
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import org.mindrot.jbcrypt.BCrypt

fun Route.authController() {
    val customerService by inject<CustomerService>()

    route("auth") {
        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                val customer = customerService.getCustomerTableByEmail(request.email) ?: throw NullPointerException("Customer, with given email, not found: ${request.email}")
                if(verify(request.password, customer.passwordHash?: throw Exception("No Password for customerId: ${customer.id}")).not()) call.respond(HttpStatusCode.Unauthorized, "Invalid password")

                val token = JwtService.generate(customer.toCustomer())

                call.respond(
                    mapOf(
                        "token" to token,
                        "customerId" to customer.id
                    )
                )

            } catch (e: Exception) {
                println("authController, login: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
            }
        }
    }
}