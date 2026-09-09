package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.OpenGateResponse
import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.modules.GateConfig
import com.example.przyczepki_landingpage.repo.GateEventRepo
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.service.GateAccessDeniedException
import com.example.przyczepki_landingpage.service.GateService
import com.example.przyczepki_landingpage.service.SuplaTokenProvider
import com.example.przyczepki_landingpage.service.startOfTheDay
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class GateServiceImpl(
    private val client: HttpClient,
    private val gateConfig: GateConfig,
    private val reservationRepo: ReservationRepo,
    private val gateEventRepo: GateEventRepo,
    private val suplaTokenProvider: SuplaTokenProvider,
) : GateService {

    override fun checkUserAuthenticated(customerId: String?): String {
        if (customerId.isNullOrBlank()) {
            throw GateAccessDeniedException("Użytkownik nie jest zalogowany")
        }
        return customerId
    }

    override suspend fun checkUserHasReservation(
        customerId: String,
        reservationId: String?,
    ): Reservation {
        val today = startOfTheDay()
        val activeReservations = reservationRepo.getActiveReservationsForCustomer(customerId, today)

        if (activeReservations.isEmpty()) {
            throw GateAccessDeniedException("Brak aktywnej rezerwacji na dziś")
        }

        return resolveReservation(activeReservations, reservationId)
    }

    override suspend fun ensureCooldown(customerId: String) {
        val lastOpen = gateEventRepo.getLastOpenEvent(customerId) ?: return
        val elapsed = Clock.System.now() - lastOpen
        val cooldown = gateConfig.cooldownSeconds.seconds

        if (elapsed < cooldown) {
            val remaining = (cooldown - elapsed).inWholeSeconds
            throw GateAccessDeniedException("Poczekaj $remaining s przed kolejnym otwarciem bramy")
        }
    }

    override suspend fun openGate(): OpenGateResponse {
        triggerGate()
        return OpenGateResponse(
            success = true,
            message = "Brama została otwarta",
        )
    }

    override suspend fun logOpenEvent(customerId: String, reservationId: String) {
        gateEventRepo.logOpenEvent(customerId = customerId, reservationId = reservationId)
    }

    private fun resolveReservation(
        activeReservations: List<Reservation>,
        reservationId: String?,
    ): Reservation {
        if (reservationId != null) {
            return activeReservations.find { it.id == reservationId }
                ?: throw GateAccessDeniedException("Brak aktywnej rezerwacji o podanym id")
        }

        if (activeReservations.size > 1) {
            throw GateAccessDeniedException("Masz kilka aktywnych rezerwacji – podaj reservationId")
        }

        return activeReservations.single()
    }

    private suspend fun triggerGate() {
//        if (gateConfig.mockMode) {
//            println("Gate mock mode enabled – skipping hardware call")
//            return
//        }

        require(gateConfig.openUrl.isNotBlank()) { "GATE_OPEN_URL is missing" }

        val method = when (gateConfig.method.uppercase()) {
            "GET" -> HttpMethod.Get
            "POST" -> HttpMethod.Post
            "PUT" -> HttpMethod.Put
            "PATCH" -> HttpMethod.Patch
            else -> throw IllegalArgumentException("Nieobsługiwana metoda GATE_OPEN_METHOD: ${gateConfig.method}")
        }

        var accessToken = suplaTokenProvider.getAccessToken()
        var response = executeGateRequest(method, accessToken)

        if (response.status == HttpStatusCode.Unauthorized) {
            accessToken = suplaTokenProvider.getAccessToken(forceRefresh = true)
            response = executeGateRequest(method, accessToken)
        }

        if (!response.status.isSuccess()) {
            val errorBody = runCatching { response.bodyAsText() }.getOrDefault("")
            throw IllegalStateException(
                "Gate hardware error ${response.status} from ${gateConfig.openUrl}: $errorBody".trim()
            )
        }
    }

    private suspend fun executeGateRequest(method: HttpMethod, accessToken: String) =
        client.request(gateConfig.openUrl) {
            this.method = method
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            val body = gateConfig.requestBody
            if (!body.isNullOrBlank() && method != HttpMethod.Get) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }
}
