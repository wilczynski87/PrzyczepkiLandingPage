package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.data.formatDatePl
import com.example.przyczepki_landingpage.data.todayUtcMillis
import com.example.przyczepki_landingpage.model.Trailer
import com.example.przyczepki_landingpage.ui.modal.ConfirmReservationSheet
import com.example.przyczepki_landingpage.ui.reservation.TrailerSelectionList
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock

@Composable
fun ReservationPage(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.appState.collectAsState()
    val trailer: Trailer? = state.selectedTrailer

    if(widthSizeClass != WindowWidthSizeClass.Compact) {
        Row {
            Column(
                modifier = Modifier
                    .sizeIn(maxWidth = 450.dp)
            ) {
                TrailerSelectionList(
                    viewModel
                )
            }

            Column(
                modifier = Modifier
                    .sizeIn(maxWidth = 450.dp)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rezerwacja przyczepek",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                    , verticalAlignment = Alignment.CenterVertically
                    , horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if(trailer?.image == null) {
                        Icon(
                            Icons.Default.BrokenImage,
                            contentDescription = "Brak zdjęcia"
                        )
                    } else {
                        Image(
                            painter = painterResource(trailer.image),
                            contentDescription = trailer.name,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = trailer?.name ?: "Wybierz Przyczepkę",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ReservationCalendar(viewModel)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Rezerwacja przyczepek",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                , verticalAlignment = Alignment.CenterVertically
                , horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if(trailer?.image == null) {
                    Icon(
                        Icons.Default.BrokenImage,
                        contentDescription = "Brak zdjęcia"
                    )
                } else {
                    Image(
                        painter = painterResource(trailer.image),
                        contentDescription = trailer.name,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = trailer?.name ?: "Wybierz Przyczepkę",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
//                    .weight(1f)
            ) {
                ReservationCalendar(viewModel)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
//                    .weight(1f)
            ) {
                TrailerSelectionList(viewModel)
            }
        }
    }
}

@Composable
fun ReservationCalendar(
    viewModel: AppViewModel,
    allowPastDates: Boolean = false
) {
    val todayMillis = remember { todayUtcMillis() }

    val selectableDates = if (!allowPastDates) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return true
            }

            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
                return year >= currentYear
            }
        }
    } else {
        DatePickerDefaults.AllDates
    }

    val dateRangePickerState: DateRangePickerState = rememberDateRangePickerState(
        selectableDates = selectableDates
    )

    LaunchedEffect(dateRangePickerState) {
        snapshotFlow {
            dateRangePickerState.selectedStartDateMillis to
                    dateRangePickerState.selectedEndDateMillis
        }.collect { (start, end) ->
            viewModel.updateDateRangePicker(start, end)
        }
    }

    Surface(
        modifier = Modifier.sizeIn(maxWidth = 450.dp),
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
    ) {
        Column {
            TextButton(
                onClick = {
                    dateRangePickerState.displayedMonthMillis = todayMillis
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Today,
                    contentDescription = "Dzisiaj"
                )
                Spacer(Modifier.width(8.dp))
                Text("Dzisiaj")
            }

            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.height(450.dp),
                title = {
                    Text(
                        "Wybierz zakres dat:",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                    todayDateBorderColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
            Order(viewModel)
        }

    }
}

@Composable
fun Order(
    viewModel: AppViewModel
) {
    val state by viewModel.appState.collectAsState()
    val trailer: Trailer? = state.selectedTrailer
    val startDate: Long? = state.dateRangePickerStart
    val endDate: Long? = state.dateRangePickerEnd

    val showModal = remember { mutableStateOf(false) }

    val canReserve =
        trailer != null &&
        startDate != null &&
        endDate != null

    Button(
        onClick = { showModal.value = true },
        enabled = canReserve,
        modifier = Modifier.fillMaxWidth().padding(4.dp)
    ) {
        Text("Rezerwuj")
    }

    if (showModal.value) {
        ConfirmReservationSheet(
            viewModel,
            onCancel = { viewModel.closeModal() },
            onConfirm = {
                viewModel.reserve()
                viewModel.closeModal()
            },
        )
    }
}