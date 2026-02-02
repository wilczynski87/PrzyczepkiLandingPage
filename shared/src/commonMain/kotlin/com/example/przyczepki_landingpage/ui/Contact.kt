package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.CompanyMap
import com.example.przyczepki_landingpage.data.latitude
import com.example.przyczepki_landingpage.data.longitude

@Composable
fun ContactPage(
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Nagłówek
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Zapraszamy do kontaktu:",
                style = MaterialTheme.typography.headlineLarge.copy(
                    shadow = Shadow(
                        color = Color.White,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Jesteśmy otwarci 24 h /7 dni - przyczepka można wypożyczyć lub zwrócić w każdej porze!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    shadow = Shadow(
                        color = Color.White,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        HorizontalDivider()

        // Kontakt
        ContactSection(widthSizeClass)

        HorizontalDivider()

        // Lokalizacja
        LocationSection()

        HorizontalDivider()

        // Informacje o firmie
        CompanyInfoSection()

        HorizontalDivider()

        // Szybki kontakt i 24/7
        QuickContactSection()
    }
}

@Composable
fun ContactSection(widthSizeClass: WindowWidthSizeClass) {
    when(widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContactItem(
                    icon = Icons.Default.Phone,
                    title = "Telefon:",
                    details = listOf("+48 727 188 330", ""),
                    modifier = Modifier.fillMaxWidth()
                )
                ContactItem(
                    icon = Icons.Default.Email,
                    title = "Em@il",
                    details = listOf("parkingOstrowskiego@gmail.com", ""),
                    modifier = Modifier.fillMaxWidth()
                )
                ContactItem(
                    icon = Icons.Default.LocationOn,
                    title = "Adres:",
                    details = listOf("ul. Aleksandra Ostrowskiego 102", "Wrocław, 53-238"),
                    modifier = Modifier.fillMaxWidth()
                )
                ContactItem(
                    icon = Icons.Default.AccessTime,
                    title = "Godziny otwarcia:",
                    details = listOf("Biuro: 7:00 - 16:00", "Wypożyczenie i zwrot: 24/7"),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        else -> {
            Row(
                modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContactItem(
                    icon = Icons.Default.Phone,
                    title = "Telefon:",
                    details = listOf("+48 727 188 330", "")
                )
                ContactItem(
                    icon = Icons.Default.Email,
                    title = "Em@il",
                    details = listOf("parkingOstrowskiego@gmail.com", "")
                )
                ContactItem(
                    icon = Icons.Default.LocationOn,
                    title = "Adres:",
                    details = listOf("ul. Aleksandra Ostrowskiego 102", "Wrocław, 53-238")
                )
                ContactItem(
                    icon = Icons.Default.AccessTime,
                    title = "Godziny otwarcia:",
                    details = listOf("Biuro: 7:00 - 16:00", "Wypożyczenie i zwrot: 24/7")
                )
            }
        }

    }
}

@Composable
fun ContactItem(
    icon: ImageVector?,
    title: String,
    details: List<String>,
    modifier: Modifier = Modifier,
) {
    Card (
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = CardDefaults.outlinedCardBorder()

    ) {
        Column(modifier = Modifier.padding(12.dp)) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(top = 4.dp, end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        } ?: Spacer(modifier = Modifier.width(40.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            details.forEach { detail ->
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        }
    }
}

@Composable
fun LocationSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Nasza Siedziba:",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "ul. Aleksandra Ostrowskiego 102, 53-238 Wrocław",
            style = MaterialTheme.typography.bodyMedium
        )
        // Placeholder mapy
        Box(
            modifier = Modifier
                .sizeIn(maxWidth = 600.dp, maxHeight = 400.dp)
                .padding(top = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(Color.Transparent)
            ) {
                CompanyMap(latitude, longitude, "google")
            }
        }
    }
}

@Composable
fun CompanyInfoSection() {
    Column(
        modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dane firmy:",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "O nas:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Wypożyczamy przyczepki lekkie, do 750kg DMC. Zapraszamy do kontaktu i wypożyczania!",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun QuickContactSection() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Quick Contact
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Szybki kontakt:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Główny: +48 727 188 330",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Awaryjny: +48 507 036 484",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "parkingostrowskiego@gmail.com",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Follow Us
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Socjal media:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            // Tutaj możesz dodać ikony społecznościowe
            Text(
                text = "[Social media icons placeholder]",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 24/7 Support
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Potrzebujesz pomocy?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "W sytuacjach nagłych jesteśmy dostępni 24/7",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { /* Call action */ },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Zadzwoń: +48 727 188 330")
                }
            }
        }
    }
}


