package com.example.przyczepki_landingpage

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.data.CurrentScreen
import com.example.przyczepki_landingpage.data.ModalType
import com.example.przyczepki_landingpage.model.Prices
import com.example.przyczepki_landingpage.model.Trailer
import com.example.przyczepki_landingpage.ui.ContactPage
import com.example.przyczepki_landingpage.ui.MainScreen
import com.example.przyczepki_landingpage.ui.MyTopAppBar
import com.example.przyczepki_landingpage.ui.ReservationPage
import com.example.przyczepki_landingpage.ui.modal.AppModals
import org.jetbrains.compose.resources.painterResource
import przyczepkilandingpage.shared.generated.resources.Res
import przyczepkilandingpage.shared.generated.resources.przyczepka1
import przyczepkilandingpage.shared.generated.resources.vesta1
import przyczepkilandingpage.shared.generated.resources.zaslaw1

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AppMainScreen() {
    // 1. Calculate window size class
    val windowSizeClass = calculateWindowSizeClass()
    // 2. Extract width size class (Compact, Medium, Expanded)
    val widthSizeClass: WindowWidthSizeClass = windowSizeClass.widthSizeClass
    val viewModel = remember { AppViewModel() }
    val currentState by viewModel.appState.collectAsState()
    val modal: ModalType = currentState.modalType
    val visible: Boolean = currentState.modalVisible

    Column(modifier = Modifier.fillMaxWidth()) {
        MyTopAppBar(viewModel)

        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(Res.drawable.przyczepka1),
                contentDescription = "logo",
                contentScale = ContentScale.Fit,
                alpha = 0.3f,
                modifier = Modifier
//                    .padding(4.dp)
                    .align(Alignment.TopCenter),
            )
            AnimatedContent(
                targetState = currentState.currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith
                    fadeOut(animationSpec = tween(220))
                },
                label = "screen-fade"
            ) { screen ->
                when (screen) {
                    CurrentScreen.LANDING -> {
                        MainScreen(widthSizeClass, viewModel)
                    }

                    CurrentScreen.RESERVATION -> {
                        ReservationPage(widthSizeClass, viewModel)
                    }

                    CurrentScreen.PRICES -> {
//                        PricesPage(widthSizeClass, viewModel)
                    }

                    CurrentScreen.CONTACT -> {
                        ContactPage(widthSizeClass, viewModel)
                    }

                    CurrentScreen.TERMS_AND_CONDITIONS -> {
//                        TermsPage(widthSizeClass)
                    }

                    CurrentScreen.LOGIN -> {
//                        LoginPage(viewModel)
                    }

                    CurrentScreen.SIGN_UP -> {
//                        SignUpPage(viewModel)
                    }
                }
            }

        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.95f),
        exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.95f)
    ) {
        if (modal != ModalType.NONE) {
            AppModals(viewModel)
        }
    }
}


val trailers = listOf(
    Trailer(
        name = "Przyczepka lekka - Vesta light 25",
        size = "252,1 × 135,4 × 37,3 cm",
        loadingMass = 520.00,
        gvw = 750.00,
        purpose = "Towarowa",
        axles = 1,
        licenseCategory = "B",
        prices = Prices(1,1, 60.00, 50.00, 40.00, 40.00),
        image = Res.drawable.vesta1
    ),
    Trailer(
        name = "Przyczepka lekka - Zasław HL300T",
        size = "300 x 150 x 35 cm",
        loadingMass = 465.00,
        gvw = 750.00,
        purpose = "Towarowa",
        axles = 2,
        licenseCategory = "B",
        prices = Prices(2,2, 70.00, 60.00, 50.00, 50.00),
        image = Res.drawable.zaslaw1
    ),

)