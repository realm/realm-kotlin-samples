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
package io.realm.appservicesusagesamples.propertyencryption.ui.records

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import io.realm.appservicesusagesamples.propertyencryption.models.SecretRecord
import org.koin.compose.koinInject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * This screen would display a list of [SecretRecord] and a control view to add new entries. For each
 * entry it would show its encrypted content in hexadecimal and the uncrypted value.
 */
@OptIn(ExperimentalEncodingApi::class)
@Composable
fun SecretRecordScreen(
    viewModel: SecretRecordsViewModel = koinInject(),
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    val records by viewModel.records.observeAsState(emptyList())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.loggedOut) {
        if (uiState.loggedOut) {
            onLogout()
        }
    }
    LaunchedEffect(key1 = records) {
        listState.scrollToItem(0)
    }
    LazyColumn(
        state = listState
    ) {
        items(
            records,
            key = {
                it._id.toHexString()
            }
        ) { record ->
            SecretRecordCard(
                content = record.content,
                encryptedContent = Base64.encode(record.securedContent),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
            )

        }
        item {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .height(144.dp)
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AddSecretRecordCard(
            uiState,
            onLogout = { viewModel.logout() }
        ) {
            viewModel.addRecord(it)
        }
    }
}


/**
 * View with controls to add new entries or logout.
 */
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
                        modifier = Modifier.weight(1f),
                        enabled = !state.loading && !state.loggingOut,
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Secret record value") },
                    )
                    IconButton(
                        enabled = !state.loading && !state.loggingOut,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp),
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
                        .padding(top = 8.dp),
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
