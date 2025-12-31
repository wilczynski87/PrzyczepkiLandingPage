package com.example.przyczepki_landingpage.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import przyczepkilandingpage.shared.generated.resources.Res
import przyczepkilandingpage.shared.generated.resources.przyczepka1
import przyczepkilandingpage.shared.generated.resources.przyczepka3

@Composable
fun TrilerList() {

}

val trailersExample = listOf(
    Trailer(
        name = "Przyczepka lekka 750kg",
        size = "210 x 130 cm",
        purpose = "Transport sprzętu ogrodowego, materiałów budowlanych",
        axles = 1,
        image = Res.drawable.przyczepka1
    ),
    Trailer(
        name = "Przyczepka dłużycowa",
        size = "300 x 150 cm",
        purpose = "Długie elementy, deski, rury",
        axles = 1,
        image = Res.drawable.przyczepka3
    ),
    Trailer(
        name = "Przyczepa podłodziowa",
        size = "Do łodzi 4–5m",
        purpose = "Transport łodzi",
        axles = 1,
        image = null
    )
)

data class Trailer(
    val name: String? = null,
    val size: String? = null,
    val purpose: String? = null,
    val axles: Int? = null,
    val price: Double? = null,
    val image: DrawableResource? = null,
)

@Composable
fun TrailerTable(
    trailers: List<Trailer> = emptyList(),
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    modifier: Modifier = Modifier
) {
//    LazyColumn(
//        modifier = Modifier.padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        items(trailers) { trailer ->
//            if (widthSizeClass != WindowWidthSizeClass.Compact) {
//                TrailerCardBig(trailer)
//            } else {
//                TrailerCardSmall(trailer)
//            }
//        }
//        item {
//            Spacer(Modifier.height(4.dp))
//        }
//    }
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        trailers.forEach { trailer ->
            if (widthSizeClass != WindowWidthSizeClass.Compact) {
                TrailerCardBig(trailer)
            } else {
                TrailerCardSmall(trailer)
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
fun TrailerCardBig(trailer: Trailer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(trailer.image == null) {
                Icon(Icons.Default.BrokenImage, "brak zdjęcia")
            }
            else {
                Image(
                    painter = painterResource(trailer.image),
                    contentDescription = trailer.name,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    trailer.name ?: "Brak nazwy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                TableRow("Wymiary", trailer.size ?: "Brak wymiarów")
                TableRow("Przeznaczenie", trailer.purpose ?: "Brak przeznaczenia")
                TableRow("Liczba osi", "${trailer.axles ?: "Brak informacji"}")
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
            ) {
                TableRowPrice("Cena", "${trailer.price ?: "Brak informacji"}")
            }
        }
        Reservation()
    }
}

@Composable
fun TrailerCardSmall(trailer: Trailer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier) {
                if(trailer.image != null) {
                    Image(
                        painter = painterResource(trailer.image),
                        contentDescription = trailer.name,
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(14.dp))
                            .alpha(0.5f)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                        contentScale = ContentScale.Fit
                    )
                }

                Column(modifier = Modifier.align(Alignment.Center)) {

                    Text(
                        trailer.name ?: "Brak nazwy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    TableRow("Wymiary", trailer.size ?: "Brak wymiarów")
                    TableRow("Przeznaczenie", trailer.purpose ?: "Brak przeznaczenia")
                    TableRow("Liczba osi", "${trailer.axles ?: "Brak informacji"}")
                    TableRowPrice("Cena", "${trailer.price ?: "Brak informacji"}")
                }

            }
        }
        Reservation()
    }
}

@Composable
fun TableRow(label: String, value: String) {
    Row {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.SemiBold
        )
        Text(text = value)
    }
}
@Composable
fun TableRowPrice(label: String, value: String) {
//    val fullPrice = String.format("%.2f zł", price)
//    val halfDayPrice = String.format("%.2f zł", price * 0.6)
    Row {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.SemiBold
        )
        Text(text = "$value zł")
    }
    Row {
        Text(
            text = "Cena za 12h: ",
            fontWeight = FontWeight.SemiBold
        )
        Text(text = "${value.toDoubleOrNull() ?: (0.0 * 0.6)} zł")
    }
}

@Composable
fun Reservation() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp).fillMaxWidth()
    ) {
        Text("Rezerwój")
    }
}
