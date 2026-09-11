package com.example.przyczepki_landingpage.auth

import com.example.przyczepki_landingpage.getLocalStorageValue
import com.example.przyczepki_landingpage.removeLocalStorageValue
import com.example.przyczepki_landingpage.setLocalStorageValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

const val REMEMBERED_LOGIN_STORAGE_KEY = "remembered_login_credentials"
const val CUSTOMER_ID_STORAGE_KEY = "customer_id"

@Serializable
data class SavedLoginCredentials(
    val login: String,
    val password: String,
)

@Serializable
private data class JwtPayload(
    val userId: String? = null,
)

private val persistenceJson = Json { ignoreUnknownKeys = true }

fun loadRememberedCredentials(): SavedLoginCredentials? {
    val raw = getLocalStorageValue(REMEMBERED_LOGIN_STORAGE_KEY) ?: return null
    return runCatching { persistenceJson.decodeFromString<SavedLoginCredentials>(raw) }.getOrNull()
}

fun saveRememberedCredentials(login: String, password: String) {
    setLocalStorageValue(
        REMEMBERED_LOGIN_STORAGE_KEY,
        persistenceJson.encodeToString(SavedLoginCredentials(login.trim(), password)),
    )
}

fun clearRememberedCredentials() {
    removeLocalStorageValue(REMEMBERED_LOGIN_STORAGE_KEY)
}

fun loadStoredCustomerId(): String? = getLocalStorageValue(CUSTOMER_ID_STORAGE_KEY)

fun saveStoredCustomerId(customerId: String) {
    setLocalStorageValue(CUSTOMER_ID_STORAGE_KEY, customerId)
}

fun clearStoredCustomerId() {
    removeLocalStorageValue(CUSTOMER_ID_STORAGE_KEY)
}

fun customerIdFromJwt(token: String?): String? {
    if (token.isNullOrBlank()) return null
    val payload = token.split('.').getOrNull(1) ?: return null
    return runCatching {
        persistenceJson.decodeFromString<JwtPayload>(decodeJwtPayload(payload)).userId
    }.getOrNull()?.takeIf { it.isNotBlank() }
}

@OptIn(ExperimentalEncodingApi::class)
private fun decodeJwtPayload(payload: String): String {
    val bytes = Base64.UrlSafe
        .withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)
        .decode(payload)
    return bytes.decodeToString()
}
