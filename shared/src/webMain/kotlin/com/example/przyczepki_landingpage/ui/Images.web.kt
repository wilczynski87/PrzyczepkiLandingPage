package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource

actual object Images {
//    @Composable
//    actual fun landingBackground(name: String): Painter = painterResource(name)

    @OptIn(InternalResourceApi::class)
    @Composable
    actual fun landingBackground(name: String): Painter {
        TODO("Not yet implemented")
//        return painterResource(name)
    }
}
