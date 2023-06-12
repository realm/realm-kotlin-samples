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
package io.realm.curatedsyncexamples

import io.realm.curatedsyncexamples.fieldencryption.FieldEncryptionActivity
import io.realm.curatedsyncexamples.ui.ExamplesScreenViewModel
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
        FIELD_ENCRYPTION_APP_ID
    ),
    USER_PRESENCE(
        "User presence",
        DemoSelectorActivity::class.java,
        USER_PRESENCE_APP_ID,
    ),
    OFFLINE_LOGIN(
        "Offline login",
        DemoSelectorActivity::class.java,
        OFFLINE_LOGIN_APP_ID,
    ),
    ERROR_HANDLING(
        "Error handling",
        DemoSelectorActivity::class.java,
        ERROR_HANDLING_APP_ID,
    ),
    BUSINESS_LOGIC(
        "Business logic",
        DemoSelectorActivity::class.java,
        BUSINESS_LOGIC_APP_ID,
    ),
    PURCHASE_VERIFICATION(
        "Purchase verification",
        DemoSelectorActivity::class.java,
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
        ExamplesScreenViewModel(
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
