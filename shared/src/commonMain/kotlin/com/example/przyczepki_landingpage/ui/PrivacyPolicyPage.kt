package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

private const val LAST_UPDATED = "30.06.2026"

@Composable
fun PrivacyPolicyPage(
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
                text = "Polityka prywatności",
                style = MaterialTheme.typography.headlineMedium
            )

            // Wstęp
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Niniejsza Polityka prywatności określa zasady przetwarzania " +
                            "i ochrony danych osobowych przekazanych przez Użytkowników w związku " +
                            "z korzystaniem ze strony oraz usług wypożyczalni przyczep. Przetwarzanie " +
                            "danych odbywa się zgodnie z Rozporządzeniem Parlamentu Europejskiego i Rady (UE) " +
                            "2016/679 z dnia 27 kwietnia 2016 r. (RODO).",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Ostatnia aktualizacja: $LAST_UPDATED",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Administrator danych
            TermsSection(
                title = "1. Administrator danych osobowych",
                content = listOf(
                    "Administratorem danych jest Kontenery Magazynowe sp. z o.o.",
                    "Adres: ul. Ostrowskiego 102, 53-238 Wrocław",
                    "NIP: 8943278612, KRS: 0001220381",
                    "E-mail: parkingostrowskiego@gmail.com",
                    "Telefon: +48 727 188 330"
                )
            )

            // Cele i podstawy prawne
            TermsSection(
                title = "2. Cele i podstawy prawne przetwarzania",
                content = listOf(
                    "Realizacja rezerwacji i umowy najmu przyczepy — art. 6 ust. 1 lit. b RODO " +
                        "(wykonanie umowy lub działania przed jej zawarciem).",
                    "Obsługa płatności online (Przelewy24) — art. 6 ust. 1 lit. b RODO.",
                    "Wystawianie i przechowywanie dokumentów księgowych oraz wypełnianie obowiązków " +
                        "podatkowych — art. 6 ust. 1 lit. c RODO (obowiązek prawny).",
                    "Obsługa zapytań i kontakt (telefon, e-mail, formularz) — art. 6 ust. 1 lit. f RODO " +
                        "(prawnie uzasadniony interes Administratora).",
                    "Ustalenie, dochodzenie i obrona przed roszczeniami — art. 6 ust. 1 lit. f RODO."
                )
            )

            // Zakres danych
            TermsSection(
                title = "3. Zakres przetwarzanych danych",
                content = listOf(
                    "Dane identyfikacyjne i kontaktowe: imię i nazwisko lub nazwa firmy, adres, " +
                        "e-mail, numer telefonu.",
                    "Dane niezbędne do zawarcia umowy najmu oraz rozliczeń (w tym NIP w przypadku firm).",
                    "Dane dotyczące rezerwacji (wybrana przyczepa, terminy, kwoty).",
                    "Dane techniczne związane z korzystaniem ze strony (np. pliki cookies)."
                )
            )

            // Odbiorcy danych
            TermsSection(
                title = "4. Odbiorcy danych",
                content = listOf(
                    "Operator płatności Przelewy24 (PayPro S.A.) — w zakresie obsługi płatności online.",
                    "Biuro rachunkowe oraz podmioty świadczące obsługę księgową i prawną.",
                    "Dostawcy usług IT i hostingu, w zakresie niezbędnym do działania strony.",
                    "Organy publiczne, jeżeli wynika to z obowiązujących przepisów prawa.",
                    "Dane nie są przekazywane do państw trzecich ani nie podlegają zautomatyzowanemu " +
                        "podejmowaniu decyzji (profilowaniu)."
                )
            )

            // Okres przechowywania
            TermsSection(
                title = "5. Okres przechowywania danych",
                content = listOf(
                    "Dane związane z umową najmu — przez czas trwania umowy, a po jej zakończeniu przez " +
                        "okres przedawnienia roszczeń (co do zasady do 6 lat).",
                    "Dokumenty księgowe i podatkowe — przez okres wymagany przepisami, tj. 5 lat licząc " +
                        "od końca roku, w którym powstał obowiązek podatkowy.",
                    "Dane przetwarzane na podstawie prawnie uzasadnionego interesu — do czasu wniesienia " +
                        "skutecznego sprzeciwu lub ustania celu przetwarzania.",
                    "Dane z korespondencji kontaktowej — przez okres niezbędny do obsługi zapytania."
                )
            )

            // Prawa użytkownika
            TermsSection(
                title = "6. Twoje prawa",
                content = listOf(
                    "Prawo dostępu do danych oraz uzyskania ich kopii.",
                    "Prawo do sprostowania (poprawienia) danych.",
                    "Prawo do usunięcia danych („prawo do bycia zapomnianym”).",
                    "Prawo do ograniczenia przetwarzania.",
                    "Prawo do przenoszenia danych.",
                    "Prawo do wniesienia sprzeciwu wobec przetwarzania opartego na uzasadnionym interesie.",
                    "Prawo do wniesienia skargi do Prezesa Urzędu Ochrony Danych Osobowych (PUODO), " +
                        "ul. Stawki 2, 00-193 Warszawa."
                )
            )

            // Dobrowolność
            TermsSection(
                title = "7. Dobrowolność podania danych",
                content = listOf(
                    "Podanie danych jest dobrowolne, jednak niezbędne do zawarcia i realizacji umowy najmu " +
                        "oraz obsługi rezerwacji i płatności.",
                    "Niepodanie danych uniemożliwia dokonanie rezerwacji oraz zawarcie umowy."
                )
            )

            // Cookies
            TermsSection(
                title = "8. Pliki cookies",
                content = listOf(
                    "Strona może wykorzystywać pliki cookies niezbędne do jej prawidłowego działania.",
                    "Ustawienia plików cookies można zmienić w każdej chwili w ustawieniach przeglądarki."
                )
            )

            // Kontakt
            TermsSection(
                title = "9. Kontakt w sprawie danych",
                content = listOf(
                    "W sprawach dotyczących ochrony danych osobowych prosimy o kontakt:",
                    "E-mail: parkingostrowskiego@gmail.com",
                    "Telefon: +48 727 188 330"
                )
            )
        }
    }
}
