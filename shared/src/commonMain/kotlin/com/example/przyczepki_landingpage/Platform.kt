package com.example.przyczepki_landingpage

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform