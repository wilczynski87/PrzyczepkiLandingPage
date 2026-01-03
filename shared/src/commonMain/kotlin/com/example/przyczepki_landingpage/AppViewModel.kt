package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.data.CurrentScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel {
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    fun navigateTo(destination: CurrentScreen) {
        _appState.value = appState.value.copy(currentScreen = destination)
    }

    fun updateDateRangePicker(start: Long?, end: Long?) {
        _appState.value = appState.value.copy(
            dateRangePickerStart = start,
            dateRangePickerEnd = end
        )
    }

}