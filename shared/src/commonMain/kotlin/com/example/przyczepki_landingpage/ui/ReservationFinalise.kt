package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.callPhone
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.model.CurrentScreen
import com.example.przyczepki_landingpage.model.ModalData
import com.example.przyczepki_landingpage.model.ModalType
import com.example.przyczepki_landingpage.model.ServerStatus
import com.example.przyczepki_landingpage.model.asPrice
import com.example.przyczepki_landingpage.model.validateReservationPayment
import com.example.przyczepki_landingpage.openEmail
import com.example.przyczepki_landingpage.seller
import com.example.przyczepki_landingpage.ui.modal.ReservationTotalPrice
import com.example.przyczepki_landingpage.ui.modal.TrailerInfoCard
import com.example.przyczepki_landingpage.ui.payment.Przelewy24Payment
import com.example.przyczepki_landingpage.ui.modal.trailerReservationDates
import com.example.przyczepki_landingpage.ui.modal.trailerReservationPrices

@Composable
fun ReservationFinaliseMain(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.appState.collectAsState()
    val trailer: Trailer? = state.selectedTrailer
    val serverStatus: ServerStatus = state.serverStatus?.status ?: ServerStatus.UNEXPECTED_STATUS


    if (serverStatus != ServerStatus.OK) {
        ReservationServerProblemScreen(viewModel)
    } else {
        PaymentForReservation(widthSizeClass, viewModel, modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationServerProblemScreen(
    viewModel: AppViewModel,
) {
    val phone = seller.company?.phoneNumber ?: ""
    val email = seller.company?.email ?: ""

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
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Problem z serwerem rezerwacji",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Prosimy skontaktować się z nami w celu dokonania rezerwacji.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(24.dp))

                    ElevatedButton(
                        onClick = { callPhone("+48727188330") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Phone, null)
                        Spacer(Modifier.width(8.dp))
                        Text(phone)
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { openEmail("parkingostrowskiego@gmail.com") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Email, null)
                        Spacer(Modifier.width(8.dp))
                        Text(email)
                    }
                }
            }
        }
}

@Composable
fun PaymentForReservation(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.appState.collectAsState()
    val reservationToMake = state.reservationToMake
    val trailer = state.selectedTrailer
    val from = reservationToMake?.startDate
    val to = reservationToMake?.endDate
    val reservationPrices = reservationToMake?.reservationPrice
    val customer = state.customer
    val isLoggedIn = customer?.id != null
    val hasTrailer = trailer != null
    val hasPeriod = from != null && to != null
    var termsAccepted by remember { mutableStateOf(false) }
    val paymentValidation = validateReservationPayment(
        isLoggedIn = isLoggedIn,
        hasTrailer = hasTrailer,
        hasPeriod = hasPeriod,
        termsAccepted = termsAccepted,
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            NavigationBackBar(
                { viewModel.navigateTo(CurrentScreen.RESERVATION) },
                "Powrót do rezerwacji",
            )

            ReservationFinaliseHeader()

            ReservationSection(
                step = 1,
                title = "Dane klienta",
                subtitle = if (isLoggedIn) "Rezerwacja na Twoje dane" else "Zaloguj się lub podaj dane",
                icon = Icons.Filled.Person,
            ) {
                if (isLoggedIn) {
                    CustomerDataFront(viewModel, useCard = false)
                } else {
                    GuestCustomerActions(viewModel)
                }
            }

            ReservationSection(
                step = 2,
                title = "Podsumowanie rezerwacji",
                subtitle = "Sprawdź termin, przyczepkę i koszty",
                icon = Icons.Filled.Event,
            ) {
                trailer?.let { currentTrailer ->
                    TrailerInfoCard(currentTrailer)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                trailerReservationDates(from, to)

                if (reservationPrices != null) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    trailerReservationPrices(reservationPrices, trailer?.prices)
                }

                ReservationTotalPrice(
                    trailer?.prices?.reservation?.asPrice(),
                    reservationToMake?.reservationPrice?.sum?.asPrice(),
                )
            }

            ReservationSection(
                step = 3,
                title = "Płatność",
                subtitle = "Opłać kaucję rezerwacyjną online",
                icon = Icons.Filled.Lock,
                wrapContentInCard = false,
            ) {
                Przelewy24Payment(
                    depositAmount = reservationPrices?.reservation ?: trailer?.prices?.reservation,
                    totalAmount = reservationPrices?.sum,
                    canPay = paymentValidation.canPay,
                    paymentIssues = paymentValidation.issues.map { it.message },
                    termsAccepted = termsAccepted,
                    onTermsAcceptedChange = { termsAccepted = it },
                    onOpenTerms = { viewModel.navigateTo(CurrentScreen.TERMS_AND_CONDITIONS) },
                    onOpenPrivacyPolicy = { viewModel.navigateTo(CurrentScreen.PRIVACY_POLICY) },
                    onPay = {
                        if (!paymentValidation.canPay) return@Przelewy24Payment
                        viewModel.changeServerStatus(ServerStatus.SERVER_ERROR)
                        // TODO: podłączyć inicjację transakcji Przelewy24 (backend)
                    },
                )
            }
        }
    }
}

@Composable
private fun ReservationFinaliseHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Finalizacja rezerwacji",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Sprawdź dane, podsumowanie i opłać kaucję, aby potwierdzić rezerwację.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ReservationSection(
    step: Int,
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    wrapContentInCard: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text(
                            text = step.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (wrapContentInCard) {
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
                    content = content,
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun GuestCustomerActions(
    viewModel: AppViewModel,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Aby dokończyć rezerwację, zaloguj się na istniejące konto lub podaj swoje dane.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Button(
            onClick = {
                viewModel.openModal(
                    ModalType.LOGIN,
                    ModalData(
                        onConfirmation = { viewModel.navigateTo(CurrentScreen.RESERVATION_FINALISE) },
                        dialogTitle = "Logowanie",
                        dialogText = "Zaloguj się, aby kontynuować rezerwację.",
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Zaloguj się")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "lub",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        OutlinedButton(
            onClick = { viewModel.navigateTo(CurrentScreen.SIGN_UP) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Podaj dane bez logowania")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBackBar(onClick: () -> Unit = {}, title: String = "Powrót") {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        IconButton(onClick = onClick ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Powrót do rezerwacji"
            )
        }
        Text(
            title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}


