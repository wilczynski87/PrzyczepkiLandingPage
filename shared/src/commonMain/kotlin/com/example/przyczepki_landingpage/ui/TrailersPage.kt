package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.model.CurrentScreen
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun TrailersPage(
    @Suppress("UNUSED_PARAMETER") widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
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
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NavigationBackBar(
                onClick = { viewModel.navigateTo(CurrentScreen.LANDING) },
                title = "Powrót"
            )

            Text(
                "Przyczepki",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Wybierz przyczepkę, aby zobaczyć galerię i szczegóły.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            trailers.forEach { trailer ->
                TrailerListItem(trailer) {
                    viewModel.openTrailerDetail(trailer, CurrentScreen.TRAILERS)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TrailerListItem(
    trailer: Trailer,
    onClick: () -> Unit,
) {
    val imageUrl = trailer.images?.get("thumbnail")
        ?: trailer.images?.values?.firstOrNull { it.isNotBlank() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.BrokenImage, contentDescription = "Brak zdjęcia")
                }
            } else {
                KamelImage(
                    resource = { asyncPainterResource(imageUrl) },
                    contentDescription = trailer.name ?: "Przyczepka",
                    contentScale = ContentScale.Crop,
                    onLoading = {
                        Box(
                            Modifier.size(96.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    },
                    onFailure = {
                        Box(
                            Modifier.size(96.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BrokenImage, contentDescription = "Błąd zdjęcia")
                        }
                    },
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    trailer.name ?: "Przyczepka",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                trailer.size?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                trailer.purpose?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
