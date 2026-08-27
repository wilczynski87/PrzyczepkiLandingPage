package com.example.przyczepki_landingpage

import com.example.przyczepki_landingpage.data.Company
import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.LicenseCategory
import com.example.przyczepki_landingpage.data.Prices
import com.example.przyczepki_landingpage.data.Private
import com.example.przyczepki_landingpage.model.CurrentScreen
import com.example.przyczepki_landingpage.model.ModalType
import com.example.przyczepki_landingpage.data.ReservationDto
import com.example.przyczepki_landingpage.data.PaymentStatusResponse
import com.example.przyczepki_landingpage.model.ModalData
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.model.LoginUiState
import com.example.przyczepki_landingpage.model.ServerResponse
import kotlinx.datetime.LocalDate

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
    val trailerDetailReturnScreen: CurrentScreen = CurrentScreen.LANDING,

    val accessToken: String? = null,
    val refreshToken: String? = null,

    val customer: Customer? = null,

    val paymentProcessing: Boolean = false,
    val paymentStatus: PaymentStatusResponse? = null,
    val paymentStatusLoading: Boolean = false,
    val paymentStatusError: String? = null,

    val loginUiState: LoginUiState = LoginUiState(),
)

val ikona = mapOf<String, String>(
    "thumbnail" to "https://i.postimg.cc/2qc864bS/ikona-przyczepkifat.png",
    "icon" to "https://i.postimg.cc/rmcdfjqx/ikona-przyczepkifat.png"
)

val logo = mapOf(
    "thumbnail" to "https://i.postimg.cc/yWHwwDrt/logo-przyczepkifat-kadr.png",
    "logo" to "https://i.postimg.cc/2jbrLvdk/logo-przyczepkifat-kadr.png"
)

val vesta = mapOf<String, String>(
    // Postimages: page-id URL = ~180px thumbnail; use website hotlink / original
    "thumbnail" to "https://i.postimg.cc/YSsv6mNj/vesta4.webp",      // 1200x800
    "przod" to "https://i.postimg.cc/SKvn6zL2/vesta-przod.webp",      // 1067x800
    "tyl" to "https://i.postimg.cc/pd6mfnQp/vesta2.webp",             // 1200x800
    "bok" to "https://i.postimg.cc/28G8dZmP/vesta3.webp",              // 1200x800
    "plandeka" to "https://i.postimg.cc/YSsv6mNj/vesta4.webp",         // 1200x800
)

val zaslaw = mapOf<String, String>(
    "thumbnail" to "https://i.postimg.cc/zX6fdRVX/zaslaw1.webp",       // 1280x720 (card)
    "bok" to "https://i.postimg.cc/YqZSXmhH/zaslaw-bok.webp",          // 1067x800
    "bokZamkniety" to "https://i.postimg.cc/fDT0RcsX/zaslaw1.webp",    // 2048x1152 original
    "tyl" to "https://i.postimg.cc/rMFtw1TN/zaslaw2.webp",             // 2048x1152 original
    "przod" to "https://i.postimg.cc/7wYTZ341/zaslaw4.webp",           // 2048x1152 original
)

val carroDelta = mapOf<String, String>(
    "thumbnail" to "https://i.postimg.cc/13Vn5Hgy/Carro-Flat.webp",    // 1151x500
    "carroFlat" to "https://i.postimg.cc/13Vn5Hgy/Carro-Flat.webp",    // 1151x500
    "carroBack" to "https://i.postimg.cc/Dw4JygW2/Carro-Back.webp",    // 1080x617
    "carroFront" to "https://i.postimg.cc/cJqKKn3P/Carro-Front.webp",  // 959x636
)

val trailers = listOf(
    Trailer(
        name = "Przyczepka lekka - Vesta light 25",
        size = "252,1 × 135,4 × 37,3 cm",
        loadingMass = 520.00,
        gvw = 750.00,
        purpose = "Towarowa",
        zastosowanie = "Przewóz towarów, mebli i AGD w mieście (kat. B).",
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
        zastosowanie = "Przeprowadzki i większe ładunki (kat. B).",
        axles = 2,
        licenseCategory = LicenseCategory.B,
        hasBreaks = false,
        prices = Prices("2", 70.00, 60.00, 50.00, 50.00, reservation = 50.00),
        images = zaslaw
    ),
    Trailer(
        name = "Przyczepka lekka - platforma CARRO DELTA D4020",
        size = "400 x 200 x 40 cm",
        loadingMass = 413.00,
        gvw = 750.00,
        purpose = "Towarowa",
        zastosowanie = "Platforma 4×2 m — palety, maszyny, długie elementy (kat. B).",
        axles = 2,
        licenseCategory = LicenseCategory.B,
        hasBreaks = false,
        prices = Prices("2", 80.00, 70.00, 60.00, 60.00, reservation = 60.00),
        images = carroDelta
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
