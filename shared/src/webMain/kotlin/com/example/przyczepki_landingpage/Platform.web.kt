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
actual fun provideSecureTokenStorage(): SecureTokenStorage {
    TODO("Not yet implemented")
}