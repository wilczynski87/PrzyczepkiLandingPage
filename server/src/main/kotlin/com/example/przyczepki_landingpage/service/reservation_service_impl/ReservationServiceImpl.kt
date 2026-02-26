package com.example.przyczepki_landingpage.service.reservation_service_impl

import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.service.ReservationService
import kotlin.getValue

class ReservationServiceImpl(
    private val reservationRepo: ReservationRepo
): ReservationService {

    override suspend fun getReservations(): List<ReservationDto> {

        return reservationRepo.getAllReservations().map { it.toDto() }
    }

    override suspend fun checkReservation(reservation: ReservationDto): ReservationDto? {


        TODO("Not yet implemented")
    }

    override suspend fun createReservation(reservation: ReservationDto): ReservationDto? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReservation(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun dtoToReservation(dto: ReservationDto): Reservation {
        TODO("Not yet implemented")
    }
}