package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.controller.authController
import com.example.przyczepki_landingpage.controller.customerController
import com.example.przyczepki_landingpage.controller.healthCheck
import com.example.przyczepki_landingpage.controller.reservation
import com.example.przyczepki_landingpage.controller.trailers
import com.example.przyczepki_landingpage.di.appModule
import com.example.przyczepki_landingpage.di.networkModule
import com.example.przyczepki_landingpage.modules.ApiConfig
import com.example.przyczepki_landingpage.modules.config
import com.example.przyczepki_landingpage.modules.configureMongo
import com.example.przyczepki_landingpage.modules.configureSecurity
import com.example.przyczepki_landingpage.modules.configureStatusPages

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    println("\nServer started\n")

    configureStatusPages()
    configureDependencyInjection()
    configureSecurity()
    configureMongo()
    config()
    routing()

}

// Funkcje konfiguracyjne w osobnym pliku
private fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(appModule, networkModule)
        properties(
            mapOf(
                "ktor.environment" to environment
            )
        )
    }
    println(ApiConfig)
}

private fun Application.routing() {
    routing {
        healthCheck()
        authController()
        trailers()
        reservation()
        customerController()
    }

}