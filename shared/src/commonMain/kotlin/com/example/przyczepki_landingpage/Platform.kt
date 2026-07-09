package com.example.przyczepki_landingpage

import androidx.compose.runtime.Composable
import com.example.przyczepki_landingpage.auth.SecureTokenStorage
import com.example.przyczepki_landingpage.auth.TokenManager
import io.ktor.client.HttpClient

interface Platform {
    val name: String
}
expect fun getPlatform(): Platform

expect fun provideSecureTokenStorage(): SecureTokenStorage

expect fun createHttpClient(tokenManager: TokenManager): HttpClient

expect fun getEnvironment(): String

@Composable
expect fun CompanyMap(latitude: Double, longitude: Double, mapsProvider: String?)
expect fun openNavigationApp(latitude: Double, longitude: Double, label: String, mapsProvider: String? = null,)

expect fun openEmail(email: String)
expect fun callPhone(phone: String)
expect fun openExternalUrl(url: String)

expect fun getCurrentPath(): String
expect fun replaceBrowserPath(path: String)
expect fun getLocalStorageValue(key: String): String?
expect fun setLocalStorageValue(key: String, value: String)
expect fun removeLocalStorageValue(key: String)

expect fun getBaseUrl(): String