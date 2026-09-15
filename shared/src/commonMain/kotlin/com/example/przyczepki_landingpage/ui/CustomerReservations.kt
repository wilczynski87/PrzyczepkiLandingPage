package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.model.CurrentScreen
import com.example.przyczepki_landingpage.model.asPrice
import com.example.przyczepki_landingpage.model.formatDatePl
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun CustomerReservationsSection(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.appState.collectAsState()
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    Column(
        modifier = modifier
            .widthIn(max = 600.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Twoje rezerwacje",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
        )

        when {
            state.customerReservationsLoading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            state.customerReservationsError != null -> {
                Text(
                    text = state.customerReservationsError ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                OutlinedButton(onClick = { viewModel.fetchCustomerReservations() }) {
                    Text("Spróbuj ponownie")
                }
            }
            state.customerReservations.isEmpty() -> {
                Text(
                    text = "Nie masz jeszcze rezerwacji.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = { viewModel.navigateTo(CurrentScreen.RESERVATION) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Zarezerwuj przyczepkę")
                }
            }
            else -> {
                state.customerReservations.forEach { reservation ->
                    CustomerReservationCard(reservation = reservation, today = today)
                }
            }
        }
    }
}

@Composable
private fun CustomerReservationCard(
    reservation: ReservationDto,
    today: LocalDate,
) {
    val phase = reservationPhase(reservation.startDate, reservation.endDate, today)
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = reservation.trailerName?.takeIf { it.isNotBlank() }
                        ?: "Przyczepka",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                )
                ReservationStatusBadge(phase)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = listOfNotNull(
                        reservation.startDate?.formatDatePl(),
                        reservation.endDate?.formatDatePl(),
                    ).joinToString(" – ").ifBlank { "Brak terminu" },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            reservation.reservationPrice?.sum?.let { sum ->
                Text(
                    text = "Koszt wynajmu: ${sum.asPrice()} zł",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }

            reservation.id?.let { id ->
                Text(
                    text = "Nr rezerwacji: $id",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private enum class ReservationPhase {
    UPCOMING,
    ACTIVE,
    PAST,
    UNKNOWN,
}

private fun reservationPhase(
    startDate: LocalDate?,
    endDate: LocalDate?,
    today: LocalDate,
): ReservationPhase = when {
    startDate == null || endDate == null -> ReservationPhase.UNKNOWN
    endDate < today -> ReservationPhase.PAST
    startDate > today -> ReservationPhase.UPCOMING
    else -> ReservationPhase.ACTIVE
}

@Composable
private fun ReservationStatusBadge(phase: ReservationPhase) {
    val (label, container, content) = when (phase) {
        ReservationPhase.UPCOMING -> Triple(
            "Nadchodząca",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
        )
        ReservationPhase.ACTIVE -> Triple(
            "Trwa",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
        )
        ReservationPhase.PAST -> Triple(
            "Zakończona",
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ReservationPhase.UNKNOWN -> Triple(
            "Rezerwacja",
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurface,
        )
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = container,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = content,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
