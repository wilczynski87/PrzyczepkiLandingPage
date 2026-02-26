package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.controller.ApiClient
import com.example.przyczepki_landingpage.model.CurrentScreen
import com.example.przyczepki_landingpage.model.ModalType
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.model.ModalData
import com.example.przyczepki_landingpage.data.Trailer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(private val scope: CoroutineScope) {
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    init {
        scope.launch {
            checkServerHealth()
            fetchTrailers()
            fetchReservations()
        }
    }

    fun navigateTo(destination: CurrentScreen) {
        _appState.update {
            it.copy(
                currentScreen = destination
            )
        }
    }

    fun updateDateRangePicker(start: Long?, end: Long?) {
        _appState.value = appState.value.copy(
            dateRangePickerStart = start,
            dateRangePickerEnd = end
        )
    }

    /*
        Sever health check
     */
    fun checkServerHealth() {
        scope.launch {
            val serverStatus = ApiClient.healthCheck.healthCheck()

            _appState.update { it.copy(serverStatus = serverStatus) }
        }
    }

    fun onTrailerSelected(trailer: Trailer? = null) {
        scope.launch {
            val trailerId: String? = trailer?.id ?: appState.value.selectedTrailer?.id
            val reservations = appState.value.reservations
            val blockedDates = mapReservationsToBlockedDates(reservations, trailerId)

            _appState.update {
                it.copy(
                    selectedTrailer = trailer,
                    blockedDates = blockedDates
                )
            }
//            println("onTrailerSelected blockedDates: $blockedDates")
//            println("onTrailerSelected reservations: $reservations")
//            println("onTrailerSelected trailerId: $trailerId")
        }
    }

    fun checkReservation(reservation: ReservationDto) {
        scope.launch {
            ApiClient.reservationController.checkReservation(reservation)
                .onSuccess { reservation ->
                    _appState.update { currentState -> currentState.copy(
                            reservationToMake = reservation,
                            reservationErrors = emptyList()
                        )
                    }
                }.onFailure { errors ->
                    _appState.update { currentState -> currentState.copy(
                            reservationToMake = reservation,
                            reservationErrors = errors.message?.split(",") ?: emptyList()
                        )
                    }
                }
        }
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

    /*
        RESERVATION
     */
    fun fetchReservations() {
        scope.launch {
            try {
                val reservations = ApiClient.reservationController.getReservations()
                if (reservations.isNotEmpty()) {
                    _appState.update { it.copy(reservations = reservations) }
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    private fun mapReservationsToBlockedDates(
        reservations: List<ReservationDto>? = null,
        trailerId: String? = null
    ): Set<Long> {
        val reservations: List<ReservationDto> = reservations ?: appState.value.reservations
        val trailerId: String = trailerId ?: appState.value.selectedTrailer?.id ?: return emptySet()

        val blockedDates = mutableSetOf<Long>()

        reservations.filter { it.trailerId == trailerId }.forEach {
            var current = it.startDate ?: throw NullPointerException("mapReservationsToBlockedDates: Brakuje daty rozpoczęcia")
            while (current <= (it.endDate
                    ?: throw NullPointerException("mapReservationsToBlockedDates: Brakuje daty zakończenia"))
            ) {
                blockedDates.add(current)
                current += 24 * 60 * 60 * 1000
            }
        }
        return blockedDates
    }

}