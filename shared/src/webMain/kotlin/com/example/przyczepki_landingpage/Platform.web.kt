package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.auth.SecureTokenStorage
import com.example.przyczepki_landingpage.auth.TokenManager
import io.ktor.client.HttpClient
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

actual fun createHttpClient(tokenManager: TokenManager): HttpClient {
    return webCreateHttpClient(tokenManager)
}

@OptIn(ExperimentalWasmJsInterop::class)
//@JsFun("() => (typeof process !== 'undefined' && process.env) ? process.env.APP_ENV : undefined")
@JsFun("() => import.meta.env?.APP_ENV")
private external fun getEnvFromProcess(): String?

actual fun getEnvironment(): String = getEnvFromProcess() ?: "prod"

expect fun jsSecureTokenStorage(): SecureTokenStorage
actual fun provideSecureTokenStorage(): SecureTokenStorage {
    return jsSecureTokenStorage()
}

@OptIn(ExperimentalWasmJsInterop::class)
fun hostname(): String = js("window.location.hostname")

actual fun getBaseUrl(): String =
    if (hostname() == "localhost")
        "http://localhost:8090"
    else
        "/api"