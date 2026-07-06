package com.example.przyczepki_landingpage.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        classDiscriminator = "type"
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                        explicitNulls = false
                    }
                )
            }
            install(DefaultRequest) {
                contentType(ContentType.Application.Json)
            }
        }
    }
}
