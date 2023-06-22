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
package io.realm.curatedsyncexamples.fieldencryption.ui.keystore

import android.view.KeyEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject

@Composable
fun UnlockUserKeyStoreScreen(
    viewModel: KeyStoreViewModel = koinInject(),
    modifier: Modifier = Modifier,
    onUnlocked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            UserKeyUnlocker(
                state = uiState,
                modifier = Modifier.padding(48.dp)
            ) { password ->
                viewModel.importRemoteKey(password)
            }
        }
        LaunchedEffect(uiState.isUnlocked) {
            if (uiState.isUnlocked) {
                onUnlocked()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun UserKeyUnlocker(
    state: KeyStoreUiState,
    modifier: Modifier = Modifier,
    onUnlock: (String) -> Unit = {}
) {
    val (passwordRef, buttonRef) = remember { FocusRequester.createRefs() }
    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var password: String by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp),
            text = if (state.isInitialized) {
                "Unlocking user's keystore"
            } else {
                "Initializing user's keystore"
            }
        )
        Text(
            textAlign = TextAlign.Justify,
            text = if (state.isInitialized) {
                """
                |The user's keystore is securely stored in Atlas and linked to your account. It can be accessed from any device. 
                |
                |Please introduced the password used to protect your user keystore.
        """.trimMargin()
            } else {
                """
                |The user's keystore is securely stored in Atlas and linked to your account. It can be accessed from any device. 
                |
                |For added security, please create a password to protect your user keystore.
        """.trimMargin()
            }
        )

        OutlinedTextField(
            value = password,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .focusProperties {
                    next = buttonRef
                }
                .onKeyEvent {
                    if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        localFocusManager.moveFocus(FocusDirection.Next)
                        true
                    } else {
                        false
                    }
                },
            enabled = !state.isUnlocking,
            isError = state.errorMessage != null,
            onValueChange = { password = it.trim() /* Do not support whitespace in password */ },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    localFocusManager.clearFocus()
                }
            ),
        )
        ElevatedButton(
            modifier = Modifier
                .padding(top = 4.dp)
                .focusRequester(buttonRef),
            enabled = !state.isUnlocking,
            onClick = { onUnlock(password) }) {
            Text(text = "Continue")
        }
        state.errorMessage?.let {
            Text(text = it)
        }

    }
}

@Preview
@Composable
fun UninitializedState() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        UserKeyUnlocker(
            state = KeyStoreUiState(
                isInitialized = false
            )
        )
    }
}

@Preview
@Composable
fun InitializedState() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        UserKeyUnlocker(
            state = KeyStoreUiState(
                isInitialized = true
            )
        )
    }
}
