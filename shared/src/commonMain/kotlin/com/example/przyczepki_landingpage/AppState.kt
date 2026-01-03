package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.data.CurrentScreen
import com.example.przyczepki_landingpage.model.Trailer

data class AppState(
    val currentScreen: CurrentScreen = CurrentScreen.LANDING,

    val dateRangePickerStart: Long? = null,
    val dateRangePickerEnd: Long? = null,

    val trailers: List<Trailer> = emptyList(),
    val selectedTrailer: Trailer? = null
)
