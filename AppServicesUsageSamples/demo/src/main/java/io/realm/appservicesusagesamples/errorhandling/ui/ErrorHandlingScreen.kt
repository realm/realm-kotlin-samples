package io.realm.appservicesusagesamples.errorhandling.ui

import androidx.compose.foundation.background
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.realm.appservicesusagesamples.errorhandling.models.Entry
import io.realm.kotlin.mongodb.sync.ConnectionState
import org.koin.compose.koinInject


@Composable
fun ErrorHandlingScreen(
    viewModel: ErrorHandlingViewModel = koinInject(),
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
) {
    val entries by viewModel.entries.observeAsState(emptyList())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    LaunchedEffect(uiState.loggedOut) {
        if (uiState.loggedOut) {
            onLogout()
        }
    }
    LaunchedEffect(key1 = entries) {
        listState.scrollToItem(0)
    }
    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        EntryList(
            listState = listState,
            list = entries,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ControlsCard(
                uiState,
                onAddEntry = { viewModel.addEntry() },
                onLogout = { viewModel.logout() },
                onConnect = { viewModel.connect() },
                onDisconnect = { viewModel.disconnect() },
            )
        }
    }
}

@Composable
fun EntryList(
    list: List<Entry>,
    modifier: Modifier,
    listState: LazyListState,
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
    onLogout: () -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

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
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        onClick = onAddEntry
                    ) {
                        Text(text = "Add entry")
                    }
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(),
                        enabled = !state.loading && !state.loggingOut,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        onClick = { expanded = true }
                    ) {
                        Text(text = "Client reset")
                    }
                    ClientResetMenu(
                        expanded = expanded,
                        onClientReset = { action ->

                        },
                        onDismiss = { expanded = false },
                    )
                }
                Divider(Modifier.padding(vertical = 4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(),
                        enabled = state.connectionState != ConnectionState.CONNECTING,
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
                        enabled = !state.loading && !state.loggingOut,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        onClick = onLogout
                    ) {
                        Text(text = "Logout")
                    }
                }
                if (state.errorMessage != null) {
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
                        )
                    }
                }
            }

        }
    }
}

enum class ClientResetAction {
    RECOVER, DISCARD, MANUAL, BACKUP
}

@Composable
fun ClientResetMenu(
    expanded: Boolean,
    onClientReset: (ClientResetAction) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        DropdownMenuItem(
            text = { Text(text = "Recover unsynced changed") },
            onClick = { onClientReset(ClientResetAction.RECOVER) }
        )
        DropdownMenuItem(
            text = { Text(text = "Discard unsynced changes") },
            onClick = { onClientReset(ClientResetAction.DISCARD) }
        )
        DropdownMenuItem(
            text = { Text(text = "Manual recover unsynced changed") },
            onClick = { onClientReset(ClientResetAction.MANUAL) }
        )
        DropdownMenuItem(text = { Text(text = "Backup realm") },
            onClick = { onClientReset(ClientResetAction.BACKUP) }
        )
    }
}

@Preview
@Composable
fun ClientResetMenuPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        ClientResetMenu(
            true,
            onClientReset = {},
            onDismiss = {}
        )
    }
}

@Preview
@Composable
fun EntryListPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        EntryList(List(5) { Entry() }, Modifier, rememberLazyListState())
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
            onLogout = {},
            onDisconnect = {},
            onConnect = {},
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
            onLogout = {},
            onDisconnect = {},
            onConnect = {},
        )
    }
}
