package com.example.przyczepki_landingpage.data

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource

@Serializable
data class Trailer(
    val id: String? = null,
    val name: String? = null,
    val size: String? = null,
    val loadingMass: Double? = null,
    val gvw: Double? = null,
    val purpose: String? = null,
    val axles: Int? = null,
    val licenseCategory: LicenseCategory? = null,
    val hasBreaks: Boolean? = null,
    val prices: Prices? = null,
    val images: Map<String, String>? = null,
) {
}

@Serializable
data class Prices(
    val trailerId: String? = null,
    val firstDay: Double? = null,
    val secondDay: Double? = null,
    val otherDays: Double? = null,
    val halfDay: Double? = null,
    val reservation: Double? = null,
) {
    constructor(trailerId: String, prices: Prices) : this (
        trailerId = trailerId,
        firstDay = prices.firstDay,
        secondDay = prices.secondDay,
        otherDays = prices.otherDays,
        halfDay = prices.halfDay,
        reservation = prices.reservation,
    )
}

@Serializable
enum class LicenseCategory {
    B,
    BE,
    C,
}