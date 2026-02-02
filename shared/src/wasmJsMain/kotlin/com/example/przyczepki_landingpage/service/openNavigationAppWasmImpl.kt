package com.example.przyczepki_landingpage.service

import kotlinx.browser.window

//fun openNavigationAppImpl(latitude: Double, longitude: Double, label: String) {
//    val navUrl = "https://www.openstreetmap.org/directions" +
//            "?engine=fossgis_osrm_car&route=%3B$latitude%2C$longitude"
//    window.open(navUrl, "_blank")
//}

fun openNavigationAppImpl(latitude: Double, longitude: Double, label: String, mapsProvider: String? = null) {
    // Użyj Google Maps lub OpenStreetMap
    val mapsProviderResolve = when (mapsProvider) {
        "google" -> "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude&travelmode=driving"
        "apple" -> "http://maps.apple.com/?daddr=$latitude,$longitude&dirflg=d"
        else -> buildOpenStreetMapViewUrl(latitude, longitude, 17)
    }

    window.open(mapsProviderResolve, "_blank", "noopener,noreferrer")
    println("📍 Opening navigation to: $latitude, $longitude ($label)")
}

private fun buildOpenStreetMapViewUrl(
    latitude: Double = 51.1085,
    longitude: Double = 17.0415,
    zoom: Int = 17,
    companyName: String = "Przyczepki FAT"
): String {
    return "https://www.openstreetmap.org" +
            "?mlat=$latitude" +  // Szerokość geograficzna
            "&mlon=$longitude" +  // Długość geograficzna
            "#map=$zoom/$latitude/$longitude" +  // Przybliżenie i pozycja
            "&layers=N"  // Warstwy
}




// Wynik: https://staticmap.openstreetmap.de/staticmap.php?center=51.1085,17.0415&zoom=16&size=800x400&markers=51.1085,17.0415,red-pushpin&scale=2

// Wynik: https://www.openstreetmap.org/export/embed.html?bbox=17.0395,51.1065,17.0435,51.1105&layer=mapnik&marker=51.1085,17.0415&zoom=17