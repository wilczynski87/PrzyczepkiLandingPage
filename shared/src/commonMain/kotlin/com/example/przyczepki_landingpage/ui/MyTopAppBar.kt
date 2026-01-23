package com.example.przyczepki_landingpage.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.data.CurrentScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(viewModel: AppViewModel) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { WebTitle() },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Prawe MENU
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Strona główna") },
                    onClick = {
                        expanded = false
                        viewModel.navigateTo(CurrentScreen.LANDING)
                    }
                )
//                DropdownMenuItem(
//                    text = { Text("Rezerwacja") },
//                    onClick = {
//                        expanded = false
//                        viewModel.navigateTo(CurrentScreen.RESERVATION)
//                    }
//                )
                DropdownMenuItem(
                    text = { Text("Kontakt") },
                    onClick = {
                        expanded = false
                        viewModel.navigateTo(CurrentScreen.CONTACT)
                    }
                )
            }
        }
    )
}

@Composable
fun WebTitle() {
    Text("Przyczepki lekkie - FAT")
}
