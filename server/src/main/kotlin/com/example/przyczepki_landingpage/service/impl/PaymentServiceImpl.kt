package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.P24Notification
import com.example.przyczepki_landingpage.data.P24TransactionRegisterRequest
import com.example.przyczepki_landingpage.data.P24TransactionRegisterResponse
import com.example.przyczepki_landingpage.data.P24TransactionVerifyRequest
import com.example.przyczepki_landingpage.modules.PaymentConfig
import com.example.przyczepki_landingpage.service.PaymentService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.util.UUID

class PaymentServiceImpl(
    private val client: HttpClient,
    private val paymentConfig: PaymentConfig,
) : PaymentService {

    private val signJson = Json { encodeDefaults = true }

    override suspend fun registerTransaction(
        amount: Int,
        email: String,
        description: String,
    ): String {
        require(amount > 0) { "Kwota musi być większa od zera" }
        require(email.isNotBlank()) { "Email jest wymagany" }

        val sessionId = UUID.randomUUID().toString()
        val sign = signRegister(sessionId = sessionId, amount = amount, currency = CURRENCY)

        val response = client.post(REGISTER_URL) {
            basicAuth(paymentConfig.posId.toString(), paymentConfig.secretId)
            contentType(ContentType.Application.Json)
            setBody(
                P24TransactionRegisterRequest(
                    merchantId = paymentConfig.merchantId,
                    posId = paymentConfig.posId,
                    sessionId = sessionId,
                    amount = amount,
                    currency = CURRENCY,
                    description = description,
                    email = email.trim(),
                    country = "PL",
                    language = "pl",
                    urlReturn = paymentConfig.urlReturn,
                    urlStatus = paymentConfig.urlStatus,
                    sign = sign,
                )
            )
        }

        if (!response.status.isSuccess()) {
            val errorBody = runCatching { response.bodyAsText() }.getOrDefault("")
            throw IllegalStateException("P24 register error ${response.status}: $errorBody".trim())
        }

        val body = response.body<P24TransactionRegisterResponse>()
        if (body.responseCode != 0) {
            throw IllegalStateException(body.error ?: "P24 register failed with code ${body.responseCode}")
        }

        return body.data?.token
            ?: throw IllegalStateException("P24 register response does not contain token")
    }

    override suspend fun verifyTransaction(
        sessionId: String,
        orderId: Long,
        amount: Int,
        currency: String,
    ) {
        val sign = signVerify(sessionId = sessionId, orderId = orderId, amount = amount, currency = currency)

        val response = client.put(VERIFY_URL) {
            basicAuth(paymentConfig.posId.toString(), paymentConfig.secretId)
            contentType(ContentType.Application.Json)
            setBody(
                P24TransactionVerifyRequest(
                    merchantId = paymentConfig.merchantId,
                    posId = paymentConfig.posId,
                    sessionId = sessionId,
                    amount = amount,
                    currency = currency,
                    orderId = orderId,
                    sign = sign,
                )
            )
        }

        if (!response.status.isSuccess()) {
            val errorBody = runCatching { response.bodyAsText() }.getOrDefault("")
            throw IllegalStateException("P24 verify error ${response.status}: $errorBody".trim())
        }
    }

    override fun paymentRedirectUrl(token: String): String = "$REDIRECT_URL$token"

    override suspend fun handlePaymentNotification(notification: P24Notification) {
        if (!verifyNotificationSign(notification)) {
            throw IllegalArgumentException("Nieprawidłowy podpis notyfikacji P24")
        }
        verifyTransaction(
            sessionId = notification.sessionId,
            orderId = notification.orderId,
            amount = notification.amount,
            currency = notification.currency,
        )
    }

    private fun verifyNotificationSign(notification: P24Notification): Boolean {
        val expected = signNotification(notification)
        return MessageDigest.isEqual(
            expected.toByteArray(Charsets.UTF_8),
            notification.sign.toByteArray(Charsets.UTF_8),
        )
    }

    private fun signRegister(sessionId: String, amount: Int, currency: String): String {
        val payload = P24RegisterSignPayload(
            sessionId = sessionId,
            merchantId = paymentConfig.merchantId,
            amount = amount,
            currency = currency,
            crc = paymentConfig.crc,
        )
        return sha384(signJson.encodeToString(payload))
    }

    private fun signVerify(sessionId: String, orderId: Long, amount: Int, currency: String): String {
        val payload = P24VerifySignPayload(
            sessionId = sessionId,
            orderId = orderId,
            amount = amount,
            currency = currency,
            crc = paymentConfig.crc,
        )
        return sha384(signJson.encodeToString(payload))
    }

    private fun signNotification(notification: P24Notification): String {
        val payload = P24NotificationSignPayload(
            merchantId = notification.merchantId,
            posId = notification.posId,
            sessionId = notification.sessionId,
            amount = notification.amount,
            currency = notification.currency,
            orderId = notification.orderId,
            crc = paymentConfig.crc,
        )
        return sha384(signJson.encodeToString(payload))
    }

    private fun sha384(data: String): String {
        val digest = MessageDigest.getInstance("SHA-384")
            .digest(data.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    @Serializable
    private data class P24RegisterSignPayload(
        val sessionId: String,
        val merchantId: Int,
        val amount: Int,
        val currency: String,
        val crc: String,
    )

    @Serializable
    private data class P24VerifySignPayload(
        val sessionId: String,
        val orderId: Long,
        val amount: Int,
        val currency: String,
        val crc: String,
    )

    @Serializable
    private data class P24NotificationSignPayload(
        val merchantId: Int,
        val posId: Int,
        val sessionId: String,
        val amount: Int,
        val currency: String,
        val orderId: Long,
        val crc: String,
    )

    companion object {
        private const val CURRENCY = "PLN"
        private const val REGISTER_URL = "https://secure.przelewy24.pl/api/v1/transaction/register"
        private const val VERIFY_URL = "https://secure.przelewy24.pl/api/v1/transaction/verify"
        private const val REDIRECT_URL = "https://secure.przelewy24.pl/trnRequest/"
    }
}
