package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.P24Notification
import com.example.przyczepki_landingpage.data.PaymentRegisterRequest
import com.example.przyczepki_landingpage.data.PaymentRegisterResponse
import com.example.przyczepki_landingpage.service.PaymentService
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.contentType
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.payment() {
    val paymentService by inject<PaymentService>()

    route("/payment") {
        post("/register") {
            val request = call.receive<PaymentRegisterRequest>()
            println("request payment register: $request")
            val registration = paymentService.registerTransaction(
                amount = request.amount,
                customer = request.customer,
                reservation = request.reservation,
                description = request.description,
                regulationAccept = request.regulationAccept,
            )
            call.respond(
                PaymentRegisterResponse(
                    token = registration.token,
                    redirectUrl = paymentService.paymentRedirectUrl(registration.token),
                    sessionId = registration.sessionId,
                )
            )
        }

        get("/status/{sessionId}") {
            val sessionId = call.parameters["sessionId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Brak sessionId")
            call.respond(paymentService.getPaymentStatus(sessionId))
        }

        post("/notification") {
            val notification = when {
                call.request.contentType().match(ContentType.Application.FormUrlEncoded) == true ->
                    call.receiveParameters().toP24Notification()
                else -> call.receive<P24Notification>()
            }
            println("notification: $notification")
            paymentService.handlePaymentNotification(notification)
            call.respondText("OK", status = HttpStatusCode.OK)
        }
    }
}

private fun Parameters.toP24Notification(): P24Notification {
    fun req(name: String) = this[name] ?: throw BadRequestException("Brak parametru $name")
    fun opt(name: String) = this[name]

    return P24Notification(
        merchantId = req("merchantId").toInt(),
        posId = req("posId").toInt(),
        sessionId = req("sessionId"),
        amount = req("amount").toInt(),
        originAmount = opt("originAmount")?.toInt(),
        currency = req("currency"),
        orderId = req("orderId").toLong(),
        methodId = opt("methodId")?.toInt(),
        statement = opt("statement"),
        sign = req("sign"),
    )
}
