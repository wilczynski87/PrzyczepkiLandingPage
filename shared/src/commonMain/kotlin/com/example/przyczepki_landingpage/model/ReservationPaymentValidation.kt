package com.example.przyczepki_landingpage.model

enum class ReservationPaymentIssue(val message: String) {
    NOT_LOGGED_IN("Zaloguj się lub podaj dane klienta"),
    NO_TRAILER("Wybierz przyczepkę"),
    NO_PERIOD("Wybierz okres rezerwacji"),
    TERMS_NOT_ACCEPTED("Zaakceptuj regulamin i politykę prywatności"),
}

data class ReservationPaymentValidation(
    val canPay: Boolean,
    val issues: List<ReservationPaymentIssue>,
)

fun validateReservationPayment(
    isLoggedIn: Boolean,
    hasTrailer: Boolean,
    hasPeriod: Boolean,
    termsAccepted: Boolean,
): ReservationPaymentValidation {
    val issues = buildList {
        if (!isLoggedIn) add(ReservationPaymentIssue.NOT_LOGGED_IN)
        if (!hasTrailer) add(ReservationPaymentIssue.NO_TRAILER)
        if (!hasPeriod) add(ReservationPaymentIssue.NO_PERIOD)
        if (!termsAccepted) add(ReservationPaymentIssue.TERMS_NOT_ACCEPTED)
    }
    return ReservationPaymentValidation(
        canPay = issues.isEmpty(),
        issues = issues,
    )
}
