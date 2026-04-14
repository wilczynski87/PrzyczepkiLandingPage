package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.data.Company
import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.Private
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.KeyboardType
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.model.CurrentScreen

// TODO poprawić walidator Firmy w ten sposób że tylko nip obowiązkowy!
// dołożyć ściąganie z ceidg / krs
// dołożyć ściąganie z mObywatel
// wprowadizć dane do stanu jeśli zwalidowane poprawnie
// zapisywać dane użykwnika do sesji lokalnej / local storage

@Composable
fun CustomerRegistrationForm(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
) {
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            CustomerRegistrationCompact(viewModel, modifier)
        }

        WindowWidthSizeClass.Medium -> {
            CustomerRegistrationMedium(viewModel, modifier)
        }

        WindowWidthSizeClass.Expanded -> {
            CustomerRegistrationExpanded(viewModel, modifier)
        }
    }
}

@Composable
fun CustomerRegistrationCompact(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    CustomerRegistrationContent(
        viewModel = viewModel,
        isTwoColumn = false,
        modifier = modifier
    )
}

@Composable
fun CustomerRegistrationMedium(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    CustomerRegistrationContent(
        viewModel = viewModel,
        isTwoColumn = true,
        modifier = modifier
    )
}

@Composable
fun CustomerRegistrationExpanded(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        CustomerRegistrationContent(
            viewModel = viewModel,
            isTwoColumn = true,
            modifier = Modifier.weight(1f)
        )

        // opcjonalnie preview / info panel
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight()
        ) {
            Text(
                "Podgląd klienta",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
@Composable
private fun CustomerRegistrationContent(
    viewModel: AppViewModel,
    isTwoColumn: Boolean,
    modifier: Modifier = Modifier
        .fillMaxWidth()
) {
    val state by viewModel.appState.collectAsState()
    val customer = state.customer
    var isCompany by rememberSaveable { mutableStateOf(false) }

    val firstName = customer?.private?.firstName ?: ""
    val lastName = customer?.private?.lastName ?: ""
    val privateEmail = customer?.private?.email ?: ""
    val privateAddress = customer?.private?.address ?: ""
    val privatePhone = customer?.private?.phoneNumber ?: ""
    val pesel = customer?.private?.pesel ?: ""

    val companyName = customer?.company?.name ?: ""
    val companyEmail = customer?.company?.email ?: ""
    val companyAddress  = customer?.company?.address ?: ""
    val companyPhone  = customer?.company?.phoneNumber ?: ""
    val nip = customer?.company?.nip ?: ""

    val validationErrors = validateCustomerForm(
        isCompany = isCompany,
        firstName = firstName,
        lastName = lastName,
        privateEmail = privateEmail,
        privatePhone = privatePhone,
        pesel = pesel,
        privateAddress = privateAddress,
        companyName = companyName,
        companyEmail = companyEmail,
        companyPhone = companyPhone,
        nip = nip,
        companyAddress = companyAddress
    )

    val canSubmit = !validationErrors.hasErrors

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        NavigationBackBar ({ viewModel.navigateTo(CurrentScreen.LANDING) })

        Text("Rejestracja klienta", style = MaterialTheme.typography.headlineSmall)

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

        HorizontalDivider()

        // 🔥 RESPONSIVE BODY
        if (isTwoColumn) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                Column(Modifier.weight(1f)) {
                    if (isCompany) {
                        CompanyFormSection(
                            isCompany = true,
                            name = companyName,
                            email = companyEmail,
                            onNameChange = { value ->
                                viewModel.updateCustomer {
                                    it.copy(
                                        company = (it.company ?: Company()).copy(
                                            name = value
                                        )
                                    )
                                }
                            },
                            onEmailChange = { value ->
                                viewModel.updateCustomer {
                                    it.copy(
                                        company = (it.company ?: Company()).copy(
                                            email = value
                                        )
                                    )
                                }
                            },
                            nameError = validationErrors.companyName,
                            emailError = validationErrors.companyEmail
                        )
                    } else {
                        PrivateFormSection(
                            isCompany = false,
                            firstName = customer?.private?.firstName ?: "",
                            lastName = lastName,
                            email = privateEmail,
                            onFirstNameChange = { value ->
                                viewModel.updateCustomer {
                                    it.copy(
                                        private = (it.private ?: Private()).copy(
                                            firstName = value
                                        )
                                    )
                                }
                            },
                            onLastNameChange = { value ->
                                viewModel.updateCustomer {
                                    it.copy(
                                        private = (it.private ?: Private()).copy(
                                            lastName = value
                                        )
                                    )
                                }
                            },
                            onEmailChange = { value ->
                                viewModel.updateCustomer {
                                    it.copy(
                                        private = (it.private ?: Private()).copy(
                                            email = value
                                        )
                                    )
                                }
                            },
                            firstNameError = validationErrors.firstName,
                            lastNameError = validationErrors.lastName,
                            emailError = validationErrors.privateEmail
                        )
                    }
                }

                Column(Modifier.weight(1f)) {
                    ExtraFieldsSection(
                        isCompany = isCompany,
                        privateAddress = privateAddress,
                        privatePhone = privatePhone,
                        pesel = pesel,
                        companyAddress = companyAddress,
                        companyPhone = companyPhone,
                        nip = nip,
                        onPrivateAddressChange = { value ->
                            viewModel.updateCustomer {
                                it.copy(
                                    private = (it.private ?: Private()).copy(
                                        address = value
                                    )
                                )
                            }
                        },
                        onPrivatePhoneChange = { value ->
                            viewModel.updateCustomer {
                                it.copy(
                                    private = (it.private ?: Private()).copy(
                                        phoneNumber = value
                                    )
                                )
                            }
                        },
                        onPeselChange = { value ->
                            viewModel.updateCustomer {
                                it.copy(
                                    private = (it.private ?: Private()).copy(
                                        pesel = value
                                    )
                                )
                            }
                        },
                        onCompanyAddressChange = { value ->
                            viewModel.updateCustomer {
                                it.copy(
                                    company = (it.company ?: Company()).copy(
                                        address = value
                                    )
                                )
                            }
                        },
                        onCompanyPhoneChange = { value ->
                            viewModel.updateCustomer {
                                it.copy(
                                    company = (it.company ?: Company()).copy(
                                        phoneNumber = value
                                    )
                                )
                            }
                        },
                        onNipChange = { value ->
                            viewModel.updateCustomer {
                                it.copy(
                                    company = (it.company ?: Company()).copy(
                                        nip = value
                                    )
                                )
                            }
                        },
                        privateAddressError = validationErrors.privateAddress,
                        privatePhoneError = validationErrors.privatePhone,
                        peselError = validationErrors.pesel,
                        companyAddressError = validationErrors.companyAddress,
                        companyPhoneError = validationErrors.companyPhone,
                        nipError = validationErrors.nip
                    )
                }
            }
        } else {
            // 📱 Compact = full stack
            if (isCompany) {
                CompanyFormSection(
                    isCompany = true,
                    name = companyName,
                    email = companyEmail,
                    onNameChange = { value ->
                        viewModel.updateCustomer {
                            it.copy(
                                company = (it.company ?: Company()).copy(
                                    name = value
                                )
                            )
                        }
                    },
                    onEmailChange = { value ->
                        viewModel.updateCustomer {
                            it.copy(
                                company = (it.company ?: Company()).copy(
                                    email = value
                                )
                            )
                        }
                    },
                    nameError = validationErrors.companyName,
                    emailError = validationErrors.companyEmail
                )
            } else {
                PrivateFormSection(
                    isCompany = false,
                    firstName = customer?.private?.firstName.orEmpty(),
                    lastName = lastName,
                    email = privateEmail,
                    onFirstNameChange = { value ->
                        viewModel.updateCustomer {
                            it.copy(
                                private = (it.private ?: Private()).copy(
                                    firstName = value
                                )
                            )
                        }
                    },
                    onLastNameChange = { value ->
                        viewModel.updateCustomer {
                            it.copy(
                                private = (it.private ?: Private()).copy(
                                    lastName = value
                                )
                            )
                        }
                    },
                    onEmailChange = { value ->
                        viewModel.updateCustomer {
                            it.copy(
                                private = (it.private ?: Private()).copy(
                                    email = value
                                )
                            )
                        }
                    },
                    firstNameError = validationErrors.firstName,
                    lastNameError = validationErrors.lastName,
                    emailError = validationErrors.privateEmail
                )
            }

            ExtraFieldsSection(
                isCompany = isCompany,
                privateAddress = privateAddress,
                privatePhone = privatePhone,
                pesel = pesel,
                companyAddress = companyAddress,
                companyPhone = companyPhone,
                nip = nip,
                onPrivateAddressChange = { value ->
                    viewModel.updateCustomer {
                        it.copy(
                            private = (it.private ?: Private()).copy(
                                address = value
                            )
                        )
                    }
                },
                onPrivatePhoneChange = { value ->
                    viewModel.updateCustomer {
                        it.copy(
                            private = (it.private ?: Private()).copy(
                                phoneNumber = value
                            )
                        )
                    }
                },
                onPeselChange = { value ->
                    viewModel.updateCustomer {
                        it.copy(
                            private = (it.private ?: Private()).copy(
                                pesel = value
                            )
                        )
                    }
                },
                onCompanyAddressChange = { value ->
                    viewModel.updateCustomer {
                        it.copy(
                            company = (it.company ?: Company()).copy(
                                address = value
                            )
                        )
                    }
                },
                onCompanyPhoneChange = { value ->
                    viewModel.updateCustomer {
                        it.copy(
                            company = (it.company ?: Company()).copy(
                                phoneNumber = value
                            )
                        )
                    }
                },
                onNipChange = { value ->
                    viewModel.updateCustomer {
                        it.copy(
                            company = (it.company ?: Company()).copy(
                                nip = value
                            )
                        )
                    }
                },
                privateAddressError = validationErrors.privateAddress,
                privatePhoneError = validationErrors.privatePhone,
                peselError = validationErrors.pesel,
                companyAddressError = validationErrors.companyAddress,
                companyPhoneError = validationErrors.companyPhone,
                nipError = validationErrors.nip
            )
        }

        Button(
            onClick = {
                // TODO wysłanie danych do serwera w celu walidacji
                // jeśli ok to rejestracja klienta -> email z potwierdzeniem
                viewModel.saveCustomer()
            },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zarejestruj")
        }
    }
}

@Composable
fun PrivateFormSection(
    isCompany: Boolean,
    firstName: String,
    lastName: String,
    email: String,
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    firstNameError: String? = null,
    lastNameError: String? = null,
    emailError: String? = null,
) {
    if (isCompany) return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = { Text("Imię") },
            isError = firstNameError != null,
            supportingText = {
                firstNameError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = { Text("Nazwisko") },
            isError = lastNameError != null,
            supportingText = {
                lastNameError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null,
            supportingText = {
                emailError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CompanyFormSection(
    isCompany: Boolean,
    name: String,
    email: String,
    onNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    nameError: String? = null,
    emailError: String? = null,
) {
    if (!isCompany) return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nazwa firmy") },
            isError = nameError != null,
            supportingText = {
                nameError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email firmowy") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null,
            supportingText = {
                emailError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ExtraFieldsSection(
    isCompany: Boolean,
    privateAddress: String,
    privatePhone: String,
    pesel: String,
    companyAddress: String,
    companyPhone: String,
    nip: String,
    onPrivateAddressChange: (String) -> Unit = {},
    onPrivatePhoneChange: (String) -> Unit = {},
    onPeselChange: (String) -> Unit = {},
    onCompanyAddressChange: (String) -> Unit = {},
    onCompanyPhoneChange: (String) -> Unit = {},
    onNipChange: (String) -> Unit = {},
    privateAddressError: String? = null,
    privatePhoneError: String? = null,
    peselError: String? = null,
    companyAddressError: String? = null,
    companyPhoneError: String? = null,
    nipError: String? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Adres
        OutlinedTextField(
            value = if (isCompany) companyAddress else privateAddress,
            onValueChange = {
                if (isCompany) onCompanyAddressChange(it)
                else onPrivateAddressChange(it)
            },
            label = { Text("Adres") },
            isError = if (isCompany) companyAddressError != null else privateAddressError != null,
            supportingText = {
                val error = if (isCompany) companyAddressError else privateAddressError
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Telefon
        OutlinedTextField(
            value = if (isCompany) companyPhone else privatePhone,
            onValueChange = {
                if (isCompany) onCompanyPhoneChange(it)
                else onPrivatePhoneChange(it)
            },
            label = { Text("Telefon") },
            isError = if (isCompany) companyPhoneError != null else privatePhoneError != null,
            supportingText = {
                val error = if (isCompany) companyPhoneError else privatePhoneError
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // PESEL lub NIP
        if (!isCompany) {
            OutlinedTextField(
                value = pesel,
                onValueChange = onPeselChange,
                label = { Text("PESEL / NR PASZPORTU") },
                isError = peselError != null,
                supportingText = {
                    peselError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedTextField(
                value = nip,
                onValueChange = onNipChange,
                label = { Text("NIP") },
                isError = nipError != null,
                supportingText = {
                    nipError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


// Klasa dla błędów walidacji
data class ValidationErrors(
    val firstName: String? = null,
    val lastName: String? = null,
    val privateEmail: String? = null,
    val privatePhone: String? = null,
    val pesel: String? = null,
    val privateAddress: String? = null,
    val companyName: String? = null,
    val companyEmail: String? = null,
    val companyPhone: String? = null,
    val nip: String? = null,
    val companyAddress: String? = null
) {
    val hasErrors: Boolean
        get() = listOf(
            firstName, lastName, privateEmail, privatePhone, pesel, privateAddress,
            companyName, companyEmail, companyPhone, nip, companyAddress
        ).any { it != null }
}

// Funkcje walidacyjne
private val EMAIL_REGEX = Regex(
    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
)

fun validateEmail(email: String): String? {
    return when {
        email.isBlank() -> "Email jest wymagany"
        !EMAIL_REGEX.matches(email) -> "Nieprawidłowy format email"
        else -> null
    }
}

fun validatePhone(phone: String): String? {
    return when {
        phone.isBlank() -> null // Telefon może być opcjonalny
        !phone.matches(Regex("^[+]?[0-9\\s-]{9,15}$")) -> "Nieprawidłowy numer telefonu"
        else -> null
    }
}

fun validatePesel(pesel: String): String? {
    return when {
        pesel.isBlank() -> "PESEL jest wymagany"
        !pesel.matches(Regex("^\\d{6,11}$")) -> "nieprawidłowy PESEL lub numer paszportu"
//        !isValidPeselChecksum(pesel) -> "Nieprawidłowa suma kontrolna PESEL"
        else -> null
    }
}

fun validateNip(nip: String): String? {
    return when {
        nip.isBlank() -> "NIP jest wymagany"
        !nip.matches(Regex("^\\d{10}$")) -> "NIP musi zawierać 10 cyfr"
        !isValidNipChecksum(nip) -> "Nieprawidłowa suma kontrolna NIP"
        else -> null
    }
}

fun validateRequired(value: String, fieldName: String): String? {
    return if (value.isBlank()) "$fieldName jest wymagany" else null
}

// Funkcje sum kontrolnych
private fun isValidPeselChecksum(pesel: String): Boolean {
    val weights = listOf(1, 3, 7, 9, 1, 3, 7, 9, 1, 3)
    val sum = pesel.take(10).mapIndexed { index, c ->
        c.toString().toInt() * weights[index]
    }.sum()
    val checksum = (10 - (sum % 10)) % 10
    return checksum == pesel.last().toString().toInt()
}

private fun isValidNipChecksum(nip: String): Boolean {
    val weights = listOf(6, 5, 7, 2, 3, 4, 5, 6, 7)
    val sum = nip.take(9).mapIndexed { index, c ->
        c.toString().toInt() * weights[index]
    }.sum()
    val checksum = sum % 11
    return checksum == nip.last().toString().toInt()
}

// Główna funkcja walidacji
fun validateCustomerForm(
    isCompany: Boolean,
    firstName: String,
    lastName: String,
    privateEmail: String,
    privatePhone: String,
    pesel: String,
    privateAddress: String,
    companyName: String,
    companyEmail: String,
    companyPhone: String,
    nip: String,
    companyAddress: String
): ValidationErrors {

    return if (isCompany) {
        ValidationErrors(
            companyName = validateRequired(companyName, "Nazwa firmy"),
            companyEmail = validateEmail(companyEmail),
            companyPhone = validatePhone(companyPhone),
            nip = validateNip(nip),
            companyAddress = validateRequired(companyAddress, "Adres firmy")
        )
    } else {
        ValidationErrors(
            firstName = validateRequired(firstName, "Imię"),
            lastName = validateRequired(lastName, "Nazwisko"),
            privateEmail = validateEmail(privateEmail),
            privatePhone = validatePhone(privatePhone),
            pesel = validatePesel(pesel),
            privateAddress = validateRequired(privateAddress, "Adres")
        )
    }
}