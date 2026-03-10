package com.example.przyczepki_landingpage

import androidx.compose.runtime.Composable
import kotlinx.browser.window

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()

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

actual fun openEmail(email: String) {
    window.open("mailto:$email", "_self")
}

actual fun callPhone(phone: String) {
    window.open("tel:$phone", "_self")
}