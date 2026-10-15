package com.example.przyczepki_landingpage.repo.impl

import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.ReservationPrice
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class PendingReservationDocumentTest {

    @Test
    fun `from and toDto preserve top-level couponCode`() {
        val dto = ReservationDto(
            customerId = "customer-1",
            trailerId = "trailer-1",
            startDate = LocalDate(2026, 10, 14),
            endDate = LocalDate(2026, 10, 14),
            couponCode = "GA673654",
            reservationPrice = ReservationPrice(
                trailerId = "trailer-1",
                reservation = 25.0,
                daysNumber = 0,
                sum = 25.0,
                couponCode = "GA673654",
                originalSum = 60.0,
            ),
        )

        val restored = PendingReservationDocument.from(dto).toDto()

        assertEquals("GA673654", restored.couponCode)
        assertEquals(25.0, restored.reservationPrice?.sum)
    }

    @Test
    fun `toDto recovers couponCode from reservationPrice for legacy documents`() {
        val legacy = PendingReservationDocument(
            customerId = "customer-1",
            trailerId = "trailer-1",
            startDate = java.time.LocalDate.of(2026, 10, 14),
            endDate = java.time.LocalDate.of(2026, 10, 14),
            couponCode = null,
            reservationPrice = ReservationPrice(
                trailerId = "trailer-1",
                reservation = 25.0,
                daysNumber = 0,
                sum = 25.0,
                couponCode = "GA673654",
                originalSum = 60.0,
            ),
        )

        assertEquals("GA673654", legacy.toDto().couponCode)
    }
}
