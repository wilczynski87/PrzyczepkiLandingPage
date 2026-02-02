package com.example.przyczepki_landingpage

import androidx.compose.runtime.Composable

interface Platform {
    val name: String
}
expect fun getPlatform(): Platform

@Composable
expect fun CompanyMap(latitude: Double, longitude: Double, mapsProvider: String?)
expect fun openNavigationApp(latitude: Double, longitude: Double, label: String, mapsProvider: String? = null,)