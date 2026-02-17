package com.example.przyczepki_landingpage

import io.ktor.client.HttpClient

actual fun createHttpClient(): HttpClient {
    return webCreateHttpClient()
}