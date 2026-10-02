package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.dto.Coupon
import com.example.przyczepki_landingpage.data.dto.Discount
import com.example.przyczepki_landingpage.data.AdminReservationRequest
import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.CustomerRegisterRequest
import com.example.przyczepki_landingpage.data.Private
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.service.auth.JwtService
import com.example.przyczepki_landingpage.support.ReservationTestFixtures
import com.example.przyczepki_landingpage.support.installAdminTestDependencies
import com.example.przyczepki_landingpage.support.testAuthConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdminEndpointTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    private val jwtService = JwtService(testAuthConfig)

    private fun token(userId: String): String =
        jwtService.generateToken(Customer(id = userId))

    private fun adminCustomer() = Customer(
        id = "admin-1",
        private = Private(firstName = "Karol", email = "parkingostrowskiego@gmail.com"),
    )

    @Test
    fun `GET admin reservations without token returns 401`() = testApplication {
        application { installAdminTestDependencies() }
        val response = client.get("/admin/reservations")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `GET admin reservations as regular user returns 403`() = testApplication {
        val customerRepo = com.example.przyczepki_landingpage.support.FakeCustomerRepo()
        application {
            installAdminTestDependencies(customerRepo)
            customerRepo.addCustomer(
                Customer(id = "user-1", private = Private(email = "klient@example.com")),
            )
        }
        val response = client.get("/admin/reservations") {
            header(HttpHeaders.Authorization, "Bearer ${token("user-1")}")
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `admin can list reservations and create customer`() = testApplication {
        val customerRepo = com.example.przyczepki_landingpage.support.FakeCustomerRepo()
        val trailersRepo = com.example.przyczepki_landingpage.support.FakeTrailersRepo()
        application {
            installAdminTestDependencies(
                customerRepo = customerRepo,
                trailersRepo = trailersRepo,
            )
            customerRepo.addCustomer(adminCustomer())
            trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        }
        val http = createClient {
            install(ContentNegotiation) { json() }
        }

        val list = http.get("/admin/reservations") {
            header(HttpHeaders.Authorization, "Bearer ${token("admin-1")}")
        }
        assertEquals(HttpStatusCode.OK, list.status)

        val created = http.post("/admin/customers") {
            header(HttpHeaders.Authorization, "Bearer ${token("admin-1")}")
            contentType(ContentType.Application.Json)
            setBody(
                CustomerRegisterRequest(
                    customer = Customer(
                        private = Private(firstName = "Anna", lastName = "Nowak", email = "anna@example.com"),
                    ),
                    password = "000000",
                ),
            )
        }
        assertEquals(HttpStatusCode.Created, created.status)
        val saved = json.decodeFromString<Customer>(created.bodyAsText())
        assertEquals("anna@example.com", saved.private?.email)
        assertTrue(!saved.confirmed.isNullOrBlank())
    }

    @Test
    fun `admin can create reservation and cannot delete customer with reservations`() = testApplication {
        val customerRepo = com.example.przyczepki_landingpage.support.FakeCustomerRepo()
        val trailersRepo = com.example.przyczepki_landingpage.support.FakeTrailersRepo()
        application {
            installAdminTestDependencies(customerRepo = customerRepo, trailersRepo = trailersRepo)
            customerRepo.addCustomer(adminCustomer())
            customerRepo.addCustomer(
                Customer(id = "c-2", private = Private(firstName = "Jan", email = "jan@example.com")),
            )
            trailersRepo.addTrailer(ReservationTestFixtures.trailer())
        }
        val http = createClient {
            install(ContentNegotiation) { json() }
        }
        val auth = "Bearer ${token("admin-1")}"

        val created = http.post("/admin/reservations") {
            header(HttpHeaders.Authorization, auth)
            contentType(ContentType.Application.Json)
            setBody(
                AdminReservationRequest(
                    customerId = "c-2",
                    trailerId = ReservationTestFixtures.TRAILER_ID,
                    startDate = LocalDate(2026, 10, 1),
                    endDate = LocalDate(2026, 10, 3),
                ),
            )
        }
        assertEquals(HttpStatusCode.Created, created.status)
        val reservation = json.decodeFromString<ReservationDto>(created.bodyAsText())
        assertEquals("c-2", reservation.customerId)
        assertTrue(reservation.reservationPrice?.sum != null)

        val blocked = http.delete("/admin/customers/c-2") {
            header(HttpHeaders.Authorization, auth)
        }
        assertEquals(HttpStatusCode.Conflict, blocked.status)

        val deletedRes = http.delete("/admin/reservations/${reservation.id}") {
            header(HttpHeaders.Authorization, auth)
        }
        assertEquals(HttpStatusCode.OK, deletedRes.status)

        val deletedCust = http.delete("/admin/customers/c-2") {
            header(HttpHeaders.Authorization, auth)
        }
        assertEquals(HttpStatusCode.OK, deletedCust.status)
    }

    @Test
    fun `admin can create and list coupons`() = testApplication {
        application {
            val deps = installAdminTestDependencies()
            deps.customerRepo.addCustomer(adminCustomer())
        }
        val http = createClient {
            install(ContentNegotiation) { json(json) }
        }
        val auth = "Bearer ${token("admin-1")}"
        val created = http.post("/admin/coupons") {
            header(HttpHeaders.Authorization, auth)
            contentType(ContentType.Application.Json)
            setBody(
                Coupon(
                    code = "promo10",
                    description = "10 zł taniej",
                    discount = Discount.Fixed(10.0),
                ),
            )
        }
        assertEquals(HttpStatusCode.Created, created.status)
        val coupon = json.decodeFromString<Coupon>(created.bodyAsText())
        assertEquals("PROMO10", coupon.code)

        val list = http.get("/admin/coupons") {
            header(HttpHeaders.Authorization, auth)
        }
        assertEquals(HttpStatusCode.OK, list.status)
        assertTrue(list.bodyAsText().contains("PROMO10"))
    }
}
