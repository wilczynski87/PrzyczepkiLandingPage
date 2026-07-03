package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.auth.RefreshTokenRequest
import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.data.LoginResponse
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

fun Route.authController() {
    val customerService by inject<CustomerService>()
    val authService by inject<JwtService>()

    route("auth") {
        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                val customer = customerService.getCustomerTableByEmail(request.email) ?: throw NullPointerException("Customer, with given email, not found: ${request.email}")
                if (verify(request.password, customer.passwordHash ?: throw Exception("No Password for customerId: ${customer.id}")).not()) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid password")
                    return@post
                }

                val token = authService.generateToken(customer.toCustomer())
                val refreshToken = authService.generateRefreshToken(customer.toCustomer())

                call.respond(
                    LoginResponse(
                        token = token,
                        customerId = customer.id ?: throw IllegalStateException("Brak customer ID: $customer"),
                        refreshToken = refreshToken
                    )
                )

            } catch (e: Exception) {
                println("authController, login: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
            }
        }

        post("refresh") {
//            println("Refresh Token")
            val request = call.receive<RefreshTokenRequest>()
//            println("request: $request")

            val principal = authService.verifyRefreshToken(request.refreshToken)

            if (!principal.isValid || principal.userId.isNullOrBlank()) {
                call.respond(HttpStatusCode.Unauthorized, principal.error ?: "Invalid refresh token")
                return@post
            }

            val customer = customerService.get(principal.userId) ?: throw NullPointerException("Customer, with given id, not found: ${principal.userId}")

            val token = authService.generateToken(customer)
            val refreshToken = authService.generateRefreshToken(customer)

            call.respond(
                LoginResponse(
                    token = token,
                    customerId = customer.id ?: throw IllegalStateException("Brak customer ID: $customer"),
                    refreshToken = refreshToken
                )
            )
        }
    }
}