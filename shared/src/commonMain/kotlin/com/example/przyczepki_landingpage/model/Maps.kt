package com.example.przyczepki_landingpage.model

data class MapMarker(
    val id: String,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val navigationUrl: String
)