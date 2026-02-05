package com.example.przyczepki_landingpage.repo.trailer_repo_impl

import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.trailers

class TrailersRepoImpl: TrailersRepo {
    override suspend fun getTrailers(): List<Trailer> {
        return trailers
    }

    override suspend fun getTrailer(id: Int): Trailer? {
        return trailers.find { it.id == id }
    }

    override suspend fun saveTrailer(trailer: Trailer): Trailer? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrailer(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateTrailer(trailer: Trailer): Trailer? {
        TODO("Not yet implemented")
    }
}


//@Serializable
//data class Trailer(
//    val id: Int? = null,
//    val name: String? = null,
//    val size: String? = null,
//    val loadingMass: Double? = null,
//    val gvw: Double? = null,
//    val purpose: String? = null,
//    val axles: Int? = null,
//    val licenseCategory: LicenseCategory? = null,
//    val hasBreaks: Boolean? = null,
//    val hasTrailer: Boolean? = null,
//    val prices: Prices? = null,
//    val images: Map<String, String>? = null,
//)
//
//@Serializable
//data class Prices(
//    val id: Long,
//    val trailerId: Long? = null,
//    val firstDay: Double,
//    val otherDays: Double,
//    val halfDay: Double,
//    val reservation: Double
//)
//
//@Serializable
//enum class LicenseCategory {
//    B,
//    BE,
//    C,
//}

//val trailers = listOf(
//    Trailer(
//        name = "Przyczepka lekka - Vesta light 25",
//        size = "252,1 × 135,4 × 37,3 cm",
//        loadingMass = 520.00,
//        gvw = 750.00,
//        purpose = "Towarowa",
//        axles = 1,
//        licenseCategory = LicenseCategory.B,
//        hasBreaks = false,
//        prices = Prices(1,1, 60.00, 50.00, 40.00, 40.00),
////        image = Res.drawable.vesta1
//    ),
//    Trailer(
//        name = "Przyczepka lekka - Zasław HL300T",
//        size = "300 x 150 x 35 cm",
//        loadingMass = 465.00,
//        gvw = 750.00,
//        purpose = "Towarowa",
//        axles = 2,
//        licenseCategory = LicenseCategory.B,
//        hasBreaks = false,
//        prices = Prices(2,2, 70.00, 60.00, 50.00, 50.00),
////        image = Res.drawable.zaslaw1
//    ),
//)