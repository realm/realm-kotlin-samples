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
package io.realm.appservicesusagesamples.errorhandling.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.realm.appservicesusagesamples.errorhandling.models.Entry
import io.realm.kotlin.mongodb.sync.ConnectionState


@Composable
fun ErrorHandlingScreen(
    viewModel: ErrorHandlingViewModel,
    onRestart: () -> Unit,
) {
    val context = LocalContext.current
    val entries by viewModel.entries.observeAsState(emptyList())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = entries) {
        listState.scrollToItem(0)
    }
    LaunchedEffect(key1 = uiState) {
        if (uiState.restart) {
            Toast.makeText(context, uiState.errorMessage!!, Toast.LENGTH_LONG).show()
            onRestart()
        }
    }
    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        if (uiState.loading) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Loading...",
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        } else {
            EntryList(
                list = entries,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                listState = listState,
                uiState = uiState,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            ControlsCard(
                uiState,
                onAddEntry = viewModel::addEntry,
                onCompensatingWrite = viewModel::triggerCompensatingWriteError,
                onConnect = viewModel::connect,
                onClientReset = viewModel::triggerClientReset,
                onDisconnect = viewModel::disconnect,
            )
        }
    }
}

@Composable
fun EntryList(
    list: List<Entry>,
    modifier: Modifier,
    listState: LazyListState,
    uiState: ErrorHandlingUIStatus,
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) {
        LazyColumn(
            state = listState,
            content = {
                items(items = list) {
                    EntryCard(
                        message = it.id.toHexString(),
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth()
                    )
                }
                item {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .height(120.dp)
                    )
                }
            }
        )
    }

}

@Composable
fun EntryCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = message,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlsCard(
    state: ErrorHandlingUIStatus,
    modifier: Modifier = Modifier,
    onAddEntry: () -> Unit,
    onCompensatingWrite: () -> Unit,
    onClientReset: () -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    var enabled by remember { mutableStateOf(false) }
    enabled = state.errorMessage != null

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(Modifier.padding(16.dp)) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(),
                        enabled = !state.loading,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        onClick = onAddEntry
                    ) {
                        Text(text = "Add entry")
                    }
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(),
                        enabled = !state.loading && state.connectionState == ConnectionState.DISCONNECTED,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        onClick = onClientReset
                    ) {
                        Text(text = "Client reset")
                    }
                }
                Divider(Modifier.padding(vertical = 4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(),
                        enabled = !state.loading && state.connectionState != ConnectionState.CONNECTING,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        onClick = {
                            if (state.connectionState == ConnectionState.DISCONNECTED) onConnect()
                            else if (state.connectionState == ConnectionState.CONNECTED) onDisconnect()
                        }
                    ) {
                        Text(
                            text = when (state.connectionState) {
                                ConnectionState.DISCONNECTED -> "Connect"
                                ConnectionState.CONNECTING -> "Connecting..."
                                ConnectionState.CONNECTED -> "Disconnect"
                            }
                        )
                    }
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(),
                        enabled = !state.loading,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        onClick = onCompensatingWrite
                    ) {
                        Text(text = "Comp. write")
                    }
                }

                if (enabled) {
                    Box(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Sync Error: ${state.errorMessage}",
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Red)
                                .padding(8.dp)
                                .clickable {
                                    enabled = false
                                },
                        )
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun EntryListPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        EntryList(
            List(5) { Entry() },
            Modifier,
            rememberLazyListState(),
            ErrorHandlingUIStatus(
                connectionState = ConnectionState.CONNECTED,
            )
        )
    }
}

@Preview
@Composable
fun EntryPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        EntryCard(message = "hello world")
    }
}

@Preview
@Composable
fun ControlsCardPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        ControlsCard(
            state = ErrorHandlingUIStatus(
                loading = false
            ),
            onAddEntry = {},
            onCompensatingWrite = {},
            onDisconnect = {},
            onConnect = {},
            onClientReset = {},
        )
    }
}

@Preview
@Composable
fun ControlsCardWithErrorPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        ControlsCard(
            state = ErrorHandlingUIStatus(
                loading = false,
                errorMessage = "Need more ips"
            ),
            onAddEntry = {},
            onCompensatingWrite = {},
            onDisconnect = {},
            onConnect = {},
            onClientReset = {},
        )
    }
}
