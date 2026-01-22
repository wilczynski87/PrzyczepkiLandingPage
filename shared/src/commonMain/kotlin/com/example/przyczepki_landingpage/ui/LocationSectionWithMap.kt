package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration


@Composable
fun ContactPageKMP() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ... inne sekcje ...

        LocationCard(
            address = "ul. Ostrowskiego 102, Wrocław",
            latitude = 51.1100,
            longitude = 17.0450
        )

        // ... reszta strony ...
    }
}

@Composable
fun LocationCard(
    address: String,
    latitude: Double,
    longitude: Double
) {
    val uriHandler = null
//        try {
//            LocalUriHandler.current
//        } catch (e: Exception) {
//            null
//        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "Lokalizacja",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = address,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Przyciski do różnych usług map
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MapServiceButton(
                    service = "Google Maps",
                    onClick = {
                        openMapUrl(
                            uriHandler = uriHandler,
                            url = "https://maps.google.com/?q=$address"
                        )
                    }
                )

                MapServiceButton(
                    service = "OpenStreetMap",
                    onClick = {
                        openMapUrl(
                            uriHandler = uriHandler,
                            url = "https://www.openstreetmap.org/?mlat=$latitude&mlon=$longitude#map=17/$latitude/$longitude"
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun MapServiceButton(
    service: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
//            .weight(1f)
    ) {
        Text(service)
    }
}

fun openMapUrl(uriHandler: UriHandler?, url: String) {
    try {
        uriHandler?.openUri(url)
    } catch (e: Exception) {
        // Fallback - logowanie lub inna akcja
        println("Map URL: $url")
        // Na Desktop możesz użyć Desktop.getDesktop().browse()
    }
}