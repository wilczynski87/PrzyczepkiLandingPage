package com.example.przyczepki_landingpage.modules

import com.example.przyczepki_landingpage.service.SuplaTokenProvider
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.ktor.ext.getKoin
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Co 10 minut sprawdza access token SUPLA i odświeża go przed wygaśnięciem.
 * Nowy refresh_token jest zapisywany w MongoDB.
 */
fun Application.configureSuplaTokenRefresh() {
    val tokenProvider = getKoin().get<SuplaTokenProvider>()

    monitor.subscribe(ApplicationStarted) {
        launch {
            delay(5.seconds)
            while (isActive) {
                runCatching {
                    tokenProvider.ensureFreshToken()
                }.onFailure { e ->
                    println("SUPLA token refresh job error: ${e.message}")
                }
                delay(10.minutes)
            }
        }
    }
}
