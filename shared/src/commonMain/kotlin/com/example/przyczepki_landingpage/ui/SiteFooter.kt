package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.seller

@Composable
fun SiteFooter(modifier: Modifier = Modifier) {
    val companyName = seller.company?.name.orEmpty()
    val nip = seller.company?.nip.orEmpty()

    HorizontalDivider(modifier = Modifier.fillMaxWidth())
    Text(
        text = "$companyName | NIP: $nip",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
    )
}
