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
import io.realm.appservicesusagesamples.errorhandling.ui.UiStateFlow
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.TypedRealm
import io.realm.kotlin.mongodb.exceptions.ClientResetRequiredException
import io.realm.kotlin.mongodb.sync.DiscardUnsyncedChangesStrategy
import io.realm.kotlin.mongodb.sync.SyncSession
import kotlinx.coroutines.flow.update

/**
 * Client reset strategy that would automatically discard any unsynced local changes.
 */
val ErrorHandlingViewModel.discardUnsyncedData
    get() = object : DiscardUnsyncedChangesStrategy {
        override fun onAfterReset(before: TypedRealm, after: MutableRealm) {
            _uiState.update {
                it.copy(loading = false, errorMessage = "Client reset: unsynced changes discarded.")
            }
        }

        override fun onBeforeReset(realm: TypedRealm) = Unit

        override fun onError(session: SyncSession, exception: ClientResetRequiredException) = Unit

        override fun onManualResetFallback(
            session: SyncSession,
            exception: ClientResetRequiredException,
        ) = Unit
    }
