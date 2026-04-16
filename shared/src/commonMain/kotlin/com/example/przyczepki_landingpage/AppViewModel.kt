package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.controller.ApiClient
import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.model.CurrentScreen
import com.example.przyczepki_landingpage.model.ModalType
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.model.ModalData
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.model.ServerStatus
import com.example.przyczepki_landingpage.model.millisToLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.onSuccess

class AppViewModel(private val scope: CoroutineScope) {
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    init {
        scope.launch {
            val serverStatus = ApiClient.healthCheck.healthCheck()
            if(serverStatus.status != ServerStatus.OK) {
                _appState.update {
                    it.copy(
                        serverStatus = serverStatus,
                        trailers = trailers
                    )
                }
            } else {
                _appState.update {
                    it.copy(
                        serverStatus = serverStatus,
                    )
                }
                fetchTrailers()
                fetchReservations()
            }
        }
    }

    fun navigateTo(destination: CurrentScreen) {
        _appState.update {
            it.copy(
                currentScreen = destination
            )
        }
    }

    fun updateDateRangePicker(start: LocalDate?, end: LocalDate?) {
        _appState.value = appState.value.copy(
            dateRangePickerStart = start,
            dateRangePickerEnd = end
        )
    }
    fun updateDateRangePicker(start: Long?, end: Long?) {
        val start = if(start == null) null else millisToLocalDate(start)
        val end = if(end == null) null else millisToLocalDate(end)

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
            try {
                println("HealthCheck: Checking server health...")
                val serverStatus = ApiClient.healthCheck.healthCheck()
                println("Server status: $serverStatus")

                _appState.update { state ->
                    state.copy(
                        serverStatus = serverStatus,
                        trailers = if (serverStatus.status != ServerStatus.OK && state.trailers.isEmpty())
                            trailers
                        else
                            state.trailers
                    )
                }

            } catch (e: Exception) {
                println("Error healthcheck: ${e.message}")
            }

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
    ): Set<LocalDate> {
        val reservations: List<ReservationDto> = reservations ?: appState.value.reservations
        val trailerId: String = trailerId ?: appState.value.selectedTrailer?.id ?: return emptySet()

        val blockedDates = mutableSetOf<LocalDate>()

        reservations.filter { it.trailerId == trailerId }.forEach {
            var current = it.startDate ?: throw NullPointerException("mapReservationsToBlockedDates: Brakuje daty rozpoczęcia")
            while (current <= (it.endDate
                    ?: throw NullPointerException("mapReservationsToBlockedDates: Brakuje daty zakończenia"))
            ) {
                blockedDates.add(current)
                current.plus(1, DateTimeUnit.DAY)
            }
        }
        return blockedDates
    }


    // CUSTOMER
    // State update
    fun updateCustomer(transform: (Customer) -> Customer) {
        _appState.update { state ->
            val current = state.customer ?: Customer()
            state.copy(
                customer = transform(current)
            )
        }
    }

    // Server update
    fun saveCustomer() {
        scope.launch {
            val customer = appState.value.customer ?: return@launch
            ApiClient.customerController.saveCustomer(customer)
                .onSuccess {
                    _appState.update { state ->
                        state.copy(
                            customer = it
                        )
                    }
                    navigateTo(CurrentScreen.RESERVATION_FINALISE)
                }.onFailure {
                    println("Error: ${it.message}")
                    openModal(ModalType.CUSTOMER_ERROR, ModalData(
                        dialogTitle = "Błąd przy zapisie danych klienta",
                        dialogText = it.message ?: "Unknown error")
                    )
                }
        }
    }

    fun putCustomer() {
        scope.launch {
            val customer = appState.value.customer ?: return@launch
            ApiClient.customerController.updateCustomer(customer)
                .onSuccess {
                    _appState.update { state ->
                        state.copy(
                            customer = it
                        )
                    }
                }
                .onFailure {
                    println("Error: ${it.message}")
                    openModal(ModalType.CUSTOMER_ERROR, ModalData(
                        dialogTitle = "Błąd przy aktualizacji danych klienta",
                        dialogText = it.message ?: "Unknown error")
                    )
                }
        }
    }

    fun deleteCustomer(customerId: String? = null) {
        scope.launch {
            val customerId: String = customerId ?: appState.value.customer?.id ?: return@launch
            ApiClient.customerController.deleteCustomer(customerId)
                .onSuccess {
                    _appState.update { state ->
                        state.copy(
                            customer = null
                        )
                    }
                }
                .onFailure {
                    println("Error: ${it.message}")
                    openModal(ModalType.CUSTOMER_ERROR, ModalData(
                        dialogTitle = "Błąd przy usuwaniu danych klienta",
                        dialogText = it.message ?: "Unknown error")
                    )
                }
        }
    }

    fun fetchCustomer(customerId: String? = null) {
        scope.launch {
            val customerId: String = customerId ?: appState.value.customer?.id ?: return@launch
            ApiClient.customerController.getCustomer(customerId)
                .onSuccess { customer ->
                    _appState.update { state ->
                        state.copy(
                            customer = customer
                        )
                    }
                }
                .onFailure {
                    println("Error: ${it.message}")
                    openModal(ModalType.CUSTOMER_ERROR, ModalData(
                        dialogTitle = "Błąd przy pobieraniu danych klienta",
                        dialogText = it.message ?: "Unknown error")
                    )
                }
        }
    }
}