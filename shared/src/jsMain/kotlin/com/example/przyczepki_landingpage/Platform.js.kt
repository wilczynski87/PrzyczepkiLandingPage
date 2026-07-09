package com.example.przyczepki_landingpage

import androidx.compose.runtime.Composable
import com.example.przyczepki_landingpage.auth.SecureTokenStorage
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

actual fun openExternalUrl(url: String) {
    window.open(url, "_self")
}

actual fun getCurrentPath(): String = window.location.pathname

actual fun replaceBrowserPath(path: String) {
    window.history.replaceState(null, "", path)
}

actual fun getLocalStorageValue(key: String): String? = kotlinx.browser.localStorage.getItem(key)

actual fun setLocalStorageValue(key: String, value: String) {
    kotlinx.browser.localStorage.setItem(key, value)
}

actual fun removeLocalStorageValue(key: String) {
    kotlinx.browser.localStorage.removeItem(key)
}