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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.realm.kotlin.demo.R
import io.realm.kotlin.demo.theme.RealmColor
import io.realm.kotlin.demo.theme.MyApplicationTheme
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    ConnectionButton(vm)
                }
                Box(Modifier.fillMaxSize()) {
                    val value: String by vm.observeCounter().collectAsState(initial = "-")
                    Text(
                        text = value,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.h1,
                        fontWeight = FontWeight.Bold,
                        fontSize = 150.sp
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

    @Composable
    fun ConnectionButton(vm: AndroidCounterViewModel) {
        val wifiEnabled: Boolean by vm.observeWifiState().collectAsState()
        val data: Pair<Int, String> by remember {
            derivedStateOf {
                when (wifiEnabled) {
                    true -> Pair(R.drawable.wifi, "Wifi enabled. Click to disable.")
                    false -> Pair(R.drawable.wifi_off, "Wifi disabled. Click to enabled.")
                }
            }
        }
        Box(modifier = Modifier
            .clip(CircleShape)
            .clickable {
                if (wifiEnabled) {
                    vm.disableWifi()
                } else {
                    vm.enableWifi()
                }
            }) {
            Image(
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .padding(16.dp),
                contentScale = ContentScale.FillBounds,
                painter = painterResource(id = data.first),
                contentDescription = data.second
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        CounterApp(viewModel)
    }
}