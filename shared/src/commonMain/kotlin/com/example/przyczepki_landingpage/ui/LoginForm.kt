package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.model.LoginUiState

@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    onLoginChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onAppleClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var loginState by remember { mutableStateOf(LoginUiState()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "Zaloguj się",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        // EMAIL / PHONE
        OutlinedTextField(
            value = loginState.login,
            onValueChange = { loginState = loginState.copy(login = it) },
            label = { Text("Email lub telefon") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // PASSWORD
        OutlinedTextField(
            value = loginState.password,
            onValueChange = { loginState = loginState.copy(password = it) },
            label = { Text("Hasło") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        loginState.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(16.dp))

        // LOGIN BUTTON
        Button(
            onClick = {
                loginState = loginState.copy(error = null, isLoading = true)
                viewModel.login(LoginRequest(loginState.login, loginState.password))
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = loginState.login.isNotBlank() && loginState.password.length >= 6
        ) {
            Text("Zaloguj")
        }

        Spacer(Modifier.height(16.dp))

        DividerWithText("lub")

        Spacer(Modifier.height(16.dp))

        // GOOGLE
        OutlinedButton(
            onClick = onGoogleClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kontynuuj z Google")
        }

        Spacer(Modifier.height(8.dp))

        // APPLE (iOS / WASM Safari)
        OutlinedButton(
            onClick = onAppleClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kontynuuj z Apple")
        }
    }
}

@Composable
fun DividerWithText(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.labelMedium
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )
    }
}

fun isValidLogin(input: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)$")
    val phoneRegex = Regex("^\\+?[0-9]{7,15}$")

    return emailRegex.matches(input) || phoneRegex.matches(input)
}