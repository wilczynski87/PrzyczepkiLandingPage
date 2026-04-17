package com.example.przyczepki_landingpage.ui.modal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.data.Prices
import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.data.ReservationPrice
import com.example.przyczepki_landingpage.model.ModalType
import com.example.przyczepki_landingpage.model.asPrice
import com.example.przyczepki_landingpage.model.formatDatePl
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.model.CurrentScreen
import com.example.przyczepki_landingpage.model.LoginUiState
import com.example.przyczepki_landingpage.seller
import com.example.przyczepki_landingpage.ui.LoadingScreen
import com.example.przyczepki_landingpage.ui.LoginScreen
import com.example.przyczepki_landingpage.ui.ReservationServerProblemScreen
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

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
        ModalType.CALL_FOR_RESERVATION -> {
            ReservationServerProblemScreen(viewModel)
        }
        ModalType.LOGIN -> {
            LoginModal(viewModel)
        }
        ModalType.CUSTOMER_ERROR -> {
            CustomerErrorModal(viewModel)
        }
    }
}

@Composable
fun ReservationConfirmationModal(
    viewModel: AppViewModel
) {
    val state by viewModel.appState.collectAsState()

    val modal = state.modal

    val reservationToMake = state.reservationToMake
    val trailer = state.selectedTrailer

    val from = reservationToMake?.startDate
    val to = reservationToMake?.endDate
    val reservationPrices = reservationToMake?.reservationPrice

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
//                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if(reservationToMake == null) {
                    LoadingScreen("loading reservation")
                } else
                // Heade
                ReservationHeader(modal?.dialogTitle, modal?.dialogText)

                // Trailer info
                trailer?.let { currentTrailer ->
                    TrailerInfoCard(currentTrailer)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dates
                trailerReservationDates(from, to)

                Spacer(modifier = Modifier.height(12.dp))

                // Prices
                if(reservationPrices != null) trailerReservationPrices(reservationPrices, trailer?.prices)

                Spacer(modifier = Modifier.height(12.dp))

                // Total price
                ReservationTotalPrice(trailer?.prices?.reservation?.asPrice(), reservationToMake?.reservationPrice?.sum?.asPrice())

                Spacer(modifier = Modifier.height(12.dp))

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
                            viewModel.closeModal()
                            viewModel.navigateTo(CurrentScreen.RESERVATION_FINALISE)

                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Przejdź dalej")
                    }
                }
            }
        }
    }
}
@Composable
fun ReservationHeader(dialogTitle: String? = "", dialogText: String? = "") {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Icon(
            Icons.Default.Event,
            contentDescription = "rezerwacja",
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = dialogTitle ?: "",
            style = MaterialTheme.typography.titleLarge
        )
    }

    Text(
        text = dialogText ?: "",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun ReservationTotalPrice(reservationPrice: String?, reservationToMake: String?) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(end = 4.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Koszt rezerwacji:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${reservationPrice ?: "Błąd!"} zł",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
//            VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(vertical = 4.dp))
            Column(
                modifier = Modifier.padding(start = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Całkowity koszt:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${reservationToMake ?: "Błąd!"} zł",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}


@Composable
fun TrailerInfoCard(trailer: Trailer) {
    Text(
        text = "Przyczepka:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(1.dp)
            .alpha(0.5f)
    )
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
//            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                modifier = Modifier
                    .padding(1.dp)
                    .background(Color.Transparent),
            ) {
                KamelImage(
                    resource = {
                        trailer.images?.get("thumbnail")?.let { asyncPainterResource(it) }!!
                    },
                    contentDescription = "Przyczepka",
                    contentScale = ContentScale.Crop,
                    onLoading = {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    onFailure = {
                        Box(
                            Modifier.matchParentSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.BrokenImage,
                                contentDescription = "Błąd",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(10.dp))
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
fun trailerReservationDates(
    from: LocalDate?,
    to: LocalDate?,
    modifier: Modifier = Modifier
) {
    val daysNumber = if(from != null && to != null) {
        from.daysUntil(to)
    } else 0

    Text(
        text = "Termin rezerwacji:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(1.dp)
            .alpha(0.5f)
    )

    Column(modifier = modifier) {

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
                    text = from?.formatDatePl() ?: "-",
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
                    text = to?.formatDatePl() ?: "-",
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
    reservationPrice: ReservationPrice?,
    prices: Prices?,
    modifier: Modifier = Modifier
) {
    if (reservationPrice == null || reservationPrice.daysNumber == null) return
    var furtherInfo: Boolean by remember { mutableStateOf(false) }

    Text(
        text = "Ceny:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(1.dp)
            .alpha(0.5f)
    )

    Column(modifier = modifier) {
        // Nagłówek sekcji
        OutlinedButton(
            onClick = {furtherInfo = !furtherInfo},
        ) {
            Text(
                text = "Szczegóły płatności",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
            )
        }
        if(furtherInfo) {
        // Lista pozycji cenowych
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pół dnia (jeśli dotyczy)
                if (prices?.halfDay != null) {
                    PriceRow(
                        label = "Wynajem pół dnia",
                        amount = prices.halfDay,
                        showInfo = true,
                        infoText = "Do 6 godzin wynajmu"
                    )
                }

                // Pierwszy dzień
                PriceRow(
                    label = if (reservationPrice.daysNumber == 1L) "Wynajem 1 dnia"
                    else "Pierwszy dzień",
                    amount = prices?.firstDay ?: throw NullPointerException("Brak 1 dnia rezerwacji"),
                )

                // Drugi dzień
                if (reservationPrice.daysNumber > 1 && prices.secondDay != null) {
                    prices.secondDay
                    PriceRow(
                        label = "Drugi dzień",
                        amount = prices.secondDay,
                        showInfo = true,
                        infoText = "${prices.secondDay.asPrice()} za dzień"
                    )
                }

                // Kolejne dni (jeśli więcej niż 1 dzień)
                if (reservationPrice.daysNumber > 2) {
                    prices.otherDays ?: throw NullPointerException("Brak kolejnych dni rezerwacji")
                    val subsequentDays = reservationPrice.daysNumber - 2
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
                    amount = reservationPrice.reservation ?: 40.00,
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
                    amount = reservationPrice.sum ?: throw NullPointerException("Brak sumy wypożyczenia"),
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

@Composable
fun LoginModal(viewModel: AppViewModel) {
    val state: LoginUiState = LoginUiState()
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
//                .fillMaxHeight(0.9f)
        ) {
            LoginScreen(
                viewModel,
                {},
                {},
                {},
                {},
                {},
            )
        }
    }
}

@Composable
fun CustomerErrorModal(viewModel: AppViewModel) {
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
//                .fillMaxHeight(0.9f)
        ) {
            Text("Error in customer seve")
        }
    }
}