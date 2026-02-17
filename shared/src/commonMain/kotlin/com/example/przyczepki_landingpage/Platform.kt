package com.example.przyczepki_landingpage

import androidx.compose.runtime.Composable
import io.ktor.client.HttpClient

interface Platform {
    val name: String
}
expect fun getPlatform(): Platform

expect fun createHttpClient(): HttpClient

@Composable
expect fun CompanyMap(latitude: Double, longitude: Double, mapsProvider: String?)
expect fun openNavigationApp(latitude: Double, longitude: Double, label: String, mapsProvider: String? = null,)