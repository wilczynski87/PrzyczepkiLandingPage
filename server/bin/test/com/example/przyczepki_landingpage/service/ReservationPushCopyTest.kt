package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.ReservationDto
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class ReservationPushCopyTest {

    @Test
    fun `body includes customer trailer and dates`() {
        val body = ReservationPushCopy.body(
            ReservationDto(
                customerName = "Jan Kowalski",
                trailerName = "Brenderup",
                startDate = LocalDate(2026, 10, 10),
                endDate = LocalDate(2026, 10, 12),
            ),
        )

        assertEquals("Jan Kowalski • Brenderup • 2026-10-10 – 2026-10-12", body)
    }

    @Test
    fun `body falls back when customer and trailer are missing`() {
        val body = ReservationPushCopy.body(ReservationDto())

        assertEquals("klient • Przyczepka", body)
    }
}
