package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.OpenGateResponse
import com.example.przyczepki_landingpage.data.Reservation

interface GateService {
    /** Sprawdza, czy caller jest zalogowany (ma niepusty customerId z JWT). */
    fun checkUserAuthenticated(customerId: String?): String

    /** Sprawdza aktywną rezerwację na dziś; zwraca wybraną rezerwację. */
    suspend fun checkUserHasReservation(
        customerId: String,
        reservationId: String? = null,
    ): Reservation

    /** Sprawdza cooldown między kolejnymi otwarciami. */
    suspend fun ensureCooldown(customerId: String)

    /** Tylko otwiera bramę (wywołanie SUPLA / hardware). */
    suspend fun openGate(): OpenGateResponse

    /** Zapisuje zdarzenie otwarcia (audyt). */
    suspend fun logOpenEvent(customerId: String, reservationId: String)
}
