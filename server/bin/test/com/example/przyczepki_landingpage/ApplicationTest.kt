package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.controller.healthCheck
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testHealthCheck() = testApplication {
        application {
            routing {
                healthCheck()
            }
        }
        val response = client.get("/healthCheck")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Server is running", response.bodyAsText())
    }
}
