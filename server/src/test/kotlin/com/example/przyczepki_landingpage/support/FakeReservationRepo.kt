package com.example.przyczepki_landingpage.support

import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.repo.ReservationRepo
import kotlinx.datetime.LocalDate

class FakeReservationRepo(
    private val reservations: MutableList<Reservation> = mutableListOf(),
) : ReservationRepo {

    fun addReservation(reservation: Reservation) {
        reservations += reservation
    }

    override suspend fun getAllReservations(from: LocalDate, to: LocalDate?): List<Reservation> =
        reservations

    override suspend fun getReservationById(id: String): Reservation? =
        reservations.firstOrNull { it.id == id }

    override suspend fun createReservation(reservation: Reservation): Reservation? {
        reservations += reservation
        return reservation
    }

    override suspend fun deleteReservation(id: String): Boolean =
        reservations.removeIf { it.id == id }

    override suspend fun checkReservationDates(
        trailerId: String,
        from: LocalDate,
        to: LocalDate,
    ): Reservation? = reservations.firstOrNull { reservation ->
        reservation.trailer?.id == trailerId &&
            reservation.startDate != null &&
            reservation.endDate != null &&
            reservation.startDate!! <= to &&
            reservation.endDate!! >= from
    }
}
