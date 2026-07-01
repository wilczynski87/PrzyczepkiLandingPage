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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun HowToReservePage(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 900.dp)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = "Jak rezerwować?",
                style = MaterialTheme.typography.headlineMedium,
            )

            Text(
                text = "Poniżej znajdziesz instrukcję rezerwacji online oraz informacje o odbiorze i zwrocie przyczepy.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Rezerwacja online — krok po kroku",
                        style = MaterialTheme.typography.titleLarge,
                    )

                    val onlineSteps = listOf(
                        "Wejdź w zakładkę Rezerwacje i wybierz przyczepkę z listy.",
                        "W kalendarzu zaznacz datę rozpoczęcia i zakończenia wynajmu.",
                        "Sprawdź podsumowanie kosztów i potwierdź rezerwację.",
                        "Podaj dane klienta lub zaloguj się na istniejące konto.",
                        "Zaakceptuj regulamin i politykę prywatności, a następnie opłać kaucję rezerwacyjną online.",
                        "Po zaksięgowaniu płatności otrzymasz potwierdzenie rezerwacji.",
                    )
                    onlineSteps.forEachIndexed { index, step ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Rezerwacja telefoniczna lub mailowa",
                        style = MaterialTheme.typography.titleLarge,
                    )

                    ReservationContactInfo(
                        onContactClick = { viewModel.navigateTo(CurrentScreen.CONTACT) },
                    )
                }
            }

            TermsSection(
                title = "Odbiór przyczepki",
                content = listOf(
                    "Odbiór następuje w wybranym terminie na placu przy ul. Ostrowskiego 102 we Wrocławiu.",
                    "Pod bramą należy zadzwonić na numer widoczny na bramie (727 188 330).",
                    "Po otwarciu bramy można podpiąć przyczepkę i odjechać.",
                    "Jeśli brama jest zamknięta – prosimy o kontakt telefoniczny.",
                ),
            )

            TermsSection(
                title = "W trakcie wynajmu",
                content = listOf(
                    "W przypadku problemów z przyczepką prosimy o kontakt.",
                    "Przejściówka znajduje się w czarnej skrzynce przy płocie.",
                ),
            )

            TermsSection(
                title = "Zwrot przyczepki",
                content = listOf(
                    "Zwrot następuje na plac przy ul. Ostrowskiego 102.",
                    "W przypadku zamkniętej bramy – należy zadzwonić na numer na bramie.",
                ),
            )
        }
    }
}

@Composable
private fun ReservationContactInfo(
    onContactClick: () -> Unit,
) {
    val annotatedText = buildAnnotatedString {
        append("Rezerwacji można dokonać również telefonicznie lub mailowo — dane kontaktowe znajdziesz w zakładce ")

        pushLink(LinkAnnotation.Clickable(tag = "CONTACT", linkInteractionListener = { onContactClick() }))
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Medium,
            )
        ) { append("Kontakt") }
        pop()

        append(". Po uzgodnieniu terminu możesz odebrać przyczepkę w wybranym dniu.")
    }

    Text(
        text = annotatedText,
        style = MaterialTheme.typography.bodyLarge,
    )
}
