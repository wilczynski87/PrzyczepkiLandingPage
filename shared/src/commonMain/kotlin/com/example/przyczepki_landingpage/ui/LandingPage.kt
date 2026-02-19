package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.model.ServerStatus
import com.example.przyczepki_landingpage.trailers

@Composable
fun MainScreen(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.appState.collectAsState()

    val trailers = state.trailers
    val serverStatus = state.serverStatus

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier.widthIn(max = 600.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FrontInfoText()
                if (serverStatus == null) {
                    LoadingScreen()
                } else if (serverStatus.status != ServerStatus.OK) {
                    ServerStatusError(serverStatus)
                } else TrailerTable(trailers = trailers, widthSizeClass = widthSizeClass, appViewModel =  viewModel)
            }
        }

        // ⭐ magia tutaj
        item {
            BottomInfo(
                widthSizeClass,
                viewModel,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}