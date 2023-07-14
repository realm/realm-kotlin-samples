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
package io.realm.appservicesusagesamples.presence

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import io.realm.appservicesusagesamples.presence.ui.UserStatusListScreen
import io.realm.appservicesusagesamples.presence.ui.UserStatusListViewModel
import io.realm.appservicesusagesamples.ui.theme.AppServicesUsageSamplesTheme
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.scope.Scope

class PresenceDetectionActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: UserStatusListViewModel by viewModel()

        setContent {
            AppServicesUsageSamplesTheme {
                UserStatusListScreen(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    // close the app
                    finish()
                }
            }
        }
    }
}
