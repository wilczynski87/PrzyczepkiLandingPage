package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.ReservationDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class ReservationController(private val client: HttpClient) {

    suspend fun getReservations(): List<ReservationDto> = client.get("$base_url/reservation/current").body() ?: emptyList()

    suspend fun getMyReservations(): Result<List<ReservationDto>> {
        return try {
            val response = client.get("$base_url/reservation/mine")
            when {
                response.status.isSuccess() ->
                    Result.success(response.body<List<ReservationDto>>())
                response.status.value == 401 || response.status.value == 403 ->
                    Result.failure(InvalidLoginCredentialsException())
                else -> {
                    val details = runCatching { response.bodyAsText() }.getOrNull()
                        ?.takeIf { it.isNotBlank() }
                    Result.failure(
                        Exception(
                            details ?: "Nie udało się pobrać rezerwacji (${response.status.value})",
                        ),
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
                val jsonError = Regex(""""error"\s*:\s*"([^"]+)"""").find(text)?.groupValues?.get(1)
                val listError = runCatching { Json.decodeFromString<List<String>>(text) }.getOrNull()
                Result.failure(
                    Exception(
                        jsonError
                            ?: listError?.joinToString { it }
                            ?: text.ifBlank { "Nie udało się sprawdzić rezerwacji" },
                    ),
                )
            }
        }
    }

    suspend fun createReservation(reservation: ReservationDto): ReservationDto? = client.post("$base_url/reservation/save") {
        setBody(reservation)
    }.body()


}