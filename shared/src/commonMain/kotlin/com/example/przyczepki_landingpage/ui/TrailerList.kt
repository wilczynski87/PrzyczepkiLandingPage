package com.example.przyczepki_landingpage.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.model.asPrice
import com.example.przyczepki_landingpage.data.Prices
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.getEnvironment
import org.jetbrains.compose.resources.painterResource

@Composable
fun TrailerTable(
    trailers: List<Trailer>? = emptyList(),
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val state by appViewModel.appState.collectAsState()
    val trailers = if(trailers.isNullOrEmpty()) state.trailers else trailers


    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        trailers.forEach { trailer ->
            if (widthSizeClass != WindowWidthSizeClass.Compact) {
                TrailerCardBig(trailer) { appViewModel.reservationButtonClick(trailer) }
            } else {
                TrailerCardSmall(trailer) { appViewModel.reservationButtonClick(trailer) }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
fun TrailerCardBig(trailer: Trailer, rezerwuj: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            // ===== GÓRNA CZĘŚĆ =====
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(trailer.images?.get("main") == null) {
                    Icon(Icons.Default.BrokenImage, "brak zdjęcia")
                }
                else {
//                    Image(
//                        painter = painterResource(trailer.image),
//                        contentDescription = trailer.name,
//                        modifier = Modifier
//                            .size(140.dp)
//                            .clip(RoundedCornerShape(14.dp))
//                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
//                        contentScale = ContentScale.Crop
//                    )
                }
                Spacer(Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(trailer.name ?: "")
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            TableRow("Wymiary", trailer.size ?: "")
                            TableRow("Przeznaczenie", trailer.purpose ?: "")
                            TableRow("Liczba osi", "${trailer.axles}")
                        }
                        VerticalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column {
                            TableRow(
                                "Obciążenie:",
                                "${trailer.loadingMass ?: "brak informacji"} kg"
                            )
                            TableRow(
                                "Dopuszczalna Masa Całkowita",
                                "${trailer.gvw ?: "brak informacji"} kg"
                            )
                            TableRow(
                                "kat. prawa jazdy",
                                trailer.licenseCategory?.name ?: "brak informacji"
                            )
                        }
                    }
                }
            }
            // ===== CENA =====
            TableRowPrice(
                prices = trailer.prices,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            // ===== CTA =====
            println("ENV: ${getEnvironment()}")
            if(getEnvironment() == "prod") Reservation(rezerwuj)
        }
    }
}

@Composable
fun TrailerCardSmall(
    trailer: Trailer,
    rezerwuj: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier) {
                if(trailer.images?.get("main") != null) {
//                    Image(
//                        painter = painterResource(trailer.image),
//                        contentDescription = trailer.name,
//                        modifier = Modifier
//                            .fillMaxHeight()
//                            .align(Alignment.Center)
//                            .clip(RoundedCornerShape(14.dp))
//                            .alpha(0.5f)
//                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
//                        contentScale = ContentScale.Fit
//                    )
                }

                Column(modifier = Modifier.align(Alignment.Center)) {

                    Text(
                        trailer.name ?: "Brak nazwy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    TableRow("Wymiary", trailer.size ?: "Brak wymiarów")
                    TableRow("Przeznaczenie", trailer.purpose ?: "Brak przeznaczenia")
                    TableRow("Liczba osi", "${trailer.axles ?: "Brak informacji"}")
                    TableRow(
                        "Obciążenie:",
                        "${trailer.loadingMass ?: "brak informacji"} kg"
                    )
                    TableRow(
                        "Dopuszczalna Masa Całkowita",
                        "${trailer.gvw ?: "brak informacji"} kg"
                    )
                    TableRow(
                        "kat. prawa jazdy",
                        trailer.licenseCategory?.name ?: "brak informacji"
                    )
                    TableRowPrice(trailer.prices)
                }

            }
        }
        if(getEnvironment() == "prod") Reservation(rezerwuj)
    }
}

@Composable
fun TableRow(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
@Composable
fun TableRowPrice(
    prices: Prices?,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .animateContentSize(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ===== GŁÓWNA LINIA =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Cena za dobę",
                        style = MaterialTheme.typography.labelMedium
                    )

                    if (prices != null) {
                        Text(
                            "${prices.firstDay?.asPrice()} zł",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            "Brak informacji",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (prices != null) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded)
                                Icons.Default.ExpandLess
                            else
                                Icons.Default.ExpandMore,
                            contentDescription = "Pokaż szczegóły ceny"
                        )
                    }
                }
            }

            // ===== SZCZEGÓŁY (TYLKO GDY SĄ DANE) =====
            if (prices != null) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PriceDetailRow("Każda kolejna doba", "${prices.otherDays?.asPrice()} zł")
                        PriceDetailRow("12 godzin", "${prices.halfDay?.asPrice()} zł")
                        PriceDetailRow("Rezerwacja", "${prices.reservation?.asPrice()} zł")
                    }
                }
            }
        }
    }
}


@Composable
fun PriceDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
@Composable
fun Reservation(
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text("Rezerwuj")
    }
}
