/*
 * Copyright 2021 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.realm.kotlin.demo.ui.counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import io.realm.kotlin.demo.theme.RealmColor
import io.realm.kotlin.demo.theme.MyApplicationTheme

class CounterActivity : ComponentActivity() {

    private val viewModel = AndroidCounterViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Realm Kotlin Demo - ${viewModel.platform}"
        setContent {
            CounterApp(viewModel)
        }
    }

    @Composable
    fun CounterApp(vm: AndroidCounterViewModel) {
        MyApplicationTheme {
            Surface(color = RealmColor.SexySalmon) {
                Column {
                    CounterButton(modifier = Modifier.weight(1F)) {
                        vm.increment()
                    }
                    CounterButton(modifier = Modifier.weight(1F)) {
                        vm.decrement()
                    }
                }
                Box(Modifier.fillMaxSize()) {
                    val value: String by vm.observeCounter().collectAsState(initial = "-")
                    Text(
                        text = value,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.h1,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    @Composable
    fun CounterButton(modifier: Modifier = Modifier, action: () -> Unit) {
        Box(modifier = modifier
            .fillMaxWidth()
            .clickable {
                action()
            }
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        CounterApp(viewModel)
    }
}