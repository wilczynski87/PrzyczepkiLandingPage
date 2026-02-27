package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.data.ReservationDto
import kotlinx.datetime.LocalDate

interface ReservationService {
    suspend fun getReservations(from: LocalDate, to: LocalDate? = null): List<ReservationDto>
    suspend fun checkReservation(reservation: ReservationDto): ReservationDto?
    suspend fun createReservation(reservation: ReservationDto): ReservationDto?
    suspend fun deleteReservation(id: Long): Boolean

    suspend fun dtoToReservation(dto: ReservationDto): Reservation
}