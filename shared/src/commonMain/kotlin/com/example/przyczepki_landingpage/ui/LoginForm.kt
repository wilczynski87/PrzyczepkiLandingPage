package com.example.przyczepki_landingpage.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    onGoogleClick: () -> Unit = {},
    onAppleClick: () -> Unit = {}
) {
    val state by viewModel.appState.collectAsState()
    val loginState = state.loginUiState
    var passwordVisible by remember { mutableStateOf(false) }

    val canSubmit = !loginState.isLoading &&
        loginState.login.isNotBlank() &&
        loginState.password.length >= 6

    Column(
        modifier = Modifier
            .fillMaxWidth()
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
            onValueChange = viewModel::onLoginInputChange,
            label = { Text("Email lub telefon") },
            singleLine = true,
            isError = loginState.error != null,
            enabled = !loginState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // PASSWORD
        OutlinedTextField(
            value = loginState.password,
            onValueChange = viewModel::onLoginPasswordChange,
            label = { Text("Hasło") },
            singleLine = true,
            isError = loginState.error != null,
            enabled = !loginState.isLoading,
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

        // KOMUNIKAT BŁĘDU
        loginState.error?.let { error ->
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // LOGIN BUTTON
        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = canSubmit
        ) {
            if (loginState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Zaloguj")
            }
        }

        Spacer(Modifier.height(16.dp))

        DividerWithText("lub")

        Spacer(Modifier.height(16.dp))

        // GOOGLE
        OutlinedButton(
            onClick = onGoogleClick,
            enabled = !loginState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kontynuuj z Google")
        }

        Spacer(Modifier.height(8.dp))

        // APPLE (iOS / WASM Safari)
        OutlinedButton(
            onClick = onAppleClick,
            enabled = !loginState.isLoading,
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