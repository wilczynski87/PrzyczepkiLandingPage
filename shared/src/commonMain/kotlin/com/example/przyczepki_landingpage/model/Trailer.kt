package com.example.przyczepki_landingpage.model

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource

@Serializable
data class Trailer(
    val id: Int? = null,
    val name: String? = null,
    val size: String? = null,
    val purpose: String? = null,
    val axles: Int? = null,
    val price: Double? = null,
    val image: DrawableResource? = null,
)