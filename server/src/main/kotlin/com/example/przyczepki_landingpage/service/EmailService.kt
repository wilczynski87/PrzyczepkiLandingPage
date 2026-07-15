package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.dto.SendEmailResponse
import pl.przyczepki.email.api.dto.AccountConfirmationData
import pl.przyczepki.email.api.dto.ReservationConfirmationData

interface EmailService {
    suspend fun sendEmailConfirmationRequest(to: String, subject: String = "Potwierdzenie rejestracji na PrzyczepkiFAT.pl", body: AccountConfirmationData): SendEmailResponse
    suspend fun sendPasswordReset(to: String, subject: String, body: String): SendEmailResponse
    suspend fun sendReservationConfirmation(
        to: String,
        subject: String = "Potwierdzenie rezerwacji na PrzyczepkiFAT.pl",
        body: ReservationConfirmationData,
    ): SendEmailResponse
}