package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.support.FakeReservationRepo
import com.example.przyczepki_landingpage.support.FakeTrailersRepo
import com.example.przyczepki_landingpage.support.ReservationTestFixtures
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ReservationServiceImplTest {

    private val reservationRepo = FakeReservationRepo()
    private val trailersRepo = FakeTrailersRepo()
    private val service = ReservationServiceImpl(reservationRepo, trailersRepo)

    @Test
    fun `checkReservation returns dto when dates are free`() = runBlocking {
        val reservation = ReservationTestFixtures.reservationDto()

        val result = service.checkReservation(reservation)

        assertEquals(reservation, result)
    }

    @Test
    fun `checkReservation throws when dates overlap existing reservation`() = runBlocking {
        reservationRepo.addReservation(ReservationTestFixtures.existingReservation())
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 11),
            endDate = LocalDate(2025, 6, 11),
        )

        val error = assertFailsWith<Exception> {
            service.checkReservation(reservation)
        }

        assertEquals(true, error.message?.contains("in collision") == true)
    }

    @Test
    fun `calculatePrice for single day uses first day rate only`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 10),
        )

        val result = service.calculatePrice(reservation)

        assertEquals(0L, result.reservationPrice?.daysNumber)
        assertEquals(30.0, result.reservationPrice?.reservation)
        assertEquals(100.0, result.reservationPrice?.sum)
        assertEquals(ReservationTestFixtures.TRAILER_ID, result.reservationPrice?.trailerId)
    }

    @Test
    fun `calculatePrice for two day rental sums first and second day rates`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 11),
        )

        val result = service.calculatePrice(reservation)

        assertEquals(1L, result.reservationPrice?.daysNumber)
        assertEquals(180.0, result.reservationPrice?.sum)
    }

    @Test
    fun `calculatePrice for multi day rental sums tiered daily prices`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 12),
        )

        val result = service.calculatePrice(reservation)

        assertEquals(2L, result.reservationPrice?.daysNumber)
        assertEquals(230.0, result.reservationPrice?.sum)
    }

    @Test
    fun `calculatePrice throws when trailer is missing`() = runBlocking {
        val reservation = ReservationTestFixtures.reservationDto(trailerId = "missing")

        val error = assertFailsWith<Exception> {
            service.calculatePrice(reservation)
        }

        assertEquals("Trailer not found", error.message)
    }

    @Test
    fun `calculatePrice throws when trailer has no prices`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer(prices = null))

        val error = assertFailsWith<Exception> {
            service.calculatePrice(ReservationTestFixtures.reservationDto())
        }

        assertEquals("Trailer prices not found", error.message)
    }

    @Test
    fun `calculatePrice throws when first day price is missing`() = runBlocking {
        trailersRepo.addTrailer(
            ReservationTestFixtures.trailer(
                prices = ReservationTestFixtures.samplePrices.copy(firstDay = null),
            ),
        )

        val error = assertFailsWith<Exception> {
            service.calculatePrice(ReservationTestFixtures.reservationDto())
        }

        assertEquals("Trailer first day price not found", error.message)
    }

    @Test
    fun `calculatePrice throws when second day price is missing for multi day range`() = runBlocking {
        trailersRepo.addTrailer(
            ReservationTestFixtures.trailer(
                prices = ReservationTestFixtures.samplePrices.copy(secondDay = null),
            ),
        )
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 11),
        )

        val error = assertFailsWith<Exception> {
            service.calculatePrice(reservation)
        }

        assertEquals("Trailer second day price not found", error.message)
    }

    @Test
    fun `calculatePrice throws when other day price is missing for longer rental`() = runBlocking {
        trailersRepo.addTrailer(
            ReservationTestFixtures.trailer(
                prices = ReservationTestFixtures.samplePrices.copy(otherDays = null),
            ),
        )
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 12),
        )

        val error = assertFailsWith<Exception> {
            service.calculatePrice(reservation)
        }

        assertEquals("Trailer other day price not found", error.message)
    }

    @Test
    fun `calculatePrice keeps original reservation fields`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 10),
        )

        val result = service.calculatePrice(reservation)

        assertEquals(reservation.trailerId, result.trailerId)
        assertEquals(reservation.startDate, result.startDate)
        assertEquals(reservation.endDate, result.endDate)
        assertNull(reservation.reservationPrice)
    }
}
