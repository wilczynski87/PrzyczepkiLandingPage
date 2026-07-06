package com.example.przyczepki_landingpage.data

enum class EmailTemplate(val templateName: String) {
    ACCOUNT_CONFIRMATION("account-confirmation"),
    RESERVATION_CONFIRMATION("reservation-confirmation"),
    RESERVATION_REMINDER("reservation-reminder"),
    ;

    companion object {
        fun fromName(name: String): EmailTemplate =
            entries.find { it.templateName == name }
                ?: throw IllegalArgumentException("Nieprawidlowa nazwa szablonu: $name")
    }
}
