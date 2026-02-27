package com.example.przyczepki_landingpage.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

const val latitude = 51.093581049896694
const val longitude = 16.966644208784885

fun formatDatePl(millis: Long?): String? {
    if (millis == null) return null

    val date = Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.UTC)
        .date

    val day = date.day.toString().padStart(2, '0')
    val month = date.month.number.toString().padStart(2, '0')
    val year = date.year

    return "$day.$month.$year"
}

fun LocalDate.formatDatePl(): String {

    val day = day.toString().padStart(2, '0')
    val month = month.number.toString().padStart(2, '0')
    val year = year

    return "$day/$month/$year"
}

fun String.toLocalDate(): LocalDate? {
    return if(this.isBlank().not()) null
        else LocalDate.parse(this)
}

fun todayUtcMillis(): Long {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    return today
        .atStartOfDayIn(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
}

fun Double.asPrice(): String {
    val whole = this.toInt()
    val fraction = ((this * 100).toInt() % 100)
    return "$whole,${fraction.toString().padStart(2, '0')}"
}
