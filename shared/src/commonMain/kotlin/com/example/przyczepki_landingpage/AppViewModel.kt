package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.auth.clearRememberedCredentials
import com.example.przyczepki_landingpage.auth.clearStoredCustomerId
import com.example.przyczepki_landingpage.auth.customerIdFromJwt
import com.example.przyczepki_landingpage.auth.loadRememberedCredentials
import com.example.przyczepki_landingpage.auth.loadStoredCustomerId
import com.example.przyczepki_landingpage.auth.saveRememberedCredentials
import com.example.przyczepki_landingpage.auth.saveStoredCustomerId
import com.example.przyczepki_landingpage.controller.ApiClient
import com.example.przyczepki_landingpage.controller.InvalidLoginCredentialsException
import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.data.LoginResponse
import com.example.przyczepki_landingpage.data.PAYMENT_RETURN_PATH
import com.example.przyczepki_landingpage.data.PAYMENT_SESSION_STORAGE_KEY
import com.example.przyczepki_landingpage.data.PaymentSessionStatus
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.model.CurrentScreen
import com.example.przyczepki_landingpage.model.LoginUiState
import com.example.przyczepki_landingpage.model.ModalData
import com.example.przyczepki_landingpage.model.ModalType
import com.example.przyczepki_landingpage.model.ServerResponse
import com.example.przyczepki_landingpage.model.ServerStatus
import com.example.przyczepki_landingpage.model.mapLoginError
import com.example.przyczepki_landingpage.model.millisToLocalDate
import com.example.przyczepki_landingpage.model.validateLoginInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class AppViewModel(private val scope: CoroutineScope) {
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    init {
        hydrateRememberedCredentials()
        scope.launch {
            // Najpierw obsłuż powrót z P24 — niezależnie od health checka
            handlePaymentReturnIfNeeded()

            val serverStatus = ApiClient.healthCheck.healthCheck()
            if (serverStatus.status != ServerStatus.OK) {
                _appState.update {
                    it.copy(
                        serverStatus = serverStatus,
                        trailers = trailers,
                    )
                }
            } else {
                _appState.update {
                    it.copy(serverStatus = serverStatus)
                }
                fetchTrailers()
                fetchReservations()
                restoreSessionIfPossible()
            }
        }
    }

    fun navigateTo(destination: CurrentScreen) {
        _appState.update {
            it.copy(currentScreen = destination)
        }
        when (destination) {
            CurrentScreen.RESERVATION_SUMMARY -> replaceBrowserPath(PAYMENT_RETURN_PATH)
            CurrentScreen.LANDING -> {
                if (getCurrentPath().endsWith(PAYMENT_RETURN_PATH)) {
                    replaceBrowserPath("/")
                }
            }
            else -> Unit
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

    fun changeServerStatus(serverStatus: ServerStatus) {
        _appState.update { state ->
            state.copy(
                serverStatus = ServerResponse(serverStatus, "Changed manually"),
            )
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

    fun openTrailerDetail(
        trailer: Trailer,
        returnScreen: CurrentScreen = CurrentScreen.LANDING,
    ) {
        _appState.update {
            it.copy(
                selectedTrailer = trailer,
                trailerDetailReturnScreen = returnScreen,
                currentScreen = CurrentScreen.TRAILER_DETAIL,
            )
        }
    }

    fun openNextTrailerDetail() {
        val trailers = appState.value.trailers
        if (trailers.isEmpty()) return
        val current = appState.value.selectedTrailer
        val currentIndex = trailers.indexOfFirst { trailer ->
            when {
                current?.id != null && trailer.id != null -> trailer.id == current.id
                else -> trailer.name == current?.name
            }
        }
        val nextIndex = if (currentIndex < 0) 0 else (currentIndex + 1) % trailers.size
        openTrailerDetail(
            trailer = trailers[nextIndex],
            returnScreen = appState.value.trailerDetailReturnScreen,
        )
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
            modalVisible = false,
            loginUiState = it.loginUiState.copy(isLoading = false, error = null)
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
                val trailerId = appState.value.selectedTrailer?.id
                val blockedDates = if (trailerId != null) {
                    mapReservationsToBlockedDates(reservations, trailerId)
                } else {
                    appState.value.blockedDates
                }
                _appState.update {
                    it.copy(
                        reservations = reservations,
                        blockedDates = blockedDates,
                    )
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
                current = current.plus(1, DateTimeUnit.DAY)
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
    fun saveCustomer(password: String) {
        scope.launch {
            val customer = appState.value.customer ?: return@launch
            ApiClient.customerController.saveCustomer(customer, password)
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
                    clearRememberedCredentials()
                    clearLocalSession()
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
        println("fetchCustomer")
        scope.launch {
            fetchCustomerInternal(
                customerId = customerId ?: appState.value.customer?.id ?: return@launch,
                closeLoginModal = true,
                showError = true,
            )
        }
    }

    private suspend fun fetchCustomerInternal(
        customerId: String,
        closeLoginModal: Boolean,
        showError: Boolean,
    ) {
        ApiClient.customerController.getCustomer(customerId)
            .onSuccess { customer ->
                saveStoredCustomerId(customerId)
                _appState.update { state ->
                    state.copy(customer = customer)
                }
                if (closeLoginModal) closeModal()
            }
            .onFailure { error ->
                println("Error: ${error.message}")
                if (error is InvalidLoginCredentialsException) {
                    clearLocalSession()
                }
                if (showError) {
                    openModal(ModalType.CUSTOMER_ERROR, ModalData(
                        dialogTitle = "Błąd przy pobieraniu danych klienta",
                        dialogText = error.message ?: "Unknown error")
                    )
                }
            }
    }

    fun initiatePayment(depositAmount: Double) {
        scope.launch {
            val customer = appState.value.customer
            val email = customer?.getEmail()
            val reservation = appState.value.reservationToMake?.copy(customerId = customer?.id)
            if (customer == null || email.isNullOrBlank()) {
                openModal(
                    ModalType.CUSTOMER_ERROR,
                    ModalData(
                        dialogTitle = "Brak adresu email",
                        dialogText = "Zaloguj się lub podaj email klienta przed płatnością.",
                    ),
                )
                return@launch
            }
            if (reservation == null || reservation.trailerId.isNullOrBlank() ||
                reservation.startDate == null || reservation.endDate == null
            ) {
                openModal(
                    ModalType.CUSTOMER_ERROR,
                    ModalData(
                        dialogTitle = "Brak danych rezerwacji",
                        dialogText = "Wybierz przyczepę i termin przed płatnością.",
                    ),
                )
                return@launch
            }

            val amountInGrosze = kotlin.math.round(depositAmount * 100).toInt()
            _appState.update { it.copy(paymentProcessing = true) }

            ApiClient.paymentController.registerPayment(
                amount = amountInGrosze,
                customer = customer,
                reservation = reservation,
                description = "Kaucja rezerwacyjna",
                regulationAccept = true,
            ).onSuccess { response ->
                setLocalStorageValue(PAYMENT_SESSION_STORAGE_KEY, response.sessionId)
                openExternalUrl(response.redirectUrl)
            }.onFailure {
                println("Payment error: ${it.message}")
                openModal(
                    ModalType.CUSTOMER_ERROR,
                    ModalData(
                        dialogTitle = "Błąd płatności",
                        dialogText = it.message ?: "Nie udało się rozpocząć płatności",
                    ),
                )
            }

            _appState.update { it.copy(paymentProcessing = false) }
        }
    }

    private fun handlePaymentReturnIfNeeded() {
        val path = getCurrentPath().substringBefore('?').trimEnd('/')
        val onReturnPath = path.endsWith(PAYMENT_RETURN_PATH) || path.endsWith("podsumowanieRezerwacji")
        val sessionId = getLocalStorageValue(PAYMENT_SESSION_STORAGE_KEY)

        // Wejście z P24 albo odświeżenie z aktywną sesją płatności
        if (!onReturnPath && sessionId.isNullOrBlank()) return

        navigateTo(CurrentScreen.RESERVATION_SUMMARY)

        if (sessionId.isNullOrBlank()) {
            _appState.update {
                it.copy(
                    paymentStatusLoading = false,
                    paymentStatusError = "Brak identyfikatora sesji płatności. Spróbuj ponownie lub skontaktuj się z nami.",
                )
            }
            return
        }

        pollPaymentStatus(sessionId)
    }

    fun retryPaymentStatusCheck() {
        val sessionId = getLocalStorageValue(PAYMENT_SESSION_STORAGE_KEY)
        if (sessionId.isNullOrBlank()) {
            _appState.update {
                it.copy(
                    paymentStatusLoading = false,
                    paymentStatusError = "Brak identyfikatora sesji płatności. Spróbuj ponownie lub skontaktuj się z nami.",
                )
            }
            return
        }
        pollPaymentStatus(sessionId)
    }

    private fun pollPaymentStatus(sessionId: String) {
        scope.launch {
            _appState.update {
                it.copy(
                    paymentStatusLoading = true,
                    paymentStatusError = null,
                    paymentStatus = null,
                )
            }

            repeat(45) { attempt -> // ~90s przy delay 2s
                ApiClient.paymentController.getPaymentStatus(sessionId).onSuccess { status ->
                    _appState.update {
                        it.copy(
                            paymentStatus = status,
                            // Loader zostaje, dopóki nie ma COMPLETED / FAILED
                            paymentStatusLoading = status.status == PaymentSessionStatus.PENDING ||
                                status.status == PaymentSessionStatus.VERIFIED,
                            paymentStatusError = null,
                        )
                    }

                    when (status.status) {
                        PaymentSessionStatus.PENDING,
                        PaymentSessionStatus.VERIFIED -> {
                            delay(2000)
                            return@repeat
                        }
                        PaymentSessionStatus.COMPLETED -> {
                            removeLocalStorageValue(PAYMENT_SESSION_STORAGE_KEY)
                            status.reservation?.trailerId?.let { trailerId ->
                                val trailer = appState.value.trailers.find {
                                    it.id == trailerId || it.prices?.trailerId == trailerId
                                } ?: trailers.find {
                                    it.id == trailerId || it.prices?.trailerId == trailerId
                                }
                                trailer?.let { onTrailerSelected(it) }
                            }
                            _appState.update {
                                it.copy(
                                    reservationToMake = status.reservation,
                                    paymentStatusLoading = false,
                                )
                            }
                            fetchReservations()
                            return@launch
                        }
                        PaymentSessionStatus.FAILED -> {
                            removeLocalStorageValue(PAYMENT_SESSION_STORAGE_KEY)
                            _appState.update {
                                it.copy(
                                    paymentStatusLoading = false,
                                    paymentStatusError = status.message ?: "Płatność nie powiodła się.",
                                )
                            }
                            return@launch
                        }
                    }
                }.onFailure { error ->
                    // Chwilowy błąd sieci — spróbuj ponownie zamiast od razu padać
                    if (attempt < 2) {
                        delay(2000)
                        return@repeat
                    }
                    _appState.update {
                        it.copy(
                            paymentStatusLoading = false,
                            paymentStatusError = error.message ?: "Nie udało się sprawdzić statusu płatności.",
                        )
                    }
                    return@launch
                }
            }

            _appState.update {
                it.copy(
                    paymentStatusLoading = false,
                    paymentStatusError = "Przekroczono czas oczekiwania na potwierdzenie płatności. Jeśli opłaciłeś rezerwację, skontaktuj się z nami.",
                )
            }
        }
    }

    // AUTH
    fun onLoginInputChange(value: String) {
        _appState.update { state ->
            state.copy(
                loginUiState = state.loginUiState.copy(login = value, error = null)
            )
        }
    }

    fun onLoginPasswordChange(value: String) {
        _appState.update { state ->
            state.copy(
                loginUiState = state.loginUiState.copy(password = value, error = null)
            )
        }
    }

    fun onRememberCredentialsChange(remember: Boolean) {
        _appState.update { state ->
            state.copy(
                loginUiState = state.loginUiState.copy(rememberCredentials = remember)
            )
        }
    }

    fun login() {
        val loginState = appState.value.loginUiState
        val loginInput = loginState.login.trim()
        val password = loginState.password

        // Walidacja danych wejściowych przed wysłaniem do serwera
        val validationError = validateLoginInput(loginInput, password)
        if (validationError != null) {
            _appState.update { state ->
                state.copy(
                    loginUiState = state.loginUiState.copy(
                        error = validationError,
                        isLoading = false
                    )
                )
            }
            return
        }

        _appState.update { state ->
            state.copy(
                loginUiState = state.loginUiState.copy(isLoading = true, error = null)
            )
        }

        scope.launch {
            try {
                val result: Result<LoginResponse> =
                    ApiClient.authController.login(LoginRequest(loginInput, password))

                result.onSuccess { loginResponse ->
                    println("Zalogowano: ${loginResponse.token}")
                    ApiClient.tokenManager.setTokens(
                        loginResponse.token,
                        loginResponse.refreshToken
                    )
                    saveStoredCustomerId(loginResponse.customerId)

                    val remember = loginState.rememberCredentials
                    if (remember) {
                        saveRememberedCredentials(loginInput, password)
                    } else {
                        clearRememberedCredentials()
                    }

                    _appState.update { state ->
                        state.copy(
                            accessToken = loginResponse.token,
                            refreshToken = loginResponse.refreshToken,
                            loginUiState = LoginUiState(
                                login = if (remember) loginInput else "",
                                password = if (remember) password else "",
                                rememberCredentials = remember,
                            )
                        )
                    }

                    fetchCustomer(loginResponse.customerId)

                }.onFailure { error ->
                    println("Nie udało się zalogować: ${error.message}")
                    _appState.update { state ->
                        state.copy(
                            loginUiState = state.loginUiState.copy(
                                isLoading = false,
                                error = mapLoginError(error)
                            )
                        )
                    }
                }
            } catch (error: Throwable) {
                println("Nie udało się zalogować: ${error.message}")
                _appState.update { state ->
                    state.copy(
                        loginUiState = state.loginUiState.copy(
                            isLoading = false,
                            error = mapLoginError(error)
                        )
                    )
                }
            }
        }
    }

    fun logout() {
        scope.launch {
            clearLocalSession()
        }
    }

    private fun hydrateRememberedCredentials() {
        val saved = loadRememberedCredentials() ?: return
        _appState.update { state ->
            state.copy(
                loginUiState = state.loginUiState.copy(
                    login = saved.login,
                    password = saved.password,
                    rememberCredentials = true,
                )
            )
        }
    }

    private suspend fun restoreSessionIfPossible() {
        val access = ApiClient.tokenManager.getAccessToken()
        val refresh = ApiClient.tokenManager.getRefreshToken()
        if (access.isNullOrBlank() && refresh.isNullOrBlank()) return

        val customerId = loadStoredCustomerId()
            ?: customerIdFromJwt(access)
            ?: customerIdFromJwt(refresh)
            ?: return

        _appState.update {
            it.copy(
                accessToken = access,
                refreshToken = refresh,
            )
        }
        fetchCustomerInternal(
            customerId = customerId,
            closeLoginModal = false,
            showError = false,
        )
    }

    private suspend fun clearLocalSession() {
        ApiClient.tokenManager.clearTokens()
        clearStoredCustomerId()
        val remembered = rememberedLoginUiState()
        _appState.update { state ->
            state.copy(
                customer = null,
                accessToken = null,
                refreshToken = null,
                loginUiState = remembered,
            )
        }
    }

    private fun rememberedLoginUiState(): LoginUiState {
        val saved = loadRememberedCredentials()
        return if (saved != null) {
            LoginUiState(
                login = saved.login,
                password = saved.password,
                rememberCredentials = true,
            )
        } else {
            LoginUiState(rememberCredentials = appState.value.loginUiState.rememberCredentials)
        }
    }
}