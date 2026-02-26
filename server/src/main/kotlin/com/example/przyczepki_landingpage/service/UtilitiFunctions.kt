package com.example.przyczepki_landingpage.service

import kotlin.time.Clock

fun todayDate(): Long = Clock.System.now().toEpochMilliseconds()

fun startOfTheDay(day: Long = todayDate()): Long {
    val dayInMillis = 24 * 60 * 60 * 1000
    val offset = day % dayInMillis

    return day - offset
}