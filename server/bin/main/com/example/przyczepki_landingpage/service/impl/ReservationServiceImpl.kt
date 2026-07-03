package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.ReservationPrice
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.service.ReservationService
import kotlinx.datetime.LocalDate

class ReservationServiceImpl(
    private val reservationRepo: ReservationRepo,
    private val trailersRepo: TrailersRepo,
//    private val customerRepo: CustomerRepo,
): ReservationService {

    override suspend fun getReservations(from: LocalDate, to: LocalDate?): List<ReservationDto> {
        val res = reservationRepo.getAllReservations(from, to)
        println("res: \n" + res)

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
        val days = reservation.endDate!!.dayOfYear - reservation.startDate!!.dayOfYear
        val prices = trailer.prices ?: throw Exception("Trailer prices not found")

        val sum = (0..days).sumOf { day ->
            when (day) {
                0 -> prices.firstDay ?: throw Exception("Trailer first day price not found")
                1 -> prices.secondDay ?: throw Exception("Trailer second day price not found")
                else -> prices.otherDays ?: throw Exception("Trailer other day price not found")
            }
        }

        val reservationPrice: ReservationPrice = ReservationPrice(
            trailerId = reservation.trailerId,
            reservation = trailer.prices!!.reservation,
            daysNumber = days.toLong(),
            sum = sum,
        )
        return reservation.copy(reservationPrice = reservationPrice)
    }

    override suspend fun createReservation(reservation: ReservationDto): ReservationDto? {
//        println("createReservation in ReservationServiceImpl: $reservation")
//        try {
            // check for Customer
            val customer: Customer? = null

            // check for Trailer
            if(reservation.trailerId == null) throw Exception("Trailer id is null")
            val trailer: Trailer = trailersRepo.getTrailer(reservation.trailerId!!) ?: throw Exception("Trailer not found")

            // validate dates
            if(reservation.endDate == null) throw Exception("End date is null")
            if(reservation.startDate == null) throw Exception("Start date is null")
            val checkReservation: Reservation? = reservationRepo.checkReservationDates(
                reservation.trailerId!!,
                reservation.startDate!!,
                reservation.endDate!!,
            )
            if(checkReservation != null) throw Exception("Dates are not available, in collision: ${checkReservation.toDto()}")

            // calculate price -> chcek if ok?
            val isPriceOk = true
            // create reservation
            val reservationToMake: Reservation = Reservation(
                customer = customer,
                trailer = trailer,
                startDate = reservation.startDate!!,
                endDate = reservation.endDate!!,
                reservationPrice = reservation.reservationPrice,
            )
            println("reservationToMake: $reservationToMake")
            val createdReservation = reservationRepo.createReservation(reservationToMake) ?: throw Exception("Reservation not created: $reservationToMake")

            // return reservation
            return createdReservation.toDto()
//        } catch (e: Exception) {
//            println("ReservationServiceImpl: Error creating reservation: ${e.message}")
//            return null
//        }

    }

    override suspend fun deleteReservation(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun dtoToReservation(dto: ReservationDto): Reservation {
        TODO("Not yet implemented")
    }
}