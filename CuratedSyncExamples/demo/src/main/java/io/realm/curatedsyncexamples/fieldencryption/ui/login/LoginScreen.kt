/*
 * Copyright 2023 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.realm.curatedsyncexamples.fieldencryption.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinInject(),
    modifier: Modifier = Modifier,
    onLoggedIn: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        LaunchedEffect(uiState.loggedIn) {
            if (uiState.loggedIn) {
                onLoggedIn()
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            LoginBox(
                state = uiState,
                modifier = Modifier.padding(48.dp)
            ) { email, password, register ->
                viewModel.login(email, password, register)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginBox(
    state: LoginUiState,
    modifier: Modifier = Modifier,
    onLogin: (String, String, Boolean) -> Unit = { _, _, _ -> }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            text = "Field level encryption",
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
        Text(
            textAlign = TextAlign.Justify,
            text =
            """
|This demo shows the process of encrypting specific fields within an object, limiting their accessibility to the user alone and preventing access on the server side.
|
|The process involves importing the required keys from the Atlas keystore to the secure keystore on the device. These imported keys are then utilized to access any of the encrypted fields.
        """.trimMargin()
        )

        OutlinedTextField(
            modifier = Modifier.padding(top = 16.dp),
            value = email,
            isError = state.errorMessage != null,
            enabled = !state.loggingIn,
            onValueChange = { email = it },
            placeholder = { Text("your@email.com") },
            label = { Text("Email") },
        )
        OutlinedTextField(
            modifier = Modifier.padding(top = 4.dp),
            value = password,
            isError = state.errorMessage != null,
            enabled = !state.loggingIn,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            ElevatedButton(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = { onLogin(email, password, true) },
                enabled = !state.loggingIn
            ) {
                Text(text = "Register")
            }
            ElevatedButton(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = { onLogin(email, password, false) },
                enabled = !state.loggingIn
            ) {
                Text(text = "Login")
            }
        }

        state.errorMessage?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 8.dp),
                color = Color.Red,
            )
        }
    }
}

@Preview
@Composable
fun InitialStatePreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        LoginBox(state = LoginUiState())
    }
}

@Preview
@Composable
fun LoggingInPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        LoginBox(state = LoginUiState(loggingIn = true))
    }
}

@Preview
@Composable
fun LoginErrorPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        LoginBox(state = LoginUiState(errorMessage = "An error occurred"))
    }
}
