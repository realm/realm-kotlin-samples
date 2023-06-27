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

import io.realm.appservicesusagesamples.fieldencryption.FieldEncryptionActivity
import io.realm.appservicesusagesamples.ui.ExamplesScreenViewModel
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
    FIELD_ENCRYPTION(
        "Field level encryption",
        FieldEncryptionActivity::class.java,
        io.realm.appservicesusagesamples.FIELD_ENCRYPTION_APP_ID
    ),
    USER_PRESENCE(
        "User presence",
        io.realm.appservicesusagesamples.DemoSelectorActivity::class.java,
        io.realm.appservicesusagesamples.USER_PRESENCE_APP_ID,
    ),
    OFFLINE_LOGIN(
        "Offline login",
        io.realm.appservicesusagesamples.DemoSelectorActivity::class.java,
        io.realm.appservicesusagesamples.OFFLINE_LOGIN_APP_ID,
    ),
    ERROR_HANDLING(
        "Error handling",
        io.realm.appservicesusagesamples.DemoSelectorActivity::class.java,
        io.realm.appservicesusagesamples.ERROR_HANDLING_APP_ID,
    ),
    BUSINESS_LOGIC(
        "Business logic",
        io.realm.appservicesusagesamples.DemoSelectorActivity::class.java,
        io.realm.appservicesusagesamples.BUSINESS_LOGIC_APP_ID,
    ),
    PURCHASE_VERIFICATION(
        "Purchase verification",
        io.realm.appservicesusagesamples.DemoSelectorActivity::class.java,
        io.realm.appservicesusagesamples.PURCHASE_VERIFICATION_APP_ID,
    );

    val qualifier = named(appId)
}

typealias DemoWithApp = Pair<io.realm.appservicesusagesamples.Demos, App>

/**
 * Koin module for the main entry point.
 */
val mainModule = module {
    // Create singletons for each app.
    for (app in io.realm.appservicesusagesamples.Demos.values()) {
        single(app.qualifier) { App.create(app.appId) }
    }

    viewModel {
        ExamplesScreenViewModel(
            apps = io.realm.appservicesusagesamples.Demos.values()
                .map { demo ->
                    io.realm.appservicesusagesamples.DemoWithApp(
                        first = demo,
                        second = get(demo.qualifier)
                    )
                }
        )
    }
}
