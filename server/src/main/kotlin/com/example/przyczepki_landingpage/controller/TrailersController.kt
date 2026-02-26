package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.service.TrailersService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

interface TrailersController {
    suspend fun getTrailers(): List<Trailer>
    suspend fun getTrailer(id: Int): Trailer?
}

fun Route.trailers() {
    val trailerService by inject<TrailersService>()

    route("/trailer") {
        
        // zwraca listę wszystkich przyczepek
        get("/all") {
            val trailers = trailerService.getTrailers()
            call.respond(trailers)
        }

        // zwraca informacje o danej przyczepce
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            val trailer = trailerService.getTrailer(id) ?: return@get call.respond(HttpStatusCode.NotFound, "Trailer not found")

            call.respond(trailer)
        }

        // zwraca informacje o danej przyczepce
        post("/save") {
            val trailer = call.receive<Trailer>()

            trailerService.saveTrailer(trailer)

            call.respond(trailer)
        }
    }
}