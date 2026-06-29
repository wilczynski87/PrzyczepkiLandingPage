package com.example.przyczepki_landingpage.model

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
