package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.controller.ApiClient
import com.example.przyczepki_landingpage.model.CurrentScreen
import com.example.przyczepki_landingpage.data.ModalType
import com.example.przyczepki_landingpage.model.ModalData
import com.example.przyczepki_landingpage.data.Trailer
import io.ktor.util.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.log

class AppViewModel(private val scope: CoroutineScope) {
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    init {
        fetchTrailers()
    }

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

    fun reserve() {
        // TODO
    }

    fun reservationButtonClick(trailer: Trailer) {
        onTrailerSelected(trailer)
        navigateTo(CurrentScreen.RESERVATION)
    }

    fun openModal(modalType: ModalType, modal: ModalData? = null,) {
        _appState.update { it.copy(
            modal = modal,
            modalType = modalType,
            modalVisible = true
        ) }
    }

    fun closeModal() {
        _appState.update { it.copy(
            modal = null,
            modalType = ModalType.NONE,
            modalVisible = false
        ) }
    }

    fun fetchTrailers() {
//        println("fetchTrailers")
        scope.launch {
            try {
                val trailers = ApiClient.trailerController.getTrailers()

                if (trailers.isNotEmpty()) {
                    _appState.update { it.copy(trailers = trailers) }
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }


}