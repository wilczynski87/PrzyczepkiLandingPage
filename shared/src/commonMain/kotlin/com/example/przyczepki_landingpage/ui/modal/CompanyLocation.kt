package com.example.przyczepki_landingpage.ui.modal

import com.example.przyczepki_landingpage.model.latitude
import com.example.przyczepki_landingpage.model.longitude

data class CompanyLocation(
    val name: String = "Przyczepki FAT",
    val latitudeComp: Double = latitude,
    val longitudeComp: Double = longitude,
    val address: String = "ul. Aleksandra Ostrowskiego 102, 53-238 Wrocław",
    val phone: String = "+48 727 188 330",
    val email: String? = "parkingostrowskiego@gmail.com",
) {
}