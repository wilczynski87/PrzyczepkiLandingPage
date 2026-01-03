package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.data.CurrentScreen
import com.example.przyczepki_landingpage.data.formatDatePl
import com.example.przyczepki_landingpage.model.Trailer
import com.example.przyczepki_landingpage.trailers
import com.example.przyczepki_landingpage.ui.reservation.TrailerSelectionList
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun ReservationPage(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.appState.collectAsState()

    val allowPastDates = false
    val triler: Trailer? = state.selectedTrailer

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
        Row {
            if(triler?.image == null) {
                Icon(
                    Icons.Default.BrokenImage,
                    contentDescription = "Brak zdjęcia"
                )
            } else {
                Image(
                    painter = painterResource(triler.image),
                    contentDescription = triler.name ?: ""
                )
            }
            Text(
                text = triler?.name ?: "Wybierz Przyczepkę",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        val selectableDates = if (!allowPastDates) {
            object : androidx.compose.material3.SelectableDates {
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
            tonalElevation = 3.dp
        ) {
            TrailerSelectionList(trailers) {}

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
        }
    }



}

