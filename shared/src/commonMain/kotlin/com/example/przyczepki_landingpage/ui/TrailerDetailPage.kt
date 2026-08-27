package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.getEnvironment
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TrailerDetailPage(
    @Suppress("UNUSED_PARAMETER") widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
) {
    val state by viewModel.appState.collectAsState()
    val trailer = state.selectedTrailer
    val returnScreen = state.trailerDetailReturnScreen
    val hasMultipleTrailers = state.trailers.size > 1

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NavigationBackBar(
                onClick = { viewModel.navigateTo(returnScreen) },
                title = "Powrót"
            )

            if (trailer == null) {
                Text(
                    "Nie wybrano przyczepki.",
                    style = MaterialTheme.typography.bodyLarge
                )
                return@Column
            }

            key(trailer.id ?: trailer.name) {
                Text(
                    trailer.name ?: "Przyczepka",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                TrailerImageGallery(
                    trailer = trailer,
                    showNextTrailerButton = hasMultipleTrailers,
                    onNextTrailer = { viewModel.openNextTrailerDetail() },
                )

                TrailerParamsSection(trailer)

                trailer.zastosowanie?.takeIf { it.isNotBlank() }?.let { text ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "Zastosowanie",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                TableRowPrice(prices = trailer.prices)

                if (getEnvironment() == "prod") {
                    Reservation { viewModel.reservationButtonClick(trailer) }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TrailerImageGallery(
    trailer: Trailer,
    showNextTrailerButton: Boolean = false,
    onNextTrailer: () -> Unit = {},
) {
    val gallery = trailer.images
        ?.entries
        ?.filter { (key, url) ->
            url.isNotBlank() && key != "thumbnail"
        }
        ?.distinctBy { it.value }
        .orEmpty()
        .ifEmpty {
            trailer.images
                ?.entries
                ?.filter { it.value.isNotBlank() }
                ?.distinctBy { it.value }
                .orEmpty()
        }

    if (gallery.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.BrokenImage, contentDescription = "Brak zdjęć")
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { gallery.size })
    val thumbListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        thumbListState.animateScrollToItem(
            index = pagerState.currentPage.coerceAtMost(gallery.lastIndex)
        )
    }

    fun goTo(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page.coerceIn(0, gallery.lastIndex))
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val entry = gallery[page]
                    KamelImage(
                        resource = { asyncPainterResource(entry.value) },
                        contentDescription = imageLabelPl(entry.key),
                        contentScale = ContentScale.Fit,
                        onLoading = {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            }
                        },
                        onFailure = {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.BrokenImage,
                                    contentDescription = "Błąd zdjęcia"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )
                }

                if (gallery.size > 1) {
                    IconButton(
                        onClick = { goTo(pagerState.currentPage - 1) },
                        enabled = pagerState.currentPage > 0,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(4.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Poprzednie zdjęcie"
                        )
                    }
                    IconButton(
                        onClick = { goTo(pagerState.currentPage + 1) },
                        enabled = pagerState.currentPage < gallery.lastIndex,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(4.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Następne zdjęcie"
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            "${pagerState.currentPage + 1} / ${gallery.size}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Text(
            imageLabelPl(gallery[pagerState.currentPage].key),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (gallery.size > 1) {
            Slider(
                value = pagerState.currentPage.toFloat(),
                onValueChange = { value -> goTo(value.roundToInt()) },
                valueRange = 0f..(gallery.size - 1).toFloat(),
                steps = (gallery.size - 2).coerceAtLeast(0),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            LazyRow(
                state = thumbListState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(gallery) { index, entry ->
                    val selected = index == pagerState.currentPage
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .size(72.dp)
                            .clickable { goTo(index) }
                    ) {
                        KamelImage(
                            resource = { asyncPainterResource(entry.value) },
                            contentDescription = imageLabelPl(entry.key),
                            contentScale = ContentScale.Crop,
                            onLoading = {
                                Box(
                                    Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                }
                            },
                            onFailure = {
                                Box(
                                    Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.BrokenImage,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        if (showNextTrailerButton) {
            OutlinedButton(
                onClick = onNextTrailer,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Następna przyczepka")
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun TrailerParamsSection(trailer: Trailer) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "Parametry",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        DetailParamRow("Wymiary", trailer.size ?: "Brak informacji")
        DetailParamRow("Przeznaczenie", trailer.purpose ?: "Brak informacji")
        DetailParamRow("Liczba osi", trailer.axles?.toString() ?: "Brak informacji")
        DetailParamRow(
            "Obciążenie",
            trailer.loadingMass?.let { "$it kg" } ?: "Brak informacji"
        )
        DetailParamRow(
            "Dopuszczalna masa całkowita",
            trailer.gvw?.let { "$it kg" } ?: "Brak informacji"
        )
        DetailParamRow(
            "Kategoria prawa jazdy",
            trailer.licenseCategory?.name ?: "Brak informacji"
        )
        DetailParamRow(
            "Hamulce",
            when (trailer.hasBreaks) {
                true -> "Tak"
                false -> "Nie"
                null -> "Brak informacji"
            }
        )
    }
}

@Composable
private fun DetailParamRow(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

internal fun imageLabelPl(key: String): String = when (key) {
    "thumbnail" -> "Miniatura"
    "plandeka" -> "Plandeka"
    "przod" -> "Przód"
    "tyl" -> "Tył"
    "bok" -> "Bok"
    "bokPrzod" -> "Bok — przód"
    "bokZamkniety" -> "Bok zamknięty"
    "bokOtwarty" -> "Bok otwarty"
    "carroFlat" -> "Platforma"
    "carroBack" -> "Tył"
    "carroFront" -> "Przód"
    "flat" -> "Platforma"
    else -> key.replaceFirstChar { it.uppercase() }
}
