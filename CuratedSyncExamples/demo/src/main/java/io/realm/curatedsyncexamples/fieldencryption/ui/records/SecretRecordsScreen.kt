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
package io.realm.curatedsyncexamples.fieldencryption.ui.records

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSecretRecordCard(
    state: SecretRecordsUiState,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onAddNewRecord: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(Modifier.padding(16.dp)) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        enabled = !state.loading && !state.loggingOut,
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Secret record value") },
                    )
                    IconButton(
                        enabled = !state.loading && !state.loggingOut,
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                        onClick = { onAddNewRecord(name) }) {
                        Icon(
                            Icons.Filled.Add,
                            "contentDescription",
                        )
                    }
                }
                ElevatedButton(
                    colors = ButtonDefaults.buttonColors(),
                    enabled = !state.loading && !state.loggingOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    onClick = onLogout
                ) {
                    Text(text = "Logout")
                }
            }
        }

    }
}

@Composable
fun SecretRecordCard(
    content: String,
    encryptedContent: String,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Decrypted: $content"
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "Encrypted: $encryptedContent",
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun SecretRecordScreen(
    viewModel: SecretRecordsViewModel = koinInject(),
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    val records by viewModel.records.observeAsState(emptyList())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
    ) {
        LaunchedEffect(uiState.loggedOut) {
            if (uiState.loggedOut) {
                onLogout()
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(
                records,
                key = {
                    it._id.toHexString()
                }
            ) { record ->
                with(record.content!!) {
                    SecretRecordCard(
                        content = value,
                        encryptedContent = Base64.encode(encryptedValue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    )
                }
            }
        }
        AddSecretRecordCard(
            uiState,
            modifier = Modifier.padding(bottom = 16.dp),
            onLogout = { viewModel.logout() }
        ) {
            viewModel.addRecord(it)
        }
    }
}

@Preview
@Composable
fun SecretRecordCardPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        SecretRecordCard(
            content = "hello world",
            encryptedContent = "ab88aa34341341231234123415673294572938475def="
        )
    }
}

@Preview
@Composable
fun AddSecretRecordCardPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        AddSecretRecordCard(
            state = SecretRecordsUiState(
                loading = false
            ),
            onLogout = {},
            onAddNewRecord = { _ -> }
        )
    }
}
