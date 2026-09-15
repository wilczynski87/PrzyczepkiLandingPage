package com.example.przyczepki_landingpage.support

import com.example.przyczepki_landingpage.data.dto.SendEmailResponse
import com.example.przyczepki_landingpage.service.EmailService
import pl.przyczepki.email.api.dto.AccountConfirmationData
import pl.przyczepki.email.api.dto.ReservationConfirmationData

class FakeEmailService : EmailService {
    override suspend fun sendEmailConfirmationRequest(
        to: String,
        subject: String,
        body: AccountConfirmationData,
    ): SendEmailResponse = SendEmailResponse(status = "ok", to = to)

    override suspend fun sendPasswordReset(
        to: String,
        subject: String,
        body: String,
    ): SendEmailResponse = SendEmailResponse(status = "ok", to = to)

    override suspend fun sendReservationConfirmation(
        to: String,
        subject: String,
        body: ReservationConfirmationData,
    ): SendEmailResponse = SendEmailResponse(status = "ok", to = to)
}
