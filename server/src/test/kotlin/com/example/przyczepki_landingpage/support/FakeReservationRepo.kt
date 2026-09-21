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
        val saved = if (reservation.id.isNullOrBlank()) {
            reservation.copy(id = "res-${reservations.size + 1}")
        } else {
            reservation
        }
        reservations += saved
        return saved
    }

    override suspend fun deleteReservation(id: String): Boolean =
        reservations.removeIf { it.id == id }

    override suspend fun checkReservationDates(
        trailerId: String,
        from: LocalDate,
        to: LocalDate,
        excludeId: String?,
    ): Reservation? = reservations.firstOrNull { reservation ->
        reservation.trailer?.id == trailerId &&
            reservation.startDate != null &&
            reservation.endDate != null &&
            reservation.startDate!! <= to &&
            reservation.endDate!! >= from &&
            (excludeId.isNullOrBlank() || reservation.id != excludeId)
    }

    override suspend fun getActiveReservationsForCustomer(
        customerId: String,
        date: LocalDate,
    ): List<Reservation> = reservations.filter { reservation ->
        reservation.customer?.id == customerId &&
            reservation.startDate != null &&
            reservation.endDate != null &&
            reservation.startDate!! <= date &&
            reservation.endDate!! >= date
    }

    override suspend fun getReservationsByCustomerId(customerId: String): List<Reservation> =
        reservations
            .filter { it.customer?.id == customerId }
            .sortedByDescending { it.startDate }

    override suspend fun updateReservation(reservation: Reservation): Reservation? {
        val index = reservations.indexOfFirst { it.id == reservation.id }
        if (index < 0) return null
        reservations[index] = reservation
        return reservation
    }
}
