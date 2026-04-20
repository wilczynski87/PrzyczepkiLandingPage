package com.example.przyczepki_landingpage

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import com.example.przyczepki_landingpage.service.CompanyMapImpl
import com.example.przyczepki_landingpage.service.openNavigationAppImpl
import kotlinx.browser.window

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

actual fun openEmail(email: String) {
    window.open("mailto:$email", "_self")
}

actual fun callPhone(phone: String) {
    window.open("tel:$phone", "_self")
}

//@OptIn(ExperimentalWasmJsInterop::class)
//fun hostname(): String = js("window.location.hostname")
//
//actual fun getBaseUrl(): String =
//    if (hostname() == "localhost")
//        "http://localhost:8090"
//    else
//        "/api"