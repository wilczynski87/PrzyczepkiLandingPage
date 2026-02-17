package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.model.CurrentScreen

@Composable
fun TermsPage(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 900.dp)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Nagłówek strony
            Text(
                text = "Regulamin wypożyczenia",
                style = MaterialTheme.typography.headlineMedium
            )

            // Sekcja Rezerwacja z linkami
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Rezerwacja",
                        style = MaterialTheme.typography.titleLarge
                    )

                    TermsWithLinks(
                        onContactClick = { viewModel.navigateTo(CurrentScreen.CONTACT) },
                        onPickupClick = { viewModel.navigateTo(CurrentScreen.CONTACT) }
                    )
                }
            }

            // Sekcja Odbiór przyczepki
            TermsSection(
                title = "Odbiór przyczepki",
                content = listOf(
                    "Pod bramą należy zadzwonić na numer widoczny na bramie (727 188 330).",
                    "Po otwarciu bramy można podpiąć przyczepkę i odjechać.",
                    "Jeśli brama jest zamknięta – prosimy o kontakt telefoniczny."
                )
            )

            // Sekcja W trakcie wynajmu
            TermsSection(
                title = "W trakcie wynajmu",
                content = listOf(
                    "W przypadku problemów z przyczepką prosimy o kontakt.",
                    "Przejściówka znajduje się w czarnej skrzynce przy płocie."
                )
            )

            // Sekcja Zwrot przyczepki
            TermsSection(
                title = "Zwrot przyczepki",
                content = listOf(
                    "Zwrot następuje na plac przy ul. Ostrowskiego 102.",
                    "W przypadku zamkniętej bramy – należy zadzwonić na numer na bramie."
                )
            )
        }
    }
}

@Composable
fun TermsSection(
    title: String,
    content: List<String>
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            content.forEach { paragraph ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("•")
                    Text(
                        text = paragraph,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}


@Composable
fun TermsWithLinks(
    onContactClick: () -> Unit,
    onPickupClick: () -> Unit
) {
    val annotatedText = buildAnnotatedString {
        append("Rezerwacji można dokonać przez formularz na stronie lub telefonicznie / mailowo (zakładka ")

        pushLink(LinkAnnotation.Clickable(tag = "CONTACT", linkInteractionListener = { onContactClick() }))
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Medium
            )
        ) { append("Kontakt") }
        pop()

        append(").\n\nPo otrzymaniu potwierdzenia można dokonać ")

        pushLink(LinkAnnotation.Clickable(tag = "PICKUP", linkInteractionListener = { onPickupClick() }))
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Medium
            )
        ) { append("odbioru") }
        pop()

        append(" przyczepki w wybranym terminie.")
    }

    Text(
        text = annotatedText,
        style = MaterialTheme.typography.bodyLarge
    )
}

