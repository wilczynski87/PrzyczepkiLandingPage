package com.example.przyczepki_landingpage

import androidx.compose.runtime.Composable
import com.example.przyczepki_landingpage.auth.SecureTokenStorage
import com.example.przyczepki_landingpage.auth.TokenManager
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
actual fun getEnvironment(): String = System.getenv("APP_ENV") ?: "prod"
actual fun openEmail(email: String) {
}

actual fun callPhone(phone: String) {
}

actual fun getBaseUrl(): String {
    return if(getEnvironment() == "prod") "/api"
        else "http://localhost:8090"
}

actual fun createHttpClient(tokenManager: TokenManager): HttpClient {
    TODO("Not yet implemented")
}

actual fun provideSecureTokenStorage(): SecureTokenStorage {
    TODO("Not yet implemented")
}