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
package io.realm.appservicesusagesamples.presence.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.realm.appservicesusagesamples.R
import io.realm.kotlin.mongodb.sync.ConnectionState
import org.koin.compose.koinInject

@Composable
fun ControlsCard(
    state: UserListUiStatus,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(Modifier.padding(16.dp)) {
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
        }
    }
}

@Composable
fun StatusCard(
    connected: Boolean,
    message: String,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            if (connected) {
                Icon(
                    painter = painterResource(R.drawable.baseline_check_circle_24),
                    contentDescription = "Connected",
                    tint = Color(0, 200, 0)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.baseline_cancel_24),
                    contentDescription = "Disconnected",
                    tint = Color(200, 0, 0)
                )
            }
            Text(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.CenterVertically),
                text = message,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun UserStatusListScreen(
    viewModel: UserStatusListViewModel,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
) {
    val users by viewModel.userStatus.observeAsState(emptyList())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.loggedOut) {
        if (uiState.loggedOut) {
            onLogout()
        }
    }
    LaunchedEffect(key1 = users) {
        listState.scrollToItem(0)
    }
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) {
        LazyColumn(
            state = listState
        ) {
            if (uiState.connectionState == ConnectionState.CONNECTED) {
                items(
                    users,
                    key = {
                        it.id.toHexString()
                    }
                ) { userStatus ->
                    StatusCard(
                        connected = userStatus.present,
                        message = if (uiState.userId == userStatus.ownerId) {
                            "You"
                        } else {
                            "User id: ${userStatus.ownerId}"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                    )

                }
            }
            item {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(72.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ControlsCard(
                uiState,
                onLogout = { viewModel.logout() },
                onConnect = { viewModel.connect() },
                onDisconnect = { viewModel.disconnect() },
            )
        }
    }
}

@Preview
@Composable
fun ConnectedUserStatusCardPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        StatusCard(
            connected = true,
            message = "hello world"
        )
    }
}


@Preview
@Composable
fun DisconnectedUserStatusCardPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        StatusCard(
            connected = false,
            message = "hello world"
        )
    }
}

@Preview
@Composable
fun ControlsCardPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        ControlsCard(
            state = UserListUiStatus(
                loading = false
            ),
            onLogout = {},
            onDisconnect = {},
            onConnect = {},
        )
    }
}
