package com.example.przyczepki_landingpage.modules

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

fun Application.config() {
    // configureSerialization

    val json = Json {
//        classDiscriminator = "type"
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }


    install(ContentNegotiation) {
        json(json)
    }

}