package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.ReservationDto

interface PushNotificationService {
    suspend fun notifyNewReservation(reservation: ReservationDto)
}

object NoOpPushNotificationService : PushNotificationService {
    override suspend fun notifyNewReservation(reservation: ReservationDto) = Unit
}

object ReservationPushCopy {
    const val TITLE = "Nowa rezerwacja"

    fun body(reservation: ReservationDto): String {
        val trailer = reservation.trailerName?.trim()?.ifBlank { null } ?: "Przyczepka"
        val who = reservation.customerName?.trim()?.ifBlank { null }
            ?: reservation.customerEmail?.trim()?.ifBlank { null }
            ?: "klient"
        val dates = listOfNotNull(reservation.startDate, reservation.endDate)
            .distinct()
            .joinToString(" – ")
        return buildString {
            append(who)
            append(" • ")
            append(trailer)
            if (dates.isNotBlank()) {
                append(" • ")
                append(dates)
            }
        }
    }
}
