package com.example.przyczepki_landingpage

import androidx.compose.runtime.Composable
import com.example.przyczepki_landingpage.service.CompanyMapImpl
import com.example.przyczepki_landingpage.service.openNavigationAppImpl

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

@Composable
actual fun CompanyMap(latitude: Double, longitude: Double, mapsProvider: String?) {
    CompanyMapImpl(latitude, longitude, mapsProvider)
}

actual fun openNavigationApp(
    latitude: Double,
    longitude: Double,
    label: String,
    mapsProvider: String?,
) {
    openNavigationAppImpl(latitude, longitude, label, mapsProvider = mapsProvider)
}