package io.realm.appservicesusagesamples.errorhandling.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.realm.appservicesusagesamples.presence.ui.UserListUiStatus
import io.realm.kotlin.mongodb.sync.ConnectionState
import io.realm.kotlin.types.RealmUUID

data class ErrorHandlingUIStatus(
    val loading: Boolean = true,
    val loggingOut: Boolean = false,
    val loggedOut: Boolean = false,
    val errorMessage: String? = null,
    val connectionState: ConnectionState = ConnectionState.CONNECTING,
)

@Composable
fun ErrorHandlingScreen(
    list: List<String>,
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        EntryList(
            list = list,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ControlsCard(
                ErrorHandlingUIStatus(),
                onLogout = { },
                onConnect = { },
                onDisconnect = { },
            )
        }
    }
}

@Composable
fun EntryList(
    list: List<String>,
    modifier: Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) {
        LazyColumn(
            content = {
                items(items = list) {
                    EntryCard(
                        message = it,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth()
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

@Composable
fun ControlsCard(
    state: ErrorHandlingUIStatus,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
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
                        onClick = {}
                    ) {
                        Text(text = "Add entry")
                    }
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(),
                        enabled = !state.loading && !state.loggingOut,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        onClick = onLogout
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


@Preview
@Composable
fun EntryListPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        EntryList(List(5) { RealmUUID.random().toString() }, Modifier)
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
            onLogout = {},
            onDisconnect = {},
            onConnect = {},
        )
    }
}

@Preview
@Composable
fun ErrorHandlingScreenPreview() {
    ErrorHandlingScreen(List(5) { RealmUUID.random().toString() })
}
