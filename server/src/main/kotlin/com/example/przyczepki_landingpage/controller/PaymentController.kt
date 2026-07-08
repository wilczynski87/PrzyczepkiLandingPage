package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.P24Notification
import com.example.przyczepki_landingpage.data.PaymentRegisterRequest
import com.example.przyczepki_landingpage.data.PaymentRegisterResponse
import com.example.przyczepki_landingpage.service.PaymentService
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.payment() {
    val paymentService by inject<PaymentService>()

    route("/payment") {
        post("/register") {
            val request = call.receive<PaymentRegisterRequest>()
            println("request payment register: $request")
            val token = paymentService.registerTransaction(
                amount = request.amount,
                customer = request.customer,
                description = request.description,
                regulationAccept = request.regulationAccept,
            )
            call.respond(
                PaymentRegisterResponse(
                    token = token,
                    redirectUrl = paymentService.paymentRedirectUrl(token),
                )
            )
        }

        post("/notification") {
            val notification = call.receiveParameters().toP24Notification()
            println("notification: $notification")
            paymentService.handlePaymentNotification(notification)
            call.respondText("OK", status = HttpStatusCode.OK)
        }
    }
}

private fun Parameters.toP24Notification(): P24Notification {
    fun required(name: String): String =
        this[name] ?: throw BadRequestException("Brak parametru $name w notyfikacji P24")

    return P24Notification(
        merchantId = required("merchantId").toInt(),
        posId = required("posId").toInt(),
        sessionId = required("sessionId"),
        amount = required("amount").toInt(),
        originAmount = this["originAmount"]?.toIntOrNull(),
        currency = required("currency"),
        orderId = required("orderId").toLong(),
        methodId = this["methodId"]?.toIntOrNull(),
        statement = this["statement"],
        sign = required("sign"),
    )
}
