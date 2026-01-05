package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.data.CurrentScreen
import com.example.przyczepki_landingpage.model.Trailer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

    fun onTrailerSelected(trailer: Trailer? = null) {
        _appState.update {
            it.copy(selectedTrailer = trailer)
        }
    }


}