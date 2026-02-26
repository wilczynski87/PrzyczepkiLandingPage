package com.example.przyczepki_landingpage.repo

import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.service.startOfTheDay

interface ReservationRepo {
    suspend fun getAllReservations(from: Long = startOfTheDay(), to: Long? = null): List<Reservation>
    suspend fun getReservationById(id: String): Reservation?
    suspend fun createReservation(reservation: Reservation): Reservation?
    suspend fun deleteReservation(id: String): Boolean


}