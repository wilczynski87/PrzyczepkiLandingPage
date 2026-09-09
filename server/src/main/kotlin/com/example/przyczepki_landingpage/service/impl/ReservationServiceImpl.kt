package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.ReservationPrice
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.repo.CustomerRepo
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.service.ReservationService
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlin.math.abs

class ReservationServiceImpl(
    private val reservationRepo: ReservationRepo,
    private val trailersRepo: TrailersRepo,
    private val customerRepo: CustomerRepo,
): ReservationService {

    override suspend fun getReservations(from: LocalDate, to: LocalDate?): List<ReservationDto> {
        return reservationRepo.getAllReservations(from, to).map { it.toDto() }
    }

    override suspend fun checkReservation(reservation: ReservationDto): ReservationDto {
        val conflictingReservation = reservationRepo.checkReservationDates(
            reservation.trailerId!!,
            reservation.startDate!!,
            reservation.endDate!!,
        )
        if (conflictingReservation != null) {
            throw Exception("Dates are not available, in collision: ${conflictingReservation.toDto()}")
        }
        return reservation
    }

    override suspend fun calculatePrice(reservation: ReservationDto): ReservationDto {
        val trailer = trailersRepo.getTrailer(reservation.trailerId!!) ?: throw Exception("Trailer not found")
        val startDate = reservation.startDate ?: throw Exception("Start date is null")
        val endDate = reservation.endDate ?: throw Exception("End date is null")
        if (endDate < startDate) throw Exception("End date is before start date")

        val days = startDate.daysUntil(endDate)
        val prices = trailer.prices ?: throw Exception("Trailer prices not found")

        // 1 dzień kalendarzowy (ta sama data od–do) = wynajem do 6h (halfDay)
        val sum = if (days == 0) {
            prices.halfDay ?: throw Exception("Trailer half day price not found")
        } else {
            (0..days).sumOf { day ->
                when (day) {
                    0 -> prices.firstDay ?: throw Exception("Trailer first day price not found")
                    1 -> prices.secondDay ?: throw Exception("Trailer second day price not found")
                    else -> prices.otherDays ?: throw Exception("Trailer other day price not found")
                }
            }
        }

        val reservationPrice = ReservationPrice(
            trailerId = reservation.trailerId,
            reservation = prices.reservation,
            daysNumber = days.toLong(),
            sum = sum,
        )
        return reservation.copy(reservationPrice = reservationPrice)
    }

    override suspend fun createReservation(reservation: ReservationDto): ReservationDto? {
        val customer: Customer? = reservation.customerId?.let { customerRepo.get(it) }

        if (reservation.trailerId == null) throw Exception("Trailer id is null")
        val trailer: Trailer = trailersRepo.getTrailer(reservation.trailerId!!)
            ?: throw Exception("Trailer not found")

        if (reservation.endDate == null) throw Exception("End date is null")
        if (reservation.startDate == null) throw Exception("Start date is null")
        val checkReservation: Reservation? = reservationRepo.checkReservationDates(
            reservation.trailerId!!,
            reservation.startDate!!,
            reservation.endDate!!,
        )
        if (checkReservation != null) {
            throw Exception("Dates are not available, in collision: ${checkReservation.toDto()}")
        }

        val expected = calculatePrice(reservation).reservationPrice
            ?: throw Exception("Could not calculate reservation price")
        val provided = reservation.reservationPrice
            ?: throw Exception("Reservation price is missing")
        if (!isPriceMatching(provided, expected)) {
            throw Exception("Reservation price mismatch: expected=$expected, got=$provided")
        }

        val reservationToMake = Reservation(
            customer = customer,
            trailer = trailer,
            startDate = reservation.startDate!!,
            endDate = reservation.endDate!!,
            reservationPrice = expected,
        )
        val createdReservation = reservationRepo.createReservation(reservationToMake)
            ?: throw Exception("Reservation not created: $reservationToMake")

        return createdReservation.toDto()
    }

    override suspend fun deleteReservation(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun dtoToReservation(dto: ReservationDto): Reservation {
        TODO("Not yet implemented")
    }

    private fun isPriceMatching(provided: ReservationPrice, expected: ReservationPrice): Boolean {
        if (provided.trailerId != expected.trailerId) return false
        if (provided.daysNumber != expected.daysNumber) return false
        if (!doublesEqual(provided.reservation, expected.reservation)) return false
        if (!doublesEqual(provided.sum, expected.sum)) return false
        return true
    }

    private fun doublesEqual(a: Double?, b: Double?): Boolean {
        if (a == null && b == null) return true
        if (a == null || b == null) return false
        return abs(a - b) < 0.01
    }
}
