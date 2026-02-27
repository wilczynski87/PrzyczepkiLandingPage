package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.model.CurrentScreen
import com.example.przyczepki_landingpage.model.ModalType
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.model.ModalData
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.model.ServerResponse
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

data class AppState(
    val currentScreen: CurrentScreen = CurrentScreen.LANDING,

    val serverStatus: ServerResponse? = null,

    val modalVisible: Boolean = false,
    val modalType: ModalType = ModalType.NONE,
    val modal: ModalData? = null,

    val dateRangePickerStart: LocalDate? = null,
    val dateRangePickerEnd: LocalDate? = null,
    val blockedDates: Set<LocalDate> = emptySet(),
    val reservations: List<ReservationDto> = emptyList(),

    val reservationToMake: ReservationDto? = null,
    val reservationErrors: List<String> = emptyList(),

    val trailers: List<Trailer> = emptyList(),
    val selectedTrailer: Trailer? = null,

    val customer: Customer? = null,
)