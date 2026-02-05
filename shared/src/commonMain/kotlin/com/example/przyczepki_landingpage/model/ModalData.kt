package com.example.przyczepki_landingpage.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.przyczepki_landingpage.data.Trailer
import org.jetbrains.compose.resources.DrawableResource

data class ModalData(
    val onDismissRequest: () -> Unit = {},
    val onConfirmation: () -> Unit = {},
    val dialogTitle: String = "",
    val dialogText: String = "",
    val dialogText2: String = "",
    val icon: ImageVector? = null ,
    val thumbnail: DrawableResource? = null,
)

data class ConfirmReservationModalData(
    val onDismissRequest: () -> Unit = {},
    val onConfirmation: () -> Unit = {},
    val trailer: Trailer,

    )