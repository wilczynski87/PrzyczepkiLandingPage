package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FrontInfoText() {
//    Text("Wynajem przyczepek lekkich - 750 kg")
//    Text("Na prawo jazdy kat \"B\", (bez E)")
//    Text("Jak przebiega rezerwacja? link")
    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Wynajem przyczepek lekkich",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            "Do 750 kg • Prawo jazdy kat. B",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "Jak przebiega rezerwacja?",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}