package com.example.przyczepki_landingpage.repo

import kotlin.time.Instant

interface GateEventRepo {
    suspend fun getLastOpenEvent(customerId: String): Instant?
    suspend fun logOpenEvent(customerId: String, reservationId: String)
}
