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
package io.realm.appservicesusagesamples.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.realm.appservicesusagesamples.errorhandling.CLIENT_RESET_STRATEGY
import io.realm.appservicesusagesamples.errorhandling.ErrorHandlingActivity
import io.realm.appservicesusagesamples.errorhandling.ui.ClientResetAction

typealias EntryView = @Composable LazyItemScope.(Boolean) -> Unit


fun buttonSelector(
    title: String,
    activity: Class<*>,
): EntryView = { enabled ->
    val context = LocalContext.current
    SampleEntry(
        enabled = enabled,
        title = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    ) {
        context.startActivity(Intent(context, activity))
    }
}

val errorHandlingSelector: EntryView = { enabled ->
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    SampleEntry(
        enabled = enabled,
        title = "Client reset and error handling",
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    ) {
        expanded = true
    }
    ClientResetMenu(
        expanded = expanded,
        onOptionSelected = { action ->
            context.startActivity(
                Intent(context, ErrorHandlingActivity::class.java).apply {
                    putExtra(CLIENT_RESET_STRATEGY, action.name)
                }
            )
            expanded = false
        },
        onDismiss = { expanded = false },
    )
}

@Composable
fun ClientResetMenu(
    expanded: Boolean,
    onOptionSelected: (ClientResetAction) -> Unit,
    onDismiss: () -> Unit,
) {
    Box {
        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
            Text(
                text = "Reset strategy to test",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp),
            )
            DropdownMenuItem(
                text = { Text(text = "Recover unsynced changes") },
                onClick = { onOptionSelected(ClientResetAction.RECOVER) }
            )
            DropdownMenuItem(
                text = { Text(text = "Discard unsynced changes") },
                onClick = { onOptionSelected(ClientResetAction.DISCARD) }
            )
            DropdownMenuItem(
                text = { Text(text = "Manual recover unsynced changes") },
                onClick = { onOptionSelected(ClientResetAction.MANUAL) }
            )
            DropdownMenuItem(text = { Text(text = "Backup Realm file") },
                onClick = { onOptionSelected(ClientResetAction.BACKUP) }
            )
        }
    }
}
