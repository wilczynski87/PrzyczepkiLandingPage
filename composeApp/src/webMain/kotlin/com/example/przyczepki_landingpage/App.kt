package com.example.przyczepki_landingpage

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun App() {
    AppMainScreen()
}

@Preview
@Composable
fun SimpleComposablePreview() {
    App()
}
