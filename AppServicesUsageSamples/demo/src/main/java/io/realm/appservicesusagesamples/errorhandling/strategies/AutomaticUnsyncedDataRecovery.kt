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
package io.realm.appservicesusagesamples.errorhandling.strategies

import io.realm.appservicesusagesamples.errorhandling.ui.ErrorHandlingViewModel
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.TypedRealm
import io.realm.kotlin.mongodb.exceptions.ClientResetRequiredException
import io.realm.kotlin.mongodb.sync.RecoverUnsyncedChangesStrategy
import io.realm.kotlin.mongodb.sync.SyncSession
import kotlinx.coroutines.flow.update

/**
 * Client reset strategy that tries to automatically try to recover any unsynced changes.
 */
val ErrorHandlingViewModel.automaticUnsyncedDataRecovery
    get() = object : RecoverUnsyncedChangesStrategy {
        override fun onAfterReset(before: TypedRealm, after: MutableRealm) {
            _uiState.update {
                it.copy(
                    loading = false,
                    errorMessage = "Client reset: unsynced changes recovered."
                )
            }
        }

        override fun onBeforeReset(realm: TypedRealm) = Unit

        override fun onManualResetFallback(
            session: SyncSession,
            exception: ClientResetRequiredException,
        ) = Unit
    }
