package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.Private
import com.example.przyczepki_landingpage.service.auth.JwtService
import com.example.przyczepki_landingpage.support.FakeCustomerRepo
import com.example.przyczepki_landingpage.support.installCustomerTestDependencies
import com.example.przyczepki_landingpage.support.testAuthConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CustomerUpdateDeleteEndpointTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    private val jwtService = JwtService(testAuthConfig)

    private fun token(userId: String): String =
        jwtService.generateToken(Customer(id = userId))

    private fun existingCustomer(
        id: String = "c1",
        email: String = "jan@example.com",
        firstName: String = "Jan",
        confirmed: String? = "2026-01-01",
    ) = Customer(
        id = id,
        confirmed = confirmed,
        private = Private(firstName = firstName, lastName = "Kowalski", email = email),
    )

    @Test
    fun `PUT customer without token returns 401`() = testApplication {
        application { installCustomerTestDependencies() }
        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.put("/customer") {
            contentType(ContentType.Application.Json)
            setBody(existingCustomer())
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `PUT customer updates own profile and ignores confirmed spoof`() = testApplication {
        val repo = FakeCustomerRepo()
        application { installCustomerTestDependencies(repo) }
        repo.addCustomer(existingCustomer())

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.put("/customer") {
            header(HttpHeaders.Authorization, "Bearer ${token("c1")}")
            contentType(ContentType.Application.Json)
            setBody(
                existingCustomer(
                    firstName = "Anna",
                    confirmed = "spoofed",
                ),
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<Customer>(response.bodyAsText())
        assertEquals("Anna", body.private?.firstName)
        assertEquals("2026-01-01", body.confirmed)
        assertEquals("Anna", repo.get("c1")?.private?.firstName)
        assertEquals("2026-01-01", repo.get("c1")?.confirmed)
    }

    @Test
    fun `PUT customer with another users token returns 403`() = testApplication {
        val repo = FakeCustomerRepo()
        application { installCustomerTestDependencies(repo) }
        repo.addCustomer(existingCustomer(id = "c1"))
        repo.addCustomer(existingCustomer(id = "c2", email = "other@example.com"))

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.put("/customer") {
            header(HttpHeaders.Authorization, "Bearer ${token("c2")}")
            contentType(ContentType.Application.Json)
            setBody(existingCustomer(id = "c1", firstName = "Hacker"))
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals("Jan", repo.get("c1")?.private?.firstName)
    }

    @Test
    fun `PUT customer returns 404 when account is missing`() = testApplication {
        application { installCustomerTestDependencies() }
        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.put("/customer") {
            header(HttpHeaders.Authorization, "Bearer ${token("missing")}")
            contentType(ContentType.Application.Json)
            setBody(existingCustomer(id = "missing"))
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `PUT customer returns 400 when email belongs to another customer`() = testApplication {
        val repo = FakeCustomerRepo()
        application { installCustomerTestDependencies(repo) }
        repo.addCustomer(existingCustomer(id = "c1", email = "one@example.com"))
        repo.addCustomer(existingCustomer(id = "c2", email = "two@example.com"))

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.put("/customer") {
            header(HttpHeaders.Authorization, "Bearer ${token("c1")}")
            contentType(ContentType.Application.Json)
            setBody(existingCustomer(id = "c1", email = "two@example.com"))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Klient z tym adresem e-mail już istnieje"))
    }

    @Test
    fun `DELETE customer without token returns 401`() = testApplication {
        application { installCustomerTestDependencies() }

        val response = client.delete("/customer/c1")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `DELETE customer removes own account`() = testApplication {
        val repo = FakeCustomerRepo()
        application { installCustomerTestDependencies(repo) }
        repo.addCustomer(existingCustomer())

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.delete("/customer/c1") {
            header(HttpHeaders.Authorization, "Bearer ${token("c1")}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("true", response.bodyAsText())
        assertNull(repo.get("c1"))
    }

    @Test
    fun `DELETE customer with another users token returns 403`() = testApplication {
        val repo = FakeCustomerRepo()
        application { installCustomerTestDependencies(repo) }
        repo.addCustomer(existingCustomer(id = "c1"))

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.delete("/customer/c1") {
            header(HttpHeaders.Authorization, "Bearer ${token("c2")}")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals("c1", repo.get("c1")?.id)
    }

    @Test
    fun `DELETE customer returns 404 when account is missing`() = testApplication {
        application { installCustomerTestDependencies() }
        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.delete("/customer/missing") {
            header(HttpHeaders.Authorization, "Bearer ${token("missing")}")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
