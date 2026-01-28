package com.example.przyczepki_landingpage.ui.modal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppState
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.ReservationPrice
import com.example.przyczepki_landingpage.data.ModalType
import com.example.przyczepki_landingpage.data.asPrice
import com.example.przyczepki_landingpage.data.formatDatePl
import com.example.przyczepki_landingpage.model.ModalData
import com.example.przyczepki_landingpage.model.Prices
import com.example.przyczepki_landingpage.model.Trailer
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import przyczepkilandingpage.shared.generated.resources.Res
import przyczepkilandingpage.shared.generated.resources.przyczepka1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppModals(
    viewModel: AppViewModel,
) {
    val state by viewModel.appState.collectAsState()
    val modalType = state.modalType

    when (modalType) {
        ModalType.NONE -> {}
        ModalType.CONFIRM_RESERVATION -> {
            ReservationConfirmationModal(viewModel)
        }
        ModalType.TRAILER_DETAILS -> {

        }
    }
}

@Composable
fun ReservationConfirmationModal(
    viewModel: AppViewModel
) {
    val state by viewModel.appState.collectAsState()
    val modal = state.modal
    val trailer = state.selectedTrailer
    val from = state.dateRangePickerStart
    val to = state.dateRangePickerEnd
    val prices = state.reservationPrice

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable { viewModel.closeModal() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 600.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Default.Event,
                        contentDescription = "rezerwacja",
                    )
                    Text(
                        text = modal?.dialogTitle ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Text(
                    text = modal?.dialogText ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Trailer info
                trailer?.let { currentTrailer ->
                    TrailerInfoCard(currentTrailer)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dates
                trailerReservationDates(from, to)

                Spacer(modifier = Modifier.height(12.dp))

                // Prices
                trailerReservationPrices(prices)

                Spacer(modifier = Modifier.height(12.dp))

//                Row(
//                    horizontalArrangement = Arrangement.Center,
//                    modifier = Modifier.padding(12.dp).fillMaxWidth()
//                ) {
//                    Text(
//                        text = "Koszt rezerwacji to ${trailer?.prices?.reservation?.asPrice() ?: "0,00"} zł",
//                        style = MaterialTheme.typography.bodyLarge,
//                        fontWeight = FontWeight.Medium,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AttachMoney,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Koszt rezerwacji",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${trailer?.prices?.reservation?.asPrice() ?: "0,00"} zł",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { viewModel.closeModal() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Wróć")
                    }
                    Button(
                        onClick = {
                            // TODO: Handle reservation confirmation
                            viewModel.closeModal()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Potwierdź rezerwację")
                    }
                }
            }
        }
    }
}


@Composable
private fun TrailerInfoCard(trailer: Trailer) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            trailer.image?.let { imageRes ->
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = "Thumbnail przyczepy",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(end = 16.dp)
                )
            }
            Column {
                Text(
                    text = trailer.name ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                trailer.purpose?.let { type ->
                    Text(
                        text = type,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun trailerMiniImage(thumbnail: DrawableResource?, name: String? = "", modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        thumbnail?.let { thumbnail ->
            Image(
                painter = painterResource(thumbnail),
                contentDescription = "thumbnail",
                modifier = Modifier
                    .size(72.dp)
                    .padding(end = 8.dp)
            )
        }
        Text(name!!, style = MaterialTheme.typography.titleMedium)

    }
}

@Composable
fun trailerReservationDates(
    from: Long?,
    to: Long?,
    modifier: Modifier = Modifier
) {
    val daysNumber = if (from != null && to != null) {
        ((to - from) / (1000 * 60 * 60 * 24)).toInt() + 1
    } else 0

    Column(modifier = modifier) {
        // Nagłówek sekcji
        Text(
            text = "Termin rezerwacji",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Data rozpoczęcia
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Od",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = from?.let { formatDatePl(it) } ?: "-",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }

            // Strzałka separator
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Data zakończenia
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Do",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = to?.let { formatDatePl(it) } ?: "-",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Liczba dni
        if (daysNumber > 0) {
            val daysText = when (daysNumber) {
                1 -> "1 dzień"
                in 2..4 -> "$daysNumber dni"
                else -> "$daysNumber dni"
            }
            Text(
                text = daysText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.End)
            )
        }
    }
}

@Composable
fun trailerReservationPrices(
    prices: ReservationPrice?,
    modifier: Modifier = Modifier
) {
    if (prices == null) return

    val totalPrice = remember(prices) {
        if (prices.daysNumber == 1L) {
            prices.firstDay + prices.reservation
        } else {
            prices.firstDay + (prices.otherDays * (prices.daysNumber - 1)) + prices.reservation
        }
    }

    Column(modifier = modifier) {
        // Nagłówek sekcji
        Text(
            text = "Szczegóły płatności",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Lista pozycji cenowych
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Pół dnia (jeśli dotyczy)
            if (prices.halfDay > 0) {
                PriceRow(
                    label = "Wynajem pół dnia",
                    amount = prices.halfDay,
                    showInfo = true,
                    infoText = "Do 5 godzin wynajmu"
                )
            }

            // Pierwszy dzień
            PriceRow(
                label = if (prices.daysNumber == 1L) "Wynajem 1 dnia"
                else "Pierwszy dzień",
                amount = prices.firstDay,
            )

            // Kolejne dni (jeśli więcej niż 1 dzień)
            if (prices.daysNumber > 1) {
                val subsequentDays = prices.daysNumber - 1
                PriceRow(
                    label = "Kolejne $subsequentDays dni",
                    amount = prices.otherDays * subsequentDays,
                    showInfo = true,
                    infoText = "${prices.otherDays.asPrice()} za dzień"
                )
            }

            // Kaucja rezerwacyjna
            PriceRow(
                label = "Kaucja rezerwacyjna",
                amount = prices.reservation,
                showInfo = true,
                infoText = "Bezzwrotna przy rezygnacji"
            )

            // Linia separatora przed sumą
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Suma końcowa
            PriceRow(
                label = "Łącznie do zapłaty",
                amount = totalPrice,
                isTotal = true
            )

            // Informacja o sposobie płatności
            Text(
                text = "Kaucja rezerwacyjna płatna od razu, pozostała kwota przy odbiorze",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun PriceRow(
    label: String,
    amount: Double,
    isTotal: Boolean = false,
    showInfo: Boolean = false,
    infoText: String = ""
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = if (isTotal) MaterialTheme.typography.titleMedium
                    else MaterialTheme.typography.bodyMedium,
                    color = if (isTotal) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
                if (showInfo && infoText.isNotEmpty()) {
                    Text(
                        text = infoText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Text(
                text = amount.asPrice(),
                style = if (isTotal) MaterialTheme.typography.titleMedium
                else MaterialTheme.typography.bodyMedium,
                fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
                color = if (isTotal) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface
            )
        }

        if (!isTotal && !showInfo) {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
