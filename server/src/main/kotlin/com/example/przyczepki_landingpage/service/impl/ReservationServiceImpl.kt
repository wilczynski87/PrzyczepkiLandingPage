package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.AdminReservationRequest
import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.ReservationPrice
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.repo.CustomerRepo
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.service.CouponService
import com.example.przyczepki_landingpage.service.ReservationService
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlin.math.abs

class ReservationServiceImpl(
    private val reservationRepo: ReservationRepo,
    private val trailersRepo: TrailersRepo,
    private val customerRepo: CustomerRepo,
    private val couponService: CouponService,
): ReservationService {

    override suspend fun getReservations(from: LocalDate, to: LocalDate?): List<ReservationDto> {
        return reservationRepo.getAllReservations(from, to).map { it.toDto() }
    }

    override suspend fun getCustomerReservations(customerId: String): List<ReservationDto> {
        if (customerId.isBlank()) return emptyList()
        return reservationRepo.getReservationsByCustomerId(customerId).map { it.toAdminDto() }
    }

    override suspend fun getAdminReservations(
        from: LocalDate?,
        to: LocalDate?,
        customerId: String?,
        trailerId: String?,
    ): List<ReservationDto> {
        val fromDate = from ?: LocalDate(2000, 1, 1)
        return reservationRepo.getAllReservations(fromDate, to)
            .filter { customerId.isNullOrBlank() || it.customer?.id == customerId }
            .filter { trailerId.isNullOrBlank() || it.trailer?.id == trailerId }
            .sortedByDescending { it.startDate }
            .map { it.toAdminDto() }
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

        val calendarSpan = startDate.daysUntil(endDate)
        val prices = trailer.prices ?: throw Exception("Trailer prices not found")

        // Ta sama data = pół dnia (do 6h).
        // Od–do kolejnego dnia (np. 8:00→8:00) = 1 doba, nie 2.
        // daysNumber = liczba dób = daysUntil(start, end).
        val sum = if (calendarSpan == 0) {
            prices.halfDay ?: throw Exception("Trailer half day price not found")
        } else {
            (0 until calendarSpan).sumOf { dayIndex ->
                when (dayIndex) {
                    0 -> prices.firstDay ?: throw Exception("Trailer first day price not found")
                    1 -> prices.secondDay ?: throw Exception("Trailer second day price not found")
                    else -> prices.otherDays ?: throw Exception("Trailer other day price not found")
                }
            }
        }

        val basePrice = ReservationPrice(
            trailerId = reservation.trailerId,
            reservation = prices.reservation,
            daysNumber = calendarSpan.toLong(),
            sum = sum,
        )
        val couponCode = reservation.couponCode?.trim()?.ifBlank { null }
        val reservationPrice = if (couponCode == null) {
            basePrice
        } else {
            val coupon = couponService.requireUsable(couponCode, reservation.customerId)
            couponService.applyToPrice(basePrice, coupon)
        }
        return reservation.copy(
            reservationPrice = reservationPrice,
            couponCode = reservationPrice.couponCode ?: couponCode,
        )
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
            couponCode = expected.couponCode ?: reservation.couponCode,
        )
        val createdReservation = reservationRepo.createReservation(reservationToMake)
            ?: throw Exception("Reservation not created: $reservationToMake")

        expected.couponCode?.let { code ->
            couponService.requireUsable(code, reservation.customerId).id?.let { couponService.markUsed(it) }
        }

        return createdReservation.toAdminDto()
    }

    override suspend fun createAdminReservation(request: AdminReservationRequest): ReservationDto {
        val dto = request.toDto()
        val customer = customerRepo.get(request.customerId)
            ?: throw IllegalArgumentException("Nie znaleziono klienta")
        val trailer = trailersRepo.getTrailer(request.trailerId)
            ?: throw IllegalArgumentException("Nie znaleziono przyczepki")
        assertDatesAvailable(request.trailerId, request.startDate, request.endDate)
        val expected = calculatePrice(dto).reservationPrice
            ?: throw IllegalStateException("Nie udało się obliczyć ceny")
        val created = reservationRepo.createReservation(
            Reservation(
                customer = customer,
                trailer = trailer,
                startDate = request.startDate,
                endDate = request.endDate,
                reservationPrice = expected,
            )
        ) ?: throw IllegalStateException("Nie udało się utworzyć rezerwacji")
        return created.toAdminDto()
    }

    override suspend fun updateAdminReservation(id: String, request: AdminReservationRequest): ReservationDto? {
        reservationRepo.getReservationById(id) ?: return null
        val customer = customerRepo.get(request.customerId)
            ?: throw IllegalArgumentException("Nie znaleziono klienta")
        val trailer = trailersRepo.getTrailer(request.trailerId)
            ?: throw IllegalArgumentException("Nie znaleziono przyczepki")
        assertDatesAvailable(request.trailerId, request.startDate, request.endDate, excludeId = id)
        val expected = calculatePrice(request.toDto()).reservationPrice
            ?: throw IllegalStateException("Nie udało się obliczyć ceny")
        return reservationRepo.updateReservation(
            Reservation(
                id = id,
                customer = customer,
                trailer = trailer,
                startDate = request.startDate,
                endDate = request.endDate,
                reservationPrice = expected,
            )
        )?.toAdminDto()
    }

    override suspend fun deleteReservation(id: String): Boolean {
        if (id.isBlank()) return false
        return reservationRepo.deleteReservation(id)
    }

    override suspend fun dtoToReservation(dto: ReservationDto): Reservation {
        throw NotImplementedError("dtoToReservation is unused")
    }

    private suspend fun assertDatesAvailable(
        trailerId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        excludeId: String? = null,
    ) {
        if (endDate < startDate) throw IllegalArgumentException("Data końcowa jest wcześniejsza niż początkowa")
        val conflicting = reservationRepo.checkReservationDates(trailerId, startDate, endDate, excludeId)
        if (conflicting != null) {
            throw IllegalArgumentException("Termin jest zajęty: ${conflicting.startDate}–${conflicting.endDate}")
        }
    }

    private fun AdminReservationRequest.toDto(): ReservationDto = ReservationDto(
        customerId = customerId,
        trailerId = trailerId,
        startDate = startDate,
        endDate = endDate,
    )

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
