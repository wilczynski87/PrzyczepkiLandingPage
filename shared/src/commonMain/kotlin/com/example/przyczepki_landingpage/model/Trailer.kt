package com.example.przyczepki_landingpage.model

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
    val licenseCategory: String? = null,
    val prices: Prices? = null,
    val image: DrawableResource? = null,
)

@Serializable
data class Prices(
    val id: Long,
    val trilerId: Long? = null,
    val firstDay: Double,
    val nextDay: Double,
    val half: Double,
    val reservation: Double
)