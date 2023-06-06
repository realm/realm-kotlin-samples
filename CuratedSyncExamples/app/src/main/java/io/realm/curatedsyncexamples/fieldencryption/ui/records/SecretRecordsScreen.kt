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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.realm.curatedsyncexamples.fieldencryption.models.SecretRecord
import org.koin.compose.koinInject

@Composable
fun NewRecord(
    uiState: SecretRecordsUiState,
    onLogout: () -> Unit,
    onAddNewRecord: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.loading && !uiState.loggingOut,
            value = name,
            onValueChange = { name = it },
            label = { Text("Record content") },
        )

        Row {
            Button(
                enabled = !uiState.loading && !uiState.loggingOut,
                onClick = onLogout
            ) {
                Text(text = "Logout")
            }
            Button(
                enabled = !uiState.loading && !uiState.loggingOut,
                onClick = { onAddNewRecord(name) }) {
                Text(text = "Add")
            }
        }

    }
}

@Composable
fun SecretRecordView(
    record: SecretRecord
) {
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                fontFamily = FontFamily.Monospace,
                text = "Decrypted: ${record.content!!.value}"
            )
            Text(
                fontFamily = FontFamily.Monospace,
                text = "Encrypted: ${String(record.content!!.encryptedValue)}"
            )
        }
    }
}

@Composable
fun SecretRecordScreen(
    viewModel: SecretRecordsViewModel = koinInject(),
    onLogout: () -> Unit
) {
    val records by viewModel.records.observeAsState(emptyList())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column {
        LaunchedEffect(uiState.loggedOut) {
            if (uiState.loggedOut) {
                onLogout()
            }
        }
        NewRecord(
            uiState,
            onLogout = { viewModel.logout() }
        ) {
            viewModel.addRecord(it)
        }
        LazyColumn {
            items(
                records,
                key = {
                    it._id.toHexString()
                }
            ) { record ->
                SecretRecordView(record)
            }
        }
    }
}
