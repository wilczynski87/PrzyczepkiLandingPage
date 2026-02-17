package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Column(
            modifier = Modifier.weight(1f).padding(4.dp)
        ) {
            Row {
                Icon(
                    Icons.Default.Map,
                    "adres:",
                )
                Text("Lokalizacja:")
            }
//            Text("Lokalizacja:")
            Text("ul. Ostrowskiego 102 - FAT")
            Text("Wrocław")
        }
        Column (
            modifier = Modifier.weight(1f).padding(4.dp)
        ) {
            Row {
                Icon(
                    Icons.Default.Phone,
                    "telefon: +48 727 188 330",
                )
                Text("tel: ")
            }
            Text(
                text = "+48 727 188 330",
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Row {
                Icon(
                    Icons.Default.Email,
                    "email: parkingostrowskiego@gmail.com"
                )
                Text(
                    text = "em@il: ",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Text("parkingostrowskiego@gmail.com")
        }
        Column (
            modifier = Modifier.weight(1f)
        ) {

            if(getEnvironment() == "prod")  TextButton(
                onClick = {
                    viewModel.navigateTo(CurrentScreen.RESERVATION)
                },
                modifier = Modifier.padding(0.dp)
            ) {
                Text("Rezerwacje")
            }
            TextButton(
                onClick = {
                    viewModel.navigateTo(CurrentScreen.PRICES)
                },
                modifier = Modifier.padding(0.dp)
            ) {
                Text("Cennik")
            }
            TextButton(
                onClick = {
                    // Użycie expect funkcji
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
}