package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.Trailer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TrailerController(private val client: HttpClient = HttpClient()) {

    suspend fun getTrailers(): List<Trailer> {
        return try {
            client.get("$base_url/trailer/all").body()
        } catch (e: Exception) {
            println("Error: ${e.message}")
            emptyList()
        }
    }

    suspend fun getTrailer(trailerId: Int): Trailer? {
        try {

        } catch (e: Exception) {
            null
        }
        return client.get("$base_url/trailer/$trailerId").body()
    }

    fun close() {
        client.close()
    }
}