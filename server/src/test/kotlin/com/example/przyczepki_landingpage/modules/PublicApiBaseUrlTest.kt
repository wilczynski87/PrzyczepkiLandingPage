package com.example.przyczepki_landingpage.modules

import kotlin.test.Test
import kotlin.test.assertEquals

class PublicApiBaseUrlTest {

    @Test
    fun `prod env defaults to public przyczepkifat api url`() {
        assertEquals(
            "https://przyczepkifat.pl/api/",
            resolvePublicApiBaseUrl("prod", 8090, publicApiBaseUrlEnv = null),
        )
        assertEquals(
            "https://przyczepkifat.pl/api/",
            resolvePublicApiBaseUrl("production", 8090, publicApiBaseUrlEnv = null),
        )
    }

    @Test
    fun `dev env defaults to localhost with port`() {
        assertEquals(
            "http://localhost:8090/",
            resolvePublicApiBaseUrl("DEV", 8090, publicApiBaseUrlEnv = null),
        )
    }

    @Test
    fun `explicit env overrides defaults and normalizes trailing slash`() {
        assertEquals(
            "https://przyczepkifat.pl/api/",
            resolvePublicApiBaseUrl("DEV", 8090, publicApiBaseUrlEnv = "https://przyczepkifat.pl/api"),
        )
    }
}
