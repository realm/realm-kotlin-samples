package io.realm.curatedsyncexamples.fieldencryption.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinInject(),
    onLoggedIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        color = MaterialTheme.colorScheme.background
    ) {
        LaunchedEffect(uiState.loggedIn) {
            if (uiState.loggedIn) {
                onLoggedIn()
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(48.dp)
        ) {
            Text(text = "Field level encryption Demo")

            OutlinedTextField(
                value = email,
                isError = uiState.errorMessage != null,
                enabled = !uiState.loggingIn,
                onValueChange = { email = it },
                placeholder = { Text("your@email.com") },
                label = { Text("Email") },
            )
            OutlinedTextField(
                value = password,
                isError = uiState.errorMessage != null,
                enabled = !uiState.loggingIn,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Row {
                ElevatedButton(
                    onClick = { viewModel.login(email, password, true) },
                    enabled = !uiState.loggingIn
                ) {
                    Text(text = "Register")
                }
                ElevatedButton(
                    onClick = { viewModel.login(email, password) },
                    enabled = !uiState.loggingIn
                ) {
                    Text(text = "Login")
                }
            }

            uiState.errorMessage?.let {
                Text(text = it)
            }
        }
    }
}