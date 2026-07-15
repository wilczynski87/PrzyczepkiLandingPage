package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.dto.SendEmailResponse
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.service.EmailService
import com.example.przyczepki_landingpage.service.ReservationConfirmationService
import io.ktor.server.plugins.NotFoundException
import pl.przyczepki.email.api.dto.ReservationConfirmationData
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ReservationConfirmationServiceImpl(
    private val reservationRepo: ReservationRepo,
    private val emailService: EmailService,
) : ReservationConfirmationService {

    override suspend fun reservationConfirmationData(
        reservationId: String,
        paidAmountGrosze: Int?,
        orderId: Long?,
    ): ReservationConfirmationData {
        val reservation = reservationRepo.getReservationById(reservationId)
            ?: throw NotFoundException("Nie znaleziono rezerwacji: $reservationId")

        val customer = reservation.customer
            ?: throw IllegalStateException("Rezerwacja $reservationId nie ma przypisanego klienta")

        val trailer = reservation.trailer
            ?: throw IllegalStateException("Rezerwacja $reservationId nie ma przypisanej przyczepy")

        val email = customer.getEmail()
            ?: throw IllegalStateException("Klient rezerwacji $reservationId nie ma adresu email")

        val startDate = reservation.startDate?.toString()
            ?: throw IllegalStateException("Rezerwacja $reservationId nie ma daty rozpoczęcia")

        val endDate = reservation.endDate?.toString()
            ?: throw IllegalStateException("Rezerwacja $reservationId nie ma daty zakończenia")

        val prices = reservation.reservationPrice
        val rentalAmount = prices?.sum
        val reservationFee = prices?.reservation
        val totalAmount = when {
            rentalAmount != null && reservationFee != null -> rentalAmount + reservationFee
            rentalAmount != null -> rentalAmount
            else -> reservationFee
        }

        return ReservationConfirmationData(
            reservationId = reservationId,
            recipientName = customer.getName(),
            email = email,
            trailerName = trailer.name ?: "Przyczepa",
            trailerSize = trailer.size,
            startDate = startDate,
            endDate = endDate,
            daysNumber = prices?.daysNumber,
            reservationFee = reservationFee,
            rentalAmount = rentalAmount,
            totalAmount = totalAmount,
            paidAmount = paidAmountGrosze?.let(::formatPlnFromGrosze),
            orderId = orderId?.toString(),
        )
    }

    override suspend fun sendReservationConfirmation(
        reservationId: String,
        paidAmountGrosze: Int?,
        orderId: Long?,
    ): SendEmailResponse {
        val data = reservationConfirmationData(
            reservationId = reservationId,
            paidAmountGrosze = paidAmountGrosze,
            orderId = orderId,
        )

        return emailService.sendReservationConfirmation(
            to = data.email,
            body = data,
        )
    }

    private fun formatPlnFromGrosze(amountGrosze: Int): String {
        val amount = amountGrosze / 100.0
        val symbols = DecimalFormatSymbols(Locale.of("pl", "PL")).apply {
            decimalSeparator = ','
            groupingSeparator = ' '
        }
        return DecimalFormat("#,##0.00 zł", symbols).format(amount)
    }
}
