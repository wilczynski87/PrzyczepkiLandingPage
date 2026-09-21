package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.AdminReservationRequest
import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.data.ReservationDto
import kotlinx.datetime.LocalDate

interface ReservationService {
    suspend fun getReservations(from: LocalDate, to: LocalDate? = null): List<ReservationDto>
    suspend fun getCustomerReservations(customerId: String): List<ReservationDto>
    suspend fun getAdminReservations(
        from: LocalDate?,
        to: LocalDate?,
        customerId: String?,
        trailerId: String?,
    ): List<ReservationDto>
    suspend fun checkReservation(reservation: ReservationDto): ReservationDto
    suspend fun calculatePrice(reservation: ReservationDto): ReservationDto
    suspend fun createReservation(reservation: ReservationDto): ReservationDto?
    suspend fun createAdminReservation(request: AdminReservationRequest): ReservationDto
    suspend fun updateAdminReservation(id: String, request: AdminReservationRequest): ReservationDto?
    suspend fun deleteReservation(id: String): Boolean
    suspend fun dtoToReservation(dto: ReservationDto): Reservation
}