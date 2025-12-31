package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomInfo(
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Column(
            modifier = Modifier.weight(1f).padding(4.dp)
        ) {
            Text("Lokalizacja:")
            Text("ul. Ostrowskiego 102 - FAT")
            Text("Wrocław")
            Text("link")
        }
        Column (
            modifier = Modifier.weight(1f).padding(4.dp)
        ) {
            Text("tel: +48 727 188 330")
            Text("em@il: ")
            Text("parkingostrowskiego@gmail.com")
        }
        Column (
            modifier = Modifier.weight(1f).padding(4.dp)
        ) {
            Text("Rezerwacje")
            Text("Cennik")
            Text("Mapka z dojazdem")
        }
    }
}