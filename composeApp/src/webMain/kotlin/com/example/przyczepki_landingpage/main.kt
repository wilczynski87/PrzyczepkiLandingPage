package com.example.przyczepki_landingpage

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import theme.ui.theme.AppTypography

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        AppTheme {
            App()
        }
    }
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val lightColors = lightColorScheme()
    val darkColors  = darkColorScheme()

    val colorScheme = if (darkTheme) darkColors else lightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
//        shapes = AppShapes,
        content = content
    )
}