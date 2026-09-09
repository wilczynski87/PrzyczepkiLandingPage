package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.P24Notification
import com.example.przyczepki_landingpage.data.PaymentSessionStatus
import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.modules.PaymentConfig
import com.example.przyczepki_landingpage.repo.PendingPaymentRepo
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.repo.impl.PendingPaymentTable
import com.example.przyczepki_landingpage.service.ReservationConfirmationService
import com.example.przyczepki_landingpage.service.ReservationService
import com.example.przyczepki_landingpage.data.dto.SendEmailResponse
import io.ktor.client.HttpClient
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertTrue

class PaymentNotificationSignTest {

  @Test
  fun `notification sign matches documented sha384 payload`() {
    val service = paymentService()
    val notification = P24Notification(
      merchantId = 405228,
      posId = 405228,
      sessionId = "test-session",
      amount = 10000,
      originAmount = 10000,
      currency = "PLN",
      orderId = 123456,
      methodId = 25,
      statement = "test",
      sign = "50a0efae6b4ee8506f0dbcf5931e32a8aeca5b29c1063e975bae87bc0bd49480ecbbda199e308e7eb5bd5de1c0a6531b",
    )

    assertTrue(service.verifyNotificationSignForTest(notification))
  }

  private fun paymentService() = PaymentServiceImpl(
    client = HttpClient(),
    paymentConfig = PaymentConfig(
      merchantId = 405228,
      posId = 405228,
      secretId = "secret",
      crc = "5a1ec3d434f2995b",
      urlReturn = "https://example.com/return",
      urlStatus = "https://example.com/notification",
      apiBaseUrl = "https://secure.przelewy24.pl/api/v1",
      redirectBaseUrl = "https://secure.przelewy24.pl/trnRequest/",
      mockMode = false,
    ),
    pendingPaymentRepo = object : PendingPaymentRepo {
      override suspend fun save(pending: PendingPaymentTable) = pending
      override suspend fun findBySessionId(sessionId: String) = null
      override suspend fun updateStatus(sessionId: String, status: PaymentSessionStatus, orderId: Long?, reservationId: String?, errorMessage: String?) = null
    },
    reservationService = object : ReservationService {
      override suspend fun getReservations(from: LocalDate, to: LocalDate?) = emptyList<ReservationDto>()
      override suspend fun checkReservation(reservation: ReservationDto) = reservation
      override suspend fun calculatePrice(reservation: ReservationDto) = reservation
      override suspend fun createReservation(reservation: ReservationDto) = reservation
      override suspend fun deleteReservation(id: Long) = false
      override suspend fun dtoToReservation(dto: ReservationDto) = throw NotImplementedError()
    },
    reservationRepo = object : ReservationRepo {
      override suspend fun getAllReservations(from: LocalDate, to: LocalDate?) = emptyList<Reservation>()
      override suspend fun getReservationById(id: String) = null
      override suspend fun createReservation(reservation: Reservation) = reservation
      override suspend fun deleteReservation(id: String) = false
      override suspend fun checkReservationDates(trailerId: String, from: LocalDate, to: LocalDate) = null
      override suspend fun getActiveReservationsForCustomer(customerId: String, date: LocalDate) = emptyList<Reservation>()
    },
    reservationConfirmationService = object : ReservationConfirmationService {
      override suspend fun reservationConfirmationData(reservationId: String, paidAmountGrosze: Int?, orderId: Long?) =
        throw NotImplementedError()
      override suspend fun sendReservationConfirmation(reservationId: String, paidAmountGrosze: Int?, orderId: Long?) =
        SendEmailResponse(status = "ok", to = "test@example.com")
    },
  )

  private fun PaymentServiceImpl.verifyNotificationSignForTest(notification: P24Notification): Boolean {
    val method = PaymentServiceImpl::class.java.getDeclaredMethod(
      "verifyNotificationSign",
      P24Notification::class.java,
    )
    method.isAccessible = true
    return method.invoke(this, notification) as Boolean
  }
}
