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

import io.realm.appservicesusagesamples.errorhandling.ui.UiStateFlow
import io.realm.kotlin.mongodb.exceptions.ClientResetRequiredException
import io.realm.kotlin.mongodb.sync.ManuallyRecoverUnsyncedChangesStrategy
import io.realm.kotlin.mongodb.sync.SyncSession
import kotlinx.coroutines.flow.update

fun backupRealm(_uiState: UiStateFlow): ManuallyRecoverUnsyncedChangesStrategy {
    return object : ManuallyRecoverUnsyncedChangesStrategy {
        override fun onClientReset(session: SyncSession, exception: ClientResetRequiredException) {
            exception.executeClientReset()
            exception.recoveryFilePath // this file contains a backed up realm file.

            // After a manual client reset all Realm instances would be automatically closed. We then
            // have to manually recover the app state by loading all data. Depending the case it is
            // faster to restart the app.
            _uiState.update {
                it.copy(
                    restart = true,
                    errorMessage = "Client reset: Realm backed up successfully. App restarted."
                )
            }
        }
    }
}