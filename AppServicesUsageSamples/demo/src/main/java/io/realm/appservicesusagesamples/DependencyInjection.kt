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
import io.realm.kotlin.mongodb.App
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Enum that lists all the available demos.
 */
enum class Demos(
    val title: String,
    val activity: Class<*>,
    val appId: String,
) {
    PROPERTY_ENCRYPTION(
        "Property level encryption",
        PropertyEncryptionActivity::class.java,
        PROPERTY_ENCRYPTION_APP_ID
    ),
    USER_PRESENCE(
        "User presence",
        SampleSelectorActivity::class.java,
        USER_PRESENCE_APP_ID,
    ),
    OFFLINE_LOGIN(
        "Offline login",
        SampleSelectorActivity::class.java,
        OFFLINE_LOGIN_APP_ID,
    ),
    ERROR_HANDLING(
        "Error handling",
        SampleSelectorActivity::class.java,
        ERROR_HANDLING_APP_ID,
    ),
    BUSINESS_LOGIC(
        "Business logic",
        SampleSelectorActivity::class.java,
        BUSINESS_LOGIC_APP_ID,
    ),
    PURCHASE_VERIFICATION(
        "Purchase verification",
        SampleSelectorActivity::class.java,
        PURCHASE_VERIFICATION_APP_ID,
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
