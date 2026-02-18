package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.ReservationDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class ReservationController(private val client: HttpClient) {

    suspend fun getReservations(): List<ReservationDto> = client.get("$base_url/reservation/current").body() ?: emptyList()

    suspend fun setReservation(reservation: ReservationDto): ReservationDto? = client.post("$base_url/reservation") {
        setBody(reservation)
    }.body()


}