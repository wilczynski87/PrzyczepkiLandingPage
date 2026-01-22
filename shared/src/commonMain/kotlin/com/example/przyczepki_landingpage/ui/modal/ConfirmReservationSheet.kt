package com.example.przyczepki_landingpage.ui.modal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmReservationSheet(
    viewModel: AppViewModel,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val state by viewModel.appState.collectAsState()
    val trailerName = state.selectedTrailer?.name
    val startDate = state.dateRangePickerStart
    val endDate = state.dateRangePickerEnd

//    ModalBottomSheet(
//        onDismissRequest = onCancel,
//        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
//        tonalElevation = 6.dp,
//        modifier = Modifier.fillMaxWidth()
//            .shadow(12.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
//    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Potwierdź rezerwację",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Przyczepka: ${trailerName ?: "brak"}")
            Text("Od: ${startDate ?: "-"}")
            Text("Do: ${endDate ?: "-"}")

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Anuluj")
                }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Rezerwuj")
                }
            }
        }
//    }
}