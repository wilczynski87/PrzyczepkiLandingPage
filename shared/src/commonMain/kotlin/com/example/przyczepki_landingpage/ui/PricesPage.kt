package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.model.asPrice

@Composable
fun PricesPage(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel
) {
    val state by viewModel.appState.collectAsState()
    val trailers = state.trailers
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 900.dp)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Nagłówek strony
            Text(
                text = "Cennik przyczepek",
                style = MaterialTheme.typography.headlineMedium
            )
            for (trailer in trailers) {
                PriceSection(trailer)
            }
//            // Jednoosiowe
//            PriceSection(
//                title = "Przyczepki jednoosiowe",
//                description = "Lekkie – do 750kg (kat. B)",
//                prices = listOf(
//                    "Rezerwacja" to "40 zł",
//                    "Pierwsza doba" to "60 zł",
//                    "Druga doba" to "50 zł",
//                    "Każda kolejna doba" to "40 zł",
//                    "Do 12h" to "40 zł"
//                )
//            )
//
//            // Dwuosiowe
//            PriceSection(
//                title = "Przyczepki dwuosiowe",
//                description = "Lekkie – do 750kg (kat. B)",
//                prices = listOf(
//                    "Rezerwacja" to "40 zł",
//                    "Pierwsza doba" to "70 zł",
//                    "Druga doba" to "60 zł",
//                    "Każda kolejna doba" to "50 zł",
//                    "Do 12h" to "50 zł"
//                )
//            )
        }
    }
}


@Composable
fun PriceSection(trailer: Trailer) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = trailer.name ?: "brak nazwy",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = trailer.purpose ?: "brak przeznaczenia",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pierwsza doba:
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pierwsza doba:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = trailer.prices?.firstDay?.asPrice() ?: "brak informacji",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            // Druga doba:
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Druga doba:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = trailer.prices?.secondDay?.asPrice() ?: "brak informacji",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            // Kolejna doba:
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Kolejna doba:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = trailer.prices?.otherDays?.asPrice() ?: "brak informacji",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            // Do 6h:
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Do 6h:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = trailer.prices?.halfDay?.asPrice() ?: "brak informacji",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            // Rezerwacja:
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Rezerwacja:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = trailer.prices?.reservation?.asPrice() ?: "brak informacji",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}