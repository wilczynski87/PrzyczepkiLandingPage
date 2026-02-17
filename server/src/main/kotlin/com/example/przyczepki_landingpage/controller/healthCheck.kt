package com.example.przyczepki_landingpage.controller

import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.healthCheck() {
    get {
        call.respondText("Server is running")

    }
}