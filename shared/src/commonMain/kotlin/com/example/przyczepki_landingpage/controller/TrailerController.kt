package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.Trailer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

class TrailerController(
    private val client: HttpClient
) {

    suspend fun getTrailers(): List<Trailer> {
        return try {
            val response: HttpResponse = client.get("$base_url/trailer/all")
            if (response.status.isSuccess()) {
                response.body()
            } else {
                println("Błąd fetch trailers: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error fetching trailers: ${e.message}")
            emptyList()
        }
    }

    suspend fun getTrailer(trailerId: Int): Trailer? {
        return try {
            client.get("$base_url/trailer/$trailerId").body()

        } catch (e: Exception) {
            null
        }
    }

    fun close() {
        client.close()
    }
}