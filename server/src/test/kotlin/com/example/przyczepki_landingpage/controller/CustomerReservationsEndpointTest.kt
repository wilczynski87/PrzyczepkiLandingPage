package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.service.auth.JwtService
import com.example.przyczepki_landingpage.support.FakeReservationRepo
import com.example.przyczepki_landingpage.support.ReservationTestFixtures
import com.example.przyczepki_landingpage.support.installMyReservationsTestDependencies
import com.example.przyczepki_landingpage.support.testAuthConfig
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CustomerReservationsEndpointTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    private val jwtService = JwtService(testAuthConfig)

    private fun token(userId: String): String =
        jwtService.generateToken(Customer(id = userId))

    @Test
    fun `GET mine without token returns 401`() = testApplication {
        application { installMyReservationsTestDependencies() }

        val response = client.get("/reservation/mine")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `GET mine returns only authenticated customer reservations`() = testApplication {
        val repo = FakeReservationRepo()
        application { installMyReservationsTestDependencies(repo) }
        repo.addReservation(
            ReservationTestFixtures.existingReservation(customerId = "c1", id = "r-own"),
        )
        repo.addReservation(
            ReservationTestFixtures.existingReservation(
                customerId = "c2",
                id = "r-other",
                startDate = LocalDate(2025, 8, 1),
                endDate = LocalDate(2025, 8, 2),
            ),
        )

        val response = client.get("/reservation/mine") {
            header(HttpHeaders.Authorization, "Bearer ${token("c1")}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<List<ReservationDto>>(response.bodyAsText())
        assertEquals(1, body.size)
        assertEquals("r-own", body.first().id)
        assertEquals("c1", body.first().customerId)
        assertEquals("Test trailer", body.first().trailerName)
    }

    @Test
    fun `GET mine returns empty list when customer has no reservations`() = testApplication {
        application { installMyReservationsTestDependencies() }

        val response = client.get("/reservation/mine") {
            header(HttpHeaders.Authorization, "Bearer ${token("c1")}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<List<ReservationDto>>(response.bodyAsText())
        assertTrue(body.isEmpty())
    }
}
