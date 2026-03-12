package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.data.Company
import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.LicenseCategory
import com.example.przyczepki_landingpage.data.Prices
import com.example.przyczepki_landingpage.data.Private
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


val vesta = mapOf<String, String>(
    "thumbnail" to "https://i.postimg.cc/t1FRyJMH/vesta4.webp",
    "plandeka" to "https://i.postimg.cc/YSsv6mNj/vesta4.webp",
    "przod" to "https://i.postimg.cc/SKvn6zL2/vesta_przod.webp",
    "bokPrzod" to "https://i.postimg.cc/28G8dZmP/vesta3.webp",
    "tyl" to "https://i.postimg.cc/pd6mfnQp/vesta2.webp",
)

val zaslaw = mapOf<String, String>(
    "thumbnail" to "https://i.postimg.cc/D8QF00rt/zaslaw1.webp",
    "bokZamkniety" to "https://i.postimg.cc/zX6fdRVX/zaslaw1.webp",
    "bokOtwarty" to "https://i.postimg.cc/YqZSXmhH/zaslaw_bok.webp",
    "przod" to "https://i.postimg.cc/TYFPkWph/zaslaw4.webp",
    "tyl" to "https://i.postimg.cc/kXz5jtB2/zaslaw2.webp",
)

val trailers = listOf(
    Trailer(
        name = "Przyczepka lekka - Vesta light 25",
        size = "252,1 × 135,4 × 37,3 cm",
        loadingMass = 520.00,
        gvw = 750.00,
        purpose = "Towarowa",
        axles = 1,
        licenseCategory = LicenseCategory.B,
        hasBreaks = false,
        prices = Prices("1", 60.00, 50.00, 40.00, 40.00, 40.00),
        images = vesta
    ),
    Trailer(
        name = "Przyczepka lekka - Zasław HL300T",
        size = "300 x 150 x 35 cm",
        loadingMass = 465.00,
        gvw = 750.00,
        purpose = "Towarowa",
        axles = 2,
        licenseCategory = LicenseCategory.B,
        hasBreaks = false,
        prices = Prices("2", 70.00, 60.00, 50.00, 50.00, reservation = 50.00),
        images = zaslaw
    ),
)

val seller = Customer (
    id = null,
    private = Private(
        firstName = "Karol",
        lastName = "Wilczyński",
        address = "ul. Ostrowskiego 102, 53-238 Wrocław",
        email = "wilczynski87@gmail.com",
        phoneNumber = "+48 507 036 484",
        pesel = "87071804991"
    ),
    company = Company(
        name = "Kontenery Magazynowe sp. z o.o.",
        address = "ul. Ostrowskiego 102, 53-238 Wrocław",
        email = "parkingostrowskiego@gmail.com",
        phoneNumber = "+48 727 188 330",
        nip = "8943278612"
    )
)
