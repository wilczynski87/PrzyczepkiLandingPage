package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.logo
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun FrontInfoText() {
//    Text("Wynajem przyczepek lekkich - 750 kg")
//    Text("Na prawo jazdy kat \"B\", (bez E)")
//    Text("Jak przebiega rezerwacja? link")
    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val resource = logo["logo"]?.let { asyncPainterResource(it) }
        KamelImage(
            { resource!! },
            contentDescription = "logo",
            onLoading = { CircularProgressIndicator() },
            onFailure = {
                Text(
                    "Wynajem przyczepek lekkich",
                    style = MaterialTheme.typography.headlineLarge
                ) },
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
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