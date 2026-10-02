package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.dto.CouponValidateRequest
import com.example.przyczepki_landingpage.service.CouponService
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.couponController() {
    val couponService by inject<CouponService>()

    route("/coupon") {
        post("/validate") {
            val request = call.receive<CouponValidateRequest>()
            val coupon = couponService.requireUsable(request.code, request.customerId)
            call.respond(coupon)
        }
    }
}
