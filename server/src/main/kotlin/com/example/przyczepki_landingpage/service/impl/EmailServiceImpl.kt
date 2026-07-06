package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.EmailTemplate
import com.example.przyczepki_landingpage.data.dto.SendEmailRequest
import com.example.przyczepki_landingpage.data.dto.SendEmailResponse
import com.example.przyczepki_landingpage.modules.AuthConfig
import com.example.przyczepki_landingpage.modules.EmailConfig
import com.example.przyczepki_landingpage.service.EmailService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.headers
import pl.przyczepki.email.api.dto.AccountConfirmationData

class EmailServiceImpl(
    private val client: HttpClient,
    private val cfgEmail: EmailConfig,
    private val cfgAuth: AuthConfig,
): EmailService {
    val emailUrlBase = "http://${cfgEmail.host}:${cfgEmail.port}"

    override suspend fun sendEmailConfirmationRequest(
        to: String,
        subject: String,
        body: AccountConfirmationData
    ): SendEmailResponse {
        val emailUrl = "$emailUrlBase/emailConfirm"
        val response = client.post(emailUrl) {
            headers{
                append("X-Internal-Api-Key", cfgAuth.internalApiKey)
            }
            setBody(
                SendEmailRequest(to, subject, EmailTemplate.ACCOUNT_CONFIRMATION.templateName, body)
            )
        }
        return response.body()
    }

    override suspend fun sendPasswordReset(
        to: String,
        subject: String,
        body: String
    ): SendEmailResponse {
        TODO("Not yet implemented")
    }

    override suspend fun sendReservationConfirmation(
        to: String,
        subject: String,
        body: String
    ): SendEmailResponse {
        TODO("Not yet implemented")
    }
}