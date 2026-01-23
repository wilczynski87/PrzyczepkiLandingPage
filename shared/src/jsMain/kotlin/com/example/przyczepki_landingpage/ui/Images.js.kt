package com.example.przyczepki_landingpage.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import przyczepkilandingpage.shared.generated.resources.Res
import przyczepkilandingpage.shared.generated.resources.przyczepka1


//actual object Images {
//        @Composable
//    actual fun landingBackground(): Painter = painterResource(Res.drawable.przyczepka1)
// TODO("Not yet implemented")
//}
actual object Images {
    @Composable
    actual fun landingBackground(name: String): Painter {
        TODO("Not yet implemented")
    }
}

//actual object Images {
//    @Composable
//    actual fun landingBackground(name: String): Painter {
//        // Mapowanie nazw na resource IDs
//        val resourceId = when (name) {
//            "hero" -> "hero_background.svg"
//            "features" -> "features_background.svg"
//            "footer" -> "footer_background.svg"
//            else -> "default_background.svg"
//        }
//        return painterResource(resourceId)
//    }
//}
