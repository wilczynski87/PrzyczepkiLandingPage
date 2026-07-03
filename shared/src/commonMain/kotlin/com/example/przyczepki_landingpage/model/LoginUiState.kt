package com.example.przyczepki_landingpage.model

import com.example.przyczepki_landingpage.controller.InvalidLoginCredentialsException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.util.network.UnresolvedAddressException

data class LoginUiState(
    val login: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@(.+)\\.(.+)$")
private val PHONE_REGEX = Regex("^\\+?[0-9]{7,15}$")

fun isValidLogin(input: String): Boolean {
    val normalized = input.trim()
    return EMAIL_REGEX.matches(normalized) ||
        PHONE_REGEX.matches(normalized.replace(" ", ""))
}

/**
 * Waliduje dane logowania. Zwraca komunikat błędu do wyświetlenia
 * użytkownikowi lub null, jeśli dane są poprawne.
 */
fun validateLoginInput(login: String, password: String): String? {
    val normalizedLogin = login.trim()
    return when {
        normalizedLogin.isBlank() -> "Podaj email lub numer telefonu"
        !isValidLogin(normalizedLogin) -> "Nieprawidłowy format email lub numeru telefonu"
        password.isBlank() -> "Podaj hasło"
        password.length < 6 -> "Hasło musi mieć co najmniej 6 znaków"
        else -> null
    }
}

private val SERVER_CONNECTION_ERROR_MARKERS = listOf(
    "Failed to fetch",
    "NetworkError",
    "Load failed",
    "connection refused",
    "ECONNREFUSED",
    "ERR_CONNECTION_REFUSED",
    "Unable to resolve host",
    "Socket timeout",
)

fun isServerConnectionError(error: Throwable): Boolean {
    var current: Throwable? = error
    while (current != null) {
        when (current) {
            is ConnectTimeoutException,
            is UnresolvedAddressException,
            is HttpRequestTimeoutException -> return true
        }

        val details = buildString {
            append(current.message.orEmpty())
            append(' ')
            append(current.toString())
        }
        if (SERVER_CONNECTION_ERROR_MARKERS.any { marker ->
                details.contains(marker, ignoreCase = true)
            }
        ) {
            return true
        }

        current = current.cause
    }
    return false
}

fun mapLoginError(error: Throwable): String {
    return when {
        error is InvalidLoginCredentialsException ->
            "Nieprawidłowy email/telefon lub hasło. Sprawdź dane i spróbuj ponownie."
        isServerConnectionError(error) ->
            "Problem z serwerem. Serwer jest obecnie niedostępny. Spróbuj ponownie później."
        else ->
            "Nieprawidłowy email/telefon lub hasło. Sprawdź dane i spróbuj ponownie."
    }
}
