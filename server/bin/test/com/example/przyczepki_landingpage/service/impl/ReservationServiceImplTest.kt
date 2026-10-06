package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.dto.Coupon
import com.example.przyczepki_landingpage.data.dto.CouponStatus
import com.example.przyczepki_landingpage.data.dto.Discount
import com.example.przyczepki_landingpage.support.FakeCouponRepo
import com.example.przyczepki_landingpage.support.FakeCustomerRepo
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
    private val customerRepo = FakeCustomerRepo()
    private val couponRepo = FakeCouponRepo()
    private val service = ReservationServiceImpl(
        reservationRepo,
        trailersRepo,
        customerRepo,
        CouponServiceImpl(couponRepo),
    )

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
    fun `calculatePrice for single day uses half day rate`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 10),
        )

        val result = service.calculatePrice(reservation)

        assertEquals(0L, result.reservationPrice?.daysNumber)
        assertEquals(30.0, result.reservationPrice?.reservation)
        assertEquals(60.0, result.reservationPrice?.sum)
        assertEquals(ReservationTestFixtures.TRAILER_ID, result.reservationPrice?.trailerId)
    }

    @Test
    fun `calculatePrice for overnight span uses one full day rate`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 11),
        )

        val result = service.calculatePrice(reservation)

        assertEquals(1L, result.reservationPrice?.daysNumber)
        assertEquals(100.0, result.reservationPrice?.sum)
    }

    @Test
    fun `calculatePrice for two full days sums first and second day rates`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 12),
        )

        val result = service.calculatePrice(reservation)

        assertEquals(2L, result.reservationPrice?.daysNumber)
        assertEquals(180.0, result.reservationPrice?.sum)
    }

    @Test
    fun `calculatePrice for three full days includes other day rate`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 13),
        )

        val result = service.calculatePrice(reservation)

        assertEquals(3L, result.reservationPrice?.daysNumber)
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
    fun `calculatePrice throws when half day price is missing for single day`() = runBlocking {
        trailersRepo.addTrailer(
            ReservationTestFixtures.trailer(
                prices = ReservationTestFixtures.samplePrices.copy(halfDay = null),
            ),
        )

        val error = assertFailsWith<Exception> {
            service.calculatePrice(ReservationTestFixtures.reservationDto())
        }

        assertEquals("Trailer half day price not found", error.message)
    }

    @Test
    fun `calculatePrice throws when first day price is missing for multi day range`() = runBlocking {
        trailersRepo.addTrailer(
            ReservationTestFixtures.trailer(
                prices = ReservationTestFixtures.samplePrices.copy(firstDay = null),
            ),
        )
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 11),
        )

        val error = assertFailsWith<Exception> {
            service.calculatePrice(reservation)
        }

        assertEquals("Trailer first day price not found", error.message)
    }

    @Test
    fun `calculatePrice throws when second day price is missing for two full days`() = runBlocking {
        trailersRepo.addTrailer(
            ReservationTestFixtures.trailer(
                prices = ReservationTestFixtures.samplePrices.copy(secondDay = null),
            ),
        )
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 12),
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
            endDate = LocalDate(2025, 6, 13),
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

    @Test
    fun `calculatePrice works across year boundary`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 12, 31),
            endDate = LocalDate(2026, 1, 2),
        )

        val result = service.calculatePrice(reservation)

        assertEquals(2L, result.reservationPrice?.daysNumber)
        assertEquals(180.0, result.reservationPrice?.sum)
    }

    @Test
    fun `createReservation rejects mismatched price`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        customerRepo.addCustomer(
            com.example.przyczepki_landingpage.data.Customer(id = "customer-1"),
        )
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 10),
        ).copy(
            customerId = "customer-1",
            reservationPrice = com.example.przyczepki_landingpage.data.ReservationPrice(
                trailerId = ReservationTestFixtures.TRAILER_ID,
                reservation = 30.0,
                daysNumber = 0,
                sum = 1.0,
            ),
        )

        val error = assertFailsWith<Exception> {
            service.createReservation(reservation)
        }

        assertEquals(true, error.message?.contains("Reservation price mismatch") == true)
    }

    @Test
    fun `createReservation sends push notification`() = runBlocking {
        val pushes = mutableListOf<com.example.przyczepki_landingpage.data.ReservationDto>()
        val local = ReservationServiceImpl(
            reservationRepo,
            trailersRepo,
            customerRepo,
            CouponServiceImpl(couponRepo),
            object : com.example.przyczepki_landingpage.service.PushNotificationService {
                override suspend fun notifyNewReservation(
                    reservation: com.example.przyczepki_landingpage.data.ReservationDto,
                ) {
                    pushes += reservation
                }
            },
        )
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        customerRepo.addCustomer(
            com.example.przyczepki_landingpage.data.Customer(
                id = "customer-1",
                private = com.example.przyczepki_landingpage.data.Private(
                    firstName = "Jan",
                    lastName = "Kowalski",
                    email = "jan@example.com",
                ),
            ),
        )
        val reservation = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 10),
        ).copy(
            customerId = "customer-1",
            reservationPrice = com.example.przyczepki_landingpage.data.ReservationPrice(
                trailerId = ReservationTestFixtures.TRAILER_ID,
                reservation = 30.0,
                daysNumber = 0,
                sum = 60.0,
            ),
        )

        val created = local.createReservation(reservation)

        assertEquals(1, pushes.size)
        assertEquals(created?.id, pushes.single().id)
        assertEquals("Jan Kowalski", pushes.single().customerName)
        assertEquals("Test trailer", pushes.single().trailerName)
    }

    @Test
    fun `getCustomerReservations returns only that customer's bookings`() = runBlocking {
        reservationRepo.addReservation(
            ReservationTestFixtures.existingReservation(customerId = "c1", id = "r1"),
        )
        reservationRepo.addReservation(
            ReservationTestFixtures.existingReservation(
                customerId = "c2",
                id = "r2",
                startDate = LocalDate(2025, 7, 1),
                endDate = LocalDate(2025, 7, 3),
            ),
        )

        val result = service.getCustomerReservations("c1")

        assertEquals(1, result.size)
        assertEquals("r1", result.first().id)
        assertEquals("c1", result.first().customerId)
        assertEquals("Test trailer", result.first().trailerName)
    }

    @Test
    fun `getCustomerReservations returns empty list for unknown customer`() = runBlocking {
        reservationRepo.addReservation(
            ReservationTestFixtures.existingReservation(customerId = "c1", id = "r1"),
        )

        assertEquals(emptyList(), service.getCustomerReservations("missing"))
        assertEquals(emptyList(), service.getCustomerReservations(""))
    }

    @Test
    fun `calculatePrice applies fixed coupon`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        couponRepo.add(
            Coupon(id = "c1", code = "MINUS20", status = CouponStatus.ACTIVE, discount = Discount.Fixed(20.0)),
        )
        val result = service.calculatePrice(
            ReservationTestFixtures.reservationDto().copy(couponCode = "minus20"),
        )
        assertEquals(40.0, result.reservationPrice?.sum)
        assertEquals(30.0, result.reservationPrice?.reservation)
        assertEquals("MINUS20", result.couponCode)
        assertEquals(60.0, result.reservationPrice?.originalSum)
    }

    @Test
    fun `calculatePrice applies percentage coupon`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        couponRepo.add(
            Coupon(id = "c2", code = "HALF", status = CouponStatus.ACTIVE, discount = Discount.Percentage(50.0)),
        )
        val result = service.calculatePrice(
            ReservationTestFixtures.reservationDto().copy(couponCode = "HALF"),
        )
        assertEquals(30.0, result.reservationPrice?.sum)
        assertEquals(15.0, result.reservationPrice?.reservation)
    }

    @Test
    fun `calculatePrice applies fixed price coupon`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        couponRepo.add(
            Coupon(id = "c3", code = "FLAT25", status = CouponStatus.ACTIVE, discount = Discount.FixedPrice(25.0)),
        )
        val result = service.calculatePrice(
            ReservationTestFixtures.reservationDto(
                startDate = LocalDate(2025, 6, 10),
                endDate = LocalDate(2025, 6, 12),
            ).copy(couponCode = "FLAT25"),
        )
        assertEquals(2L, result.reservationPrice?.daysNumber)
        assertEquals(50.0, result.reservationPrice?.sum)
        assertEquals(25.0, result.reservationPrice?.reservation)
    }

    @Test
    fun `calculatePrice rejects used coupon`() = runBlocking {
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        couponRepo.add(
            Coupon(id = "c4", code = "USED", status = CouponStatus.USED, discount = Discount.Fixed(10.0)),
        )
        val error = assertFailsWith<IllegalArgumentException> {
            service.calculatePrice(ReservationTestFixtures.reservationDto().copy(couponCode = "USED"))
        }
        assertEquals("Kupon został już wykorzystany", error.message)
    }
}
