package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.data.Company
import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.Private
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.text.input.KeyboardType
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.model.CurrentScreen

@Composable
fun CustomerRegistrationForm(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
) {
    var isCompany by remember { mutableStateOf(false) }

    // PRIVATE
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var privateAddress by remember { mutableStateOf("") }
    var privateEmail by remember { mutableStateOf("") }
    var privatePhone by remember { mutableStateOf("") }
    var pesel by remember { mutableStateOf("") }

    // COMPANY
    var companyName by remember { mutableStateOf("") }
    var companyAddress by remember { mutableStateOf("") }
    var companyEmail by remember { mutableStateOf("") }
    var companyPhone by remember { mutableStateOf("") }
    var nip by remember { mutableStateOf("") }

    val canSubmit = remember(isCompany,
        firstName, lastName, privateEmail,
        companyName, companyEmail
    ) {
        if (isCompany) {
            companyName.isNotBlank() && companyEmail.isNotBlank()
        } else {
            firstName.isNotBlank() && lastName.isNotBlank() && privateEmail.isNotBlank()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NavigationBackBar()

        Text("Rejestracja klienta", style = MaterialTheme.typography.headlineSmall)

        // SWITCH
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = !isCompany,
                onClick = { isCompany = false },
                label = { Text("Osoba prywatna") }
            )
            FilterChip(
                selected = isCompany,
                onClick = { isCompany = true },
                label = { Text("Firma") }
            )
        }

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        if (!isCompany) {
            PrivateForm(
                firstName = firstName,
                lastName = lastName,
                address = privateAddress,
                email = privateEmail,
                phone = privatePhone,
                pesel = pesel,
                onFirstNameChange = { firstName = it },
                onLastNameChange = { lastName = it },
                onAddressChange = { privateAddress = it },
                onEmailChange = { privateEmail = it },
                onPhoneChange = { privatePhone = it },
                onPeselChange = { pesel = it },
            )
        } else {
            CompanyForm(
                name = companyName,
                address = companyAddress,
                email = companyEmail,
                phone = companyPhone,
                nip = nip,
                onNameChange = { companyName = it },
                onAddressChange = { companyAddress = it },
                onEmailChange = { companyEmail = it },
                onPhoneChange = { companyPhone = it },
                onNipChange = { nip = it }
            )
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                val customer = Customer(
                    private = if (!isCompany) Private(
                        firstName = firstName,
                        lastName = lastName,
                        address = privateAddress,
                        email = privateEmail,
                        phoneNumber = privatePhone,
                        pesel = pesel
                    ) else null,
                    company = if (isCompany) Company(
                        name = companyName,
                        address = companyAddress,
                        email = companyEmail,
                        phoneNumber = companyPhone,
                        nip = nip
                    ) else null
                )

                // TODO: send to backend
//                onSubmit(customer)
                viewModel.navigateTo(CurrentScreen.RESERVATION_FINALISE)
            },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zarejestruj")
        }
    }
}

@Composable
private fun PrivateForm(
    firstName: String,
    lastName: String,
    address: String,
    email: String,
    phone: String,
    pesel: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPeselChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = { Text("Imię") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = { Text("Nazwisko") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Adres") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Telefon") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pesel,
            onValueChange = onPeselChange,
            label = { Text("PESEL") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CompanyForm(
    name: String,
    address: String,
    email: String,
    phone: String,
    nip: String,
    onNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNipChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nazwa firmy") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Adres firmy") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email firmowy") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Telefon") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nip,
            onValueChange = onNipChange,
            label = { Text("NIP") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}