package com.example.przyczepki_landingpage.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.data.PaymentSessionStatus
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.model.CurrentScreen
import com.example.przyczepki_landingpage.model.asPrice
import com.example.przyczepki_landingpage.seller
import com.example.przyczepki_landingpage.trailers as fallbackTrailers
import com.example.przyczepki_landingpage.ui.modal.ReservationTotalPrice
import com.example.przyczepki_landingpage.ui.modal.TrailerInfoCard
import com.example.przyczepki_landingpage.ui.modal.trailerReservationDates
import com.example.przyczepki_landingpage.ui.modal.trailerReservationPrices

@Composable
fun ReservationSummaryPage(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.appState.collectAsState()
    val paymentStatus = state.paymentStatus
    val reservation = paymentStatus?.reservation ?: state.reservationToMake
    val waitingForPayment = state.paymentStatusLoading ||
        paymentStatus?.status == PaymentSessionStatus.PENDING ||
        paymentStatus?.status == PaymentSessionStatus.VERIFIED

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        when {
            state.paymentStatusError != null && !waitingForPayment -> {
                PaymentSummaryError(
                    message = state.paymentStatusError!!,
                    onRetry = { viewModel.retryPaymentStatusCheck() },
                    onGoHome = { viewModel.navigateTo(CurrentScreen.LANDING) },
                )
            }

            paymentStatus?.status == PaymentSessionStatus.COMPLETED && reservation != null -> {
                ReservationSummarySuccess(
                    reservation = reservation,
                    trailer = resolveTrailer(state.trailers, state.selectedTrailer, reservation.trailerId),
                    onGoHome = { viewModel.navigateTo(CurrentScreen.LANDING) },
                    onNewReservation = { viewModel.navigateTo(CurrentScreen.RESERVATION) },
                )
            }

            paymentStatus?.status == PaymentSessionStatus.FAILED -> {
                PaymentSummaryError(
                    message = paymentStatus.message ?: "Płatność nie powiodła się.",
                    onRetry = { viewModel.retryPaymentStatusCheck() },
                    onGoHome = { viewModel.navigateTo(CurrentScreen.LANDING) },
                )
            }

            else -> {
                PaymentConfirmationWaiting(
                    status = paymentStatus?.status,
                    statusMessage = paymentStatus?.message,
                )
            }
        }
    }
}

@Composable
private fun PaymentConfirmationWaiting(
    status: PaymentSessionStatus?,
    statusMessage: String?,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "paymentWaitPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "paymentWaitPulseAlpha",
    )

    val statusLabel = when (status) {
        PaymentSessionStatus.VERIFIED -> "Płatność zweryfikowana — zapisujemy rezerwację"
        PaymentSessionStatus.PENDING -> "Oczekiwanie na potwierdzenie z Przelewy24"
        else -> "Łączymy się z Przelewy24"
    }

    Column(
        modifier = Modifier
            .widthIn(max = 520.dp)
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(72.dp),
                strokeWidth = 5.dp,
            )
            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(28.dp)
                    .alpha(pulseAlpha),
            )
        }

        Text(
            text = "Potwierdzenie rezerwacji",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Text(
            text = statusLabel,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(pulseAlpha),
        )

        Text(
            text = "Prosimy nie zamykać tej strony. Po zaksięgowaniu płatności " +
                "wyświetlimy szczegóły rezerwacji i wyślemy potwierdzenie e-mail.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        statusMessage?.takeIf { it.isNotBlank() }?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Co się dzieje?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "1. Przelewy24 potwierdza wpływ kaucji rezerwacyjnej",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "2. System zapisuje rezerwację w kalendarzu",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "3. Dostajesz podsumowanie i e-mail z potwierdzeniem",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        LoadingDotsText(
            baseText = "Sprawdzanie statusu płatności",
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun ReservationSummarySuccess(
    reservation: ReservationDto,
    trailer: Trailer?,
    onGoHome: () -> Unit,
    onNewReservation: () -> Unit,
) {
    Column(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 8.dp)
                .size(56.dp),
        )

        Text(
            text = "Rezerwacja potwierdzona!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "Płatność została zaksięgowana, a rezerwacja zapisana. " +
                "Potwierdzenie wyślemy na Twój adres e-mail.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        reservation.id?.let { id ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Numer rezerwacji",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Text(
                        text = id,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Szczegóły rezerwacji",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                trailer?.let { TrailerInfoCard(it) }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                trailerReservationDates(reservation.startDate, reservation.endDate)

                reservation.reservationPrice?.let { prices ->
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    trailerReservationPrices(prices, trailer?.prices)
                }

                ReservationTotalPrice(
                    trailer?.prices?.reservation?.asPrice(),
                    reservation.reservationPrice?.sum?.asPrice(),
                )
            }
        }

        Button(
            onClick = onGoHome,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Wróć na stronę główną")
        }

        OutlinedButton(
            onClick = onNewReservation,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Zarezerwuj ponownie")
        }
    }
}

@Composable
private fun PaymentSummaryError(
    message: String,
    onRetry: () -> Unit,
    onGoHome: () -> Unit,
) {
    Column(
        modifier = Modifier
            .widthIn(max = 480.dp)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = "Problem z potwierdzeniem rezerwacji",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Jeśli płatność została pobrana, skontaktuj się z nami: ${seller.company?.phoneNumber ?: ""}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Sprawdź ponownie")
        }

        OutlinedButton(
            onClick = onGoHome,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Wróć na stronę główną")
        }
    }
}

private fun resolveTrailer(
    trailers: List<Trailer>,
    selectedTrailer: Trailer?,
    trailerId: String?,
): Trailer? {
    if (trailerId == null) return selectedTrailer

    selectedTrailer?.takeIf { it.id == trailerId || it.prices?.trailerId == trailerId }
        ?.let { return it }

    trailers.find { it.id == trailerId || it.prices?.trailerId == trailerId }
        ?.let { return it }

    return fallbackTrailers.find { it.id == trailerId || it.prices?.trailerId == trailerId }
}
