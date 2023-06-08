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
package io.realm.curatedsyncexamples.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.realm.curatedsyncexamples.R
import org.koin.compose.koinInject

@Composable
fun ExamplesScreen(
    viewModel: ExamplesScreenViewModel = koinInject()
) {
    val unavailableApps by viewModel.unavailableApps.observeAsState(emptyList())
    val loading by viewModel.loadingState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(48.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.realmio_logo_vector),
                contentDescription = "Realm logo",
            )
            Text(
                text = "Reference app that showcases different design patterns and examples of Realm Kotlin SDK with Atlas",
                modifier = Modifier.padding(vertical = 48.dp),
                textAlign = TextAlign.Center
            )
            if (!loading) {
                if (unavailableApps.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "⚠️",
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Some App Services Apps are not available. Please enable then using the Readme instructions.",
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    viewModel.examplesList.forEach { example ->
                        ExampleEntry(example.name) {
                            context.startActivity(Intent(context, example.activity))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExampleEntry(name: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = name,
            modifier = modifier
        )
    }
}
