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
package io.realm.appservicesusagesamples

import io.realm.appservicesusagesamples.propertyencryption.PropertyEncryptionActivity
import io.realm.appservicesusagesamples.ui.SampleSelectorScreenViewModel
import io.realm.appservicesusagesamples.presence.PresenceDetectionActivity
import io.realm.appservicesusagesamples.ui.EntryView
import io.realm.appservicesusagesamples.ui.buttonSelector
import io.realm.appservicesusagesamples.ui.errorHandlingSelector
import io.realm.kotlin.mongodb.App
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


/**
 * Enum that lists all the available demos.
 */
enum class Demos(
    val appId: String,
    val addView: EntryView,
) {
    PROPERTY_ENCRYPTION(
        appId = PROPERTY_ENCRYPTION_APP_ID,
        addView = buttonSelector(
            "Property level encryption",
            PropertyEncryptionActivity::class.java,
        )
    ),
    USER_PRESENCE(
        appId = USER_PRESENCE_APP_ID,
        addView = buttonSelector(
            "User presence",
            PresenceDetectionActivity::class.java,
        )
    ),
    ERROR_HANDLING(
        appId = ERROR_HANDLING_APP_ID,
        addView = errorHandlingSelector,
    );

    val qualifier = named(appId)
}

typealias DemoWithApp = Pair<Demos, App>

/**
 * Koin module for the main entry point.
 */
val mainModule = module {
    // Create singletons for each app.
    for (app in Demos.values()) {
        single(app.qualifier) { App.create(app.appId) }
    }

    scope<SampleSelectorActivity> {
        viewModel {
            SampleSelectorScreenViewModel(
                apps = Demos.values()
                    .map { demo ->
                        DemoWithApp(
                            first = demo,
                            second = get(demo.qualifier)
                        )
                    }
            )
        }
    }
}
