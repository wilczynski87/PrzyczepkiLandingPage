package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.ReservationDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

class ReservationController(private val client: HttpClient) {

    suspend fun getReservations(): List<ReservationDto> = client.get("$base_url/reservation/current").body() ?: emptyList()

    suspend fun checkReservation(reservation: ReservationDto): Result<ReservationDto> {
        val response = client.post("$base_url/reservation/check") {
            setBody(reservation)
        }
        return when (response.status) {
            HttpStatusCode.OK -> {
                val dto = response.body<ReservationDto>()
                Result.success(dto)
            }
            else -> {
                val text = response.bodyAsText()
                val errors: List<String> = Json.decodeFromString(text)
                Result.failure(Exception(errors.joinToString { it }))
            }
        }
    }

    suspend fun createReservation(reservation: ReservationDto): ReservationDto? = client.post("$base_url/reservation/save") {
        setBody(reservation)
    }.body()


}