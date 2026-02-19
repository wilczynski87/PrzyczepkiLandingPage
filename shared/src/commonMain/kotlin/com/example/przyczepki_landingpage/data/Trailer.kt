package com.example.przyczepki_landingpage.data

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource

@Serializable
data class Trailer(
    val id: Int? = null,
    val name: String? = null,
    val size: String? = null,
    val loadingMass: Double? = null,
    val gvw: Double? = null,
    val purpose: String? = null,
    val axles: Int? = null,
    val licenseCategory: LicenseCategory? = null,
    val hasBreaks: Boolean? = null,
    val hasTrailer: Boolean? = null,
    val prices: Prices? = null,
    val images: Map<String, String>? = null,
)

@Serializable
data class Prices(
    val id: Long,
    val trailerId: Long? = null,
    val firstDay: Double? = null,
    val otherDays: Double? = null,
    val halfDay: Double? = null,
    val reservation: Double? = null,
)

@Serializable
enum class LicenseCategory {
    B,
    BE,
    C,
}