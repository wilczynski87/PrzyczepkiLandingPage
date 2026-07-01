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
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel

private const val LAST_UPDATED = "01.07.2026"

private const val PAYPRO_PAYMENT_OPERATOR_CLAUSE =
    "Płatności online realizowane są za pośrednictwem systemu Przelewy24. " +
        "Operatorem płatności jest PayPro S.A. z siedzibą w Poznaniu (ul. Pastelowa 8, 60-198 Poznań), " +
        "wpisana do rejestru przedsiębiorców Krajowego Rejestru Sądowego prowadzonego przez Sąd Rejonowy " +
        "Poznań–Nowe Miasto i Wilda w Poznaniu, VIII Wydział Gospodarczy KRS pod numerem 0000347935, " +
        "NIP 7792369887, REGON 301345068. W przypadku aktywacji płatności kartami operatorem kart " +
        "płatniczych jest PayPro S.A. Agent Rozliczeniowy."

@Composable
fun TermsPage(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
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

            Text(
                text = "Ostatnia aktualizacja: $LAST_UPDATED",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            TermsSection(
                title = "Postanowienia ogólne",
                content = listOf(
                    "Regulamin określa zasady wypożyczania przyczep samochodowych przez stronę internetową.",
                    "Złożenie rezerwacji i opłacenie kaucji rezerwacyjnej oznacza akceptację niniejszego Regulaminu.",
                    "Usługodawcą jest Kontenery Magazynowe sp. z o.o., ul. Ostrowskiego 102, 53-238 Wrocław, " +
                        "NIP 8943278612, KRS 0001220381, e-mail: parkingostrowskiego@gmail.com, tel.: +48 727 188 330.",
                )
            )

            TermsSection(
                title = "Płatności",
                content = listOf(
                    "Ceny wynajmu oraz kaucji rezerwacyjnej są podawane przed złożeniem rezerwacji na stronie.",
                    "Kaucja rezerwacyjna jest pobierana online w momencie finalizacji rezerwacji. Pozostała część " +
                        "wynagrodzenia płatna jest przy odbiorze przyczepy, o ile nie ustalono inaczej.",
                    "Dostępne metody płatności online: szybkie przelewy i inne metody udostępnione w systemie Przelewy24.",
                    PAYPRO_PAYMENT_OPERATOR_CLAUSE,
                    "Po kliknięciu przycisku Zapłać Klient zostanie przekierowany na bezpieczną stronę operatora płatności.",
                    "Rezerwacja zostaje potwierdzona po zaksięgowaniu płatności kaucji rezerwacyjnej.",
                )
            )

            TermsSection(
                title = "Prawo odstąpienia od umowy",
                content = listOf(
                    "Konsument ma prawo odstąpić od umowy zawartej na odległość w terminie 14 dni od jej zawarcia, " +
                        "bez podania przyczyny, z zastrzeżeniem wyjątków przewidzianych w ustawie o prawach konsumenta.",
                    "Prawo odstąpienia nie przysługuje m.in. w odniesieniu do umów o świadczenie usług, jeżeli " +
                        "przedsiębiorca wykonał w pełni usługę za wyraźną zgodą konsumenta, który został poinformowany " +
                        "przed rozpoczęciem świadczenia, że po spełnieniu świadczenia przez przedsiębiorcę utraci prawo " +
                        "odstąpienia od umowy.",
                    "Oświadczenie o odstąpieniu można przesłać na adres e-mail: parkingostrowskiego@gmail.com " +
                        "lub listownie na adres siedziby Usługodawcy.",
                )
            )

            TermsSection(
                title = "Reklamacje",
                content = listOf(
                    "Reklamacje dotyczące usługi wypożyczenia można składać telefonicznie, mailowo lub pisemnie " +
                        "na dane kontaktowe Usługodawcy wskazane w Regulaminie.",
                    "Reklamacja powinna zawierać opis problemu oraz dane umożliwiające identyfikację rezerwacji.",
                    "Usługodawca rozpatruje reklamację w terminie 14 dni od jej otrzymania.",
                    "Reklamacje dotyczące płatności online realizowanych przez Przelewy24 mogą być kierowane " +
                        "również do operatora płatności PayPro S.A.",
                )
            )

            // Sekcja Dane firmy
            TermsSection(
                title = "Dane przedsiębiorstwa",
                content = listOf(
                    "Kontenery Magazynowe sp. z o.o.",
                    "ul. Ostrowskiego 102, 53-238 Wrocław",
                    "NIP: 8943278612",
                    "KRS: 0001220381",
                    "E-mail: parkingostrowskiego@gmail.com",
                    "Telefon: +48 727 188 330",
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
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }

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

