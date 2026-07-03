package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.getEnvironment
import com.example.przyczepki_landingpage.model.CurrentScreen
import com.example.przyczepki_landingpage.model.latitude
import com.example.przyczepki_landingpage.model.longitude
import com.example.przyczepki_landingpage.openNavigationApp

@Composable
fun BottomInfo(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomInfoLocation()
                BottomInfoContact()
                BottomInfoLinks(viewModel)
            }
        }

        else -> {
            Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                BottomInfoLocation(modifier = Modifier.weight(1f))
                BottomInfoContact(modifier = Modifier.weight(1f))
//                BottomInfoLinks(viewModel, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BottomInfoLocation(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            Icon(Icons.Default.Map, "adres:")
            Text("Lokalizacja:")
        }
        Text("ul. Ostrowskiego 102 - FAT")
        Text("Wrocław")
        TextButton(
            onClick = {
                openNavigationApp(
                    latitude = latitude,
                    longitude = longitude,
                    label = "Przyczepki Plac"
                )
            },
            modifier = Modifier.padding(0.dp)
        ) {
            Icon(Icons.Default.Navigation, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Nawiguj do biura")
        }
    }
}

@Composable
private fun BottomInfoContact(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            Icon(Icons.Default.Phone, "telefon: +48 727 188 330")
            Text("tel: ")
        }
        Text(
            text = "+48 727 188 330",
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Row {
            Icon(Icons.Default.Email, "email: parkingostrowskiego@gmail.com")
            Text(
                text = "em@il: ",
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Text("parkingostrowskiego@gmail.com")
    }
}

@Composable
private fun BottomInfoLinks(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (getEnvironment() == "prod") {
            TextButton(
                onClick = { viewModel.navigateTo(CurrentScreen.RESERVATION) },
                modifier = Modifier.padding(0.dp)
            ) {
                Text("Rezerwacje")
            }
        }
        TextButton(
            onClick = { viewModel.navigateTo(CurrentScreen.PRICES) },
            modifier = Modifier.padding(0.dp)
        ) {
            Text("Cennik")
        }

        TextButton(
            onClick = { viewModel.navigateTo(CurrentScreen.HOW_TO_RESERVE) },
            modifier = Modifier.padding(0.dp)
        ) {
            Text("Jak rezerwować?")
        }
        TextButton(
            onClick = { viewModel.navigateTo(CurrentScreen.TERMS_AND_CONDITIONS) },
            modifier = Modifier.padding(0.dp)
        ) {
            Text("Regulamin")
        }
        TextButton(
            onClick = { viewModel.navigateTo(CurrentScreen.PRIVACY_POLICY) },
            modifier = Modifier.padding(0.dp)
        ) {
            Text("Polityka prywatności")
        }
    }
}
