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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import io.realm.appservicesusagesamples.R

/**
 * View that displays the available and unavailable samples.
 */
@Composable
fun SampleSelectorScreen(
    viewModel: SampleSelectorScreenViewModel
) {
    val sampleEntriesWithStatus by viewModel.sampleEntriesWithStatus.observeAsState(emptyList())
    val loading by viewModel.loadingState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        if (!loading) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 48.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(top = 48.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.realmio_logo_vector),
                            contentDescription = "Realm logo",
                        )
                        Text(
                            text = "Reference app that showcases different design patterns and samples of Realm Kotlin SDK with Atlas.",
                            modifier = Modifier.padding(top = 48.dp),
                            textAlign = TextAlign.Justify
                        )
                    }
                }
                items(
                    items = sampleEntriesWithStatus.filter { it.second }.map { it.first }
                ) { sample ->
                    SampleEntry(
                        title = sample.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                    ) {
                        context.startActivity(Intent(context, sample.activity))
                    }
                }
                if (sampleEntriesWithStatus.any { !it.second }) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp, bottom = 24.dp)
                        ) {
                            Text(
                                text = "⚠️ The following samples are not available. Please follow the Readme instructions to set them up.",
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                    items(
                        items = sampleEntriesWithStatus.filter { !it.second }.map { it.first }
                    ) { sample ->
                        SampleEntry(
                            title = sample.title,
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                        ) {
                            context.startActivity(Intent(context, sample.activity))
                        }
                    }
                }
            }
        }
    }
}

/**
 * View that shows the details of a specific sample.
 */
@Composable
fun SampleEntry(
    title: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = title
        )
    }
}
