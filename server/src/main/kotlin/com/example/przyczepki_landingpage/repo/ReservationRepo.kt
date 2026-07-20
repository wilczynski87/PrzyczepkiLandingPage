package com.example.przyczepki_landingpage.repo

import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.service.startOfTheDay
import kotlinx.datetime.LocalDate

interface ReservationRepo {
    suspend fun getAllReservations(from: LocalDate = startOfTheDay(), to: LocalDate? = null): List<Reservation>
    suspend fun getReservationById(id: String): Reservation?
    suspend fun createReservation(reservation: Reservation): Reservation?
    suspend fun deleteReservation(id: String): Boolean
    /** Returns an existing reservation that overlaps [from, to] for [trailerId], or null if dates are free. */
    suspend fun checkReservationDates(trailerId: String, from: LocalDate, to: LocalDate): Reservation?

    suspend fun getActiveReservationsForCustomer(customerId: String, date: LocalDate): List<Reservation>
}