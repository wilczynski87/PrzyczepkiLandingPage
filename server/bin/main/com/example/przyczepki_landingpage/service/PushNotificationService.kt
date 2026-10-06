package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.ReservationDto
import kotlinx.serialization.Serializable

interface PushNotificationService {
    suspend fun notifyNewReservation(reservation: ReservationDto)
    suspend fun sendTest(): PushSendResult
}

object NoOpPushNotificationService : PushNotificationService {
    override suspend fun notifyNewReservation(reservation: ReservationDto) = Unit
    override suspend fun sendTest() = PushSendResult(
        sent = false,
        message = "FCM wyłączony (NoOp)",
    )
}

@Serializable
data class PushSendResult(
    val sent: Boolean,
    val message: String,
)

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
