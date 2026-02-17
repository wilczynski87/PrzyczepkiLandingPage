package com.example.przyczepki_landingpage

import io.ktor.client.HttpClient
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

actual fun createHttpClient(): HttpClient {
    return webCreateHttpClient()
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("() => (typeof process !== 'undefined' && process.env) ? process.env.APP_ENV : undefined")
private external fun getEnvFromProcess(): String?

actual fun getEnvironment(): String = getEnvFromProcess() ?: "dev"