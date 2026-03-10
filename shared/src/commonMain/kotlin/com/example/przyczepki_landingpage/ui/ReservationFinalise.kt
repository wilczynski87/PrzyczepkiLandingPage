package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.callPhone
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.model.ServerResponse
import com.example.przyczepki_landingpage.model.ServerStatus
import com.example.przyczepki_landingpage.openEmail
import com.example.przyczepki_landingpage.seller

@Composable
fun ReservationFinaliseMain(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.appState.collectAsState()
    val trailer: Trailer? = state.selectedTrailer
    val serverStatus: ServerStatus = state.serverStatus?.status ?: ServerStatus.UNEXPECTED_STATUS


    if(serverStatus != ServerStatus.OK) {
        ReservationServerProblemScreen(viewModel)
    } else PaymentForReservation()



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
fun PaymentForReservation() {
    Text("PaymentForReservation")
}