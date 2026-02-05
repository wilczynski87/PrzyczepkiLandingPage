package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.controller.trailers
import com.example.przyczepki_landingpage.di.appModule
import com.example.przyczepki_landingpage.di.networkModule
import com.example.przyczepki_landingpage.modules.config

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

    configureDependencyInjection()

    config()

    routing()

}

// Funkcje konfiguracyjne w osobnym pliku
private fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(appModule, networkModule)
    }
}

private fun Application.routing() {
    routing {
        trailers()
    }

}