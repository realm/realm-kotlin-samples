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
package io.realm.appservicesusagesamples.propertyencryption

import io.realm.appservicesusagesamples.propertyencryption.ui.NavGraphViewModel
import io.realm.appservicesusagesamples.propertyencryption.ui.keystore.UnlockRemoteKeyStoreScreenViewModel
import io.realm.appservicesusagesamples.propertyencryption.ui.login.LoginViewModel
import io.realm.appservicesusagesamples.propertyencryption.ui.records.SecretRecordsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.security.KeyStore


val propertyEncryptionModule = module {
    val keyAlias = "fieldLevelEncryptionKey"

    val androidKeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    viewModel {
        UnlockRemoteKeyStoreScreenViewModel(
            app = get(qualifier = io.realm.appservicesusagesamples.Demos.PROPERTY_ENCRYPTION.qualifier),
            keyAlias = keyAlias,
            localKeyStore = androidKeyStore
        )
    }
    viewModel { LoginViewModel(get(qualifier = io.realm.appservicesusagesamples.Demos.PROPERTY_ENCRYPTION.qualifier)) }
    viewModel {
        SecretRecordsViewModel(
            app = get(qualifier = io.realm.appservicesusagesamples.Demos.PROPERTY_ENCRYPTION.qualifier),
            keyAlias = keyAlias,
            localKeyStore = androidKeyStore
        )
    }
    viewModel {
        NavGraphViewModel(
            app = get(qualifier = io.realm.appservicesusagesamples.Demos.PROPERTY_ENCRYPTION.qualifier),
            localKeyStore = androidKeyStore,
            keyAlias = keyAlias,
        )
    }
}
