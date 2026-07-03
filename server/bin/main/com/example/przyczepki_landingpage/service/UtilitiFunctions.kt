package com.example.przyczepki_landingpage.service

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

fun todayDate(): Long = Clock.System.now().toEpochMilliseconds()

fun startOfTheDay(day: Long = todayDate()): LocalDate {
    return Instant
        .fromEpochMilliseconds(day)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
}