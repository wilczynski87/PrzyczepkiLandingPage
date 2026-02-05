package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.Trailer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

const val base_url = "http://localhost:8080"

class ApiClient(private val client: HttpClient = HttpClient()) {

    suspend fun getTrailers(): List<Trailer> {
        return client.get("$base_url/trailer/all").body()
    }

    suspend fun getTrailer(trailerId: Int): Trailer {
        return client.get("$base_url/trailer/$trailerId").body()
    }

    fun close() {
        client.close()
    }

}