package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.P24Notification
import com.example.przyczepki_landingpage.data.P24TransactionRegisterRequest
import com.example.przyczepki_landingpage.data.P24TransactionRegisterResponse
import com.example.przyczepki_landingpage.data.P24TransactionVerifyRequest
import com.example.przyczepki_landingpage.data.PaymentSessionStatus
import com.example.przyczepki_landingpage.data.PaymentStatusResponse
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.modules.PaymentConfig
import com.example.przyczepki_landingpage.repo.PendingPaymentRepo
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.repo.impl.PendingPaymentTable
import com.example.przyczepki_landingpage.service.PaymentRegistration
import com.example.przyczepki_landingpage.service.PaymentService
import com.example.przyczepki_landingpage.service.ReservationService
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
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.util.UUID

class PaymentServiceImpl(
    private val client: HttpClient,
    private val paymentConfig: PaymentConfig,
    private val pendingPaymentRepo: PendingPaymentRepo,
    private val reservationService: ReservationService,
    private val reservationRepo: ReservationRepo,
) : PaymentService {

    private val signJson = Json { encodeDefaults = true }

    override suspend fun registerTransaction(
        amount: Int,
        customer: Customer,
        reservation: ReservationDto,
        description: String,
        regulationAccept: Boolean,
    ): PaymentRegistration {
        require(amount > 0) { "Kwota musi być większa od zera" }
        require(customer.getEmail()?.isNotBlank() ?: false) { "Email jest wymagany" }
        require(!reservation.trailerId.isNullOrBlank()) { "Brak przyczepy w rezerwacji" }
        require(reservation.startDate != null && reservation.endDate != null) { "Brak dat rezerwacji" }

        val sessionId = UUID.randomUUID().toString()
        val sign = signRegister(sessionId = sessionId, amount = amount, currency = CURRENCY)

        val response = client.post("${paymentConfig.apiBaseUrl}/transaction/register") {
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
                    email = customer.getEmail()!!,
                    client = customer.getName(),
                    address = customer.getAddress(),
                    country = "PL",
                    language = "pl",
                    urlReturn = paymentConfig.urlReturn,
                    urlStatus = paymentConfig.urlStatus,
                    sign = sign,
                    phone = customer.private?.phoneNumber,
                    regulationAccept = regulationAccept,
                    transferLabel = "${customer.getEmail()}, $sessionId".substring(0, 19),
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

        val token = body.data?.token
            ?: throw IllegalStateException("P24 register response does not contain token")

        pendingPaymentRepo.save(
            PendingPaymentTable(
                sessionId = sessionId,
                reservation = reservation,
                customerId = customer.id ?: reservation.customerId,
                amount = amount,
                status = PaymentSessionStatus.PENDING,
            )
        )

        return PaymentRegistration(token = token, sessionId = sessionId)
    }

    override suspend fun verifyTransaction(
        sessionId: String,
        orderId: Long,
        amount: Int,
        currency: String,
    ) {
        val sign = signVerify(sessionId = sessionId, orderId = orderId, amount = amount, currency = currency)

        val response = client.put("${paymentConfig.apiBaseUrl}/transaction/verify") {
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

    override fun paymentRedirectUrl(token: String): String = "${paymentConfig.redirectBaseUrl}$token"

    override suspend fun handlePaymentNotification(notification: P24Notification) {
        if (!verifyNotificationSign(notification)) {
            val expectedSign = signNotification(notification)
            println(
                "P24 notification sign mismatch. expected=$expectedSign received=${notification.sign}"
            )
            throw IllegalArgumentException("Nieprawidłowy podpis notyfikacji P24")
        }

        val pending = pendingPaymentRepo.findBySessionId(notification.sessionId)
            ?: throw IllegalArgumentException("Nie znaleziono sesji płatności: ${notification.sessionId}")

        if (pending.status == PaymentSessionStatus.COMPLETED) {
            return
        }

        verifyTransaction(
            sessionId = notification.sessionId,
            orderId = notification.orderId,
            amount = notification.amount,
            currency = notification.currency,
        )

        pendingPaymentRepo.updateStatus(
            sessionId = notification.sessionId,
            status = PaymentSessionStatus.VERIFIED,
            orderId = notification.orderId,
        )

        try {
            val reservationDto = pending.reservation.copy(
                customerId = pending.customerId ?: pending.reservation.customerId,
            )
            val created = reservationService.createReservation(reservationDto)
                ?: throw IllegalStateException("Nie udało się utworzyć rezerwacji")

            pendingPaymentRepo.updateStatus(
                sessionId = notification.sessionId,
                status = PaymentSessionStatus.COMPLETED,
                orderId = notification.orderId,
                reservationId = created.id,
            )
        } catch (e: Exception) {
            pendingPaymentRepo.updateStatus(
                sessionId = notification.sessionId,
                status = PaymentSessionStatus.FAILED,
                orderId = notification.orderId,
                errorMessage = e.message,
            )
            throw e
        }
    }

    override suspend fun getPaymentStatus(sessionId: String): PaymentStatusResponse {
        val pending = pendingPaymentRepo.findBySessionId(sessionId)
            ?: return PaymentStatusResponse(
                sessionId = sessionId,
                status = PaymentSessionStatus.FAILED,
                message = "Nie znaleziono sesji płatności",
            )

        val reservation = when {
            pending.status == PaymentSessionStatus.COMPLETED && pending.reservationId != null ->
                reservationRepo.getReservationById(pending.reservationId!!)?.toDto()
            else -> pending.reservation
        }

        return PaymentStatusResponse(
            sessionId = sessionId,
            status = pending.status,
            reservation = reservation,
            message = pending.errorMessage,
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
            originAmount = notification.originAmount ?: notification.amount,
            currency = notification.currency,
            orderId = notification.orderId,
            methodId = notification.methodId ?: 0,
            statement = notification.statement.orEmpty(),
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
        val originAmount: Int,
        val currency: String,
        val orderId: Long,
        val methodId: Int,
        val statement: String,
        val crc: String,
    )

    companion object {
        private const val CURRENCY = "PLN"
    }
}
