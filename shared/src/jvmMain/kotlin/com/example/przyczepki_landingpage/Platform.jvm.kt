package com.example.przyczepki_landingpage

import androidx.compose.runtime.Composable
import io.ktor.client.HttpClient

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

@Composable
actual fun CompanyMap(latitude: Double, longitude: Double, mapsProvider: String?) {
}

actual fun openNavigationApp(
    latitude: Double,
    longitude: Double,
    label: String,
    mapsProvider: String?
) {
}

actual fun createHttpClient(): HttpClient {
    TODO("Not yet implemented")
}

actual fun getEnvironment(): String = System.getenv("APP_ENV") ?: "prod"