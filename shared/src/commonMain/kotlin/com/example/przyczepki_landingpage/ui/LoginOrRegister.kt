package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.model.CurrentScreen
import com.example.przyczepki_landingpage.model.ModalData
import com.example.przyczepki_landingpage.model.ModalType

@Composable
fun LoginOrRegister(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel
) {
    LoggingOptions(widthSizeClass, viewModel)
}

@Composable
fun LoggingOptions(
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    viewModel: AppViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            viewModel.openModal(
                ModalType.LOGIN,
                ModalData(
                    onConfirmation = { viewModel.navigateTo(CurrentScreen.RESERVATION_FINALISE) },
                    dialogTitle = "Zadzwoń/napisz w celu rezerwacji",
                    dialogText = "Czy na pewno chcesz rezerwować przyczepkę?",
                )
            )
        }) {
            Text("Logowanie")
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurfaceVariant )
        Text("nie masz konta?")
        Button(onClick = {
            viewModel.navigateTo(CurrentScreen.SIGN_UP)
        }) {
            Text("Podaj dane")
        }
    }

}