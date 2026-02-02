package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.data.CurrentScreen
import com.example.przyczepki_landingpage.data.ModalType
import com.example.przyczepki_landingpage.model.ModalData
import com.example.przyczepki_landingpage.model.Trailer
import kotlinx.serialization.Serializable

data class AppState(
    val currentScreen: CurrentScreen = CurrentScreen.LANDING,

    val modalVisible: Boolean = false,
    val modalType: ModalType = ModalType.NONE,
    val modal: ModalData? = null,

    val dateRangePickerStart: Long? = null,
    val dateRangePickerEnd: Long? = null,
    val reservationPrice: ReservationPrice? = null,
    val blockedDates: Set<Long> = emptySet(),


//    val trailers: List<Trailer> = emptyList(),
    val trailers: List<Trailer> = com.example.przyczepki_landingpage.trailers,
    val selectedTrailer: Trailer? = null,
)

@Serializable
data class ReservationPrice(
    val trailerId: Long? = null,
    val halfDay: Double,
    val reservation: Double,
    val firstDay: Double,
    val otherDays: Double,
    val daysNumber: Long = 1,
    val sum: Double = 0.0,
)