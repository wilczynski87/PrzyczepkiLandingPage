package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.service.CustomerService
import com.example.przyczepki_landingpage.service.auth.PasswordUtil.hash
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.response.respond
import io.ktor.server.response.respondNullable
import org.koin.ktor.ext.inject

fun Route.customerController() {
    val customerService by inject<CustomerService>()

    route("/customer") {
        post {
            try {
                // TODO hasło początkowe + validaja (np powtarzanie email lub nip/pesel)
                val customer = call.receive<Customer>()
                val saved = customerService.save(customer)
                call.respondNullable(saved?.toDto())
            } catch (e: Exception) {
                println("customerController, Error: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
            }
        }

        post("/changePassword") {
            try {
                val request = call.receive<LoginRequest>()

                val customer = customerService.getCustomerByEmail(request.email)
                    ?: throw NullPointerException("Customer, with given email, not found: ${request.email}")

                val updated = customerService.update(customer.copy(passwordHash = hash(request.password)))
                    ?: throw NullPointerException("Bad Password for customer email: ${request.email}")

                call.respondNullable(updated.id)
            } catch (e: Exception) {
                println("customerController, Error: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
            }
        }

        authenticate {
            get("/{id}") {
                try {
                    val id = call.parameters["id"] ?: throw BadRequestException("Brak id")

                    val customer = customerService.get(id)
                        ?: throw NullPointerException("Nie ma klienta o takim id: $id")

                    call.respondNullable(customer.toDto())

                } catch (e: Exception) {
                    println("Error: ${e.message}")
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }

            put {
                val customer = call.receive<Customer>()

                val updated = customerService.update(customer)
                    ?: return@put call.respond(HttpStatusCode.NotFound)

                call.respondNullable(HttpStatusCode.OK, updated.toDto())
            }

            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest, null
                )

                val deleted = customerService.delete(id)

                if (deleted) {
                    call.respond(HttpStatusCode.OK, true)
                } else {
                    call.respond(HttpStatusCode.NotFound, false)
                }
            }
        }
    }
}