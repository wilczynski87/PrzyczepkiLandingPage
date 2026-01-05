package com.example.przyczepki_landingpage.ui.reservation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import org.jetbrains.compose.resources.painterResource
import com.example.przyczepki_landingpage.model.Trailer

@Composable
fun TrailerSelectionList(
    viewModel: AppViewModel
) {
    val state by viewModel.appState.collectAsState()
    val trailers: List<Trailer> = state.trailers
    val selectedTrailer: Trailer? = state.selectedTrailer
    val onSelect: (Trailer) -> Unit = { viewModel.onTrailerSelected(it) }


    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = 300.dp,   // ⬅️ MIN: 3 karty
                max = 420.dp    // ⬅️ MAX: nie zdominuje ekranu
            )
        , contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(trailers) {
            TrailerCard(
                trailer = it,
                selected = it == selectedTrailer,
                onClick = { onSelect(it) }
            )
        }
    }
}

@Composable
fun TrailerCard(
    trailer: Trailer,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = CardDefaults.cardColors(
        containerColor = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface
    )
    val elevation by animateDpAsState(
        targetValue = if (selected) 10.dp else 2.dp,
        label = "cardElevation"
    )
    val borderStroke = if (selected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = colors,
        border = borderStroke,
        elevation = CardDefaults.cardElevation(
//            defaultElevation = if (selected) 4.dp else 1.dp
            elevation
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (trailer.image != null) {
                Image(
                    painter = painterResource(trailer.image),
                    contentDescription = trailer.name,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    trailer.name ?: "błąd nazwy",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "${trailer.size} • ${trailer.purpose}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Osie: ${trailer.axles}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (selected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Wybrana",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}