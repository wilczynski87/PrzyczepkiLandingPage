package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.support.FakeReservationRepo
import com.example.przyczepki_landingpage.support.FakeTrailersRepo
import com.example.przyczepki_landingpage.support.ReservationTestFixtures
import com.example.przyczepki_landingpage.support.installReservationCheckTestDependencies
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReservationCheckEndpointTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Test
    fun `POST reservation check returns priced reservation when dates are available`() = testApplication {
        val reservationRepo = FakeReservationRepo()
        val trailersRepo = FakeTrailersRepo()
        application {
            installReservationCheckTestDependencies(reservationRepo, trailersRepo)
        }
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val request = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 10),
            endDate = LocalDate(2025, 6, 12),
        )

        val response = client.post("/reservation/check") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<ReservationDto>(response.bodyAsText())
        assertEquals(ReservationTestFixtures.TRAILER_ID, body.trailerId)
        assertEquals(LocalDate(2025, 6, 10), body.startDate)
        assertEquals(LocalDate(2025, 6, 12), body.endDate)
        assertEquals(2L, body.reservationPrice?.daysNumber)
        assertEquals(230.0, body.reservationPrice?.sum)
        assertEquals(30.0, body.reservationPrice?.reservation)
    }

    @Test
    fun `POST reservation check returns 406 when trailer does not exist`() = testApplication {
        application {
            installReservationCheckTestDependencies()
        }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val request = ReservationTestFixtures.reservationDto(trailerId = "unknown-trailer")

        val response = client.post("/reservation/check") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.NotAcceptable, response.status)
        assertTrue(
            response.bodyAsText().contains("Nie znaleziono sprzętu o takim id: unknown-trailer"),
        )
    }

    @Test
    fun `POST reservation check returns error when dates collide with existing reservation`() = testApplication {
        val reservationRepo = FakeReservationRepo()
        val trailersRepo = FakeTrailersRepo()
        application {
            installReservationCheckTestDependencies(reservationRepo, trailersRepo)
        }
        trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        reservationRepo.addReservation(ReservationTestFixtures.existingReservation())

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val request = ReservationTestFixtures.reservationDto(
            startDate = LocalDate(2025, 6, 11),
            endDate = LocalDate(2025, 6, 11),
        )

        val response = client.post("/reservation/check") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("in collision"))
    }

    @Test
    fun `POST reservation check returns error when price cannot be calculated`() = testApplication {
        val reservationRepo = FakeReservationRepo()
        val trailersRepo = FakeTrailersRepo()
        application {
            installReservationCheckTestDependencies(reservationRepo, trailersRepo)
        }
        trailersRepo.addTrailer(ReservationTestFixtures.trailer(prices = null))

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val request = ReservationTestFixtures.reservationDto()

        val response = client.post("/reservation/check") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("Trailer prices not found"))
    }
}
