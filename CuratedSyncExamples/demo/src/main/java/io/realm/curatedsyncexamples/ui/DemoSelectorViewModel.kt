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
package io.realm.curatedsyncexamples.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.curatedsyncexamples.DemoWithApp
import io.realm.curatedsyncexamples.Demos
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.AuthenticationProvider
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.exceptions.ServiceException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias DemoWithStatus = Pair<Demos, Boolean>

class ExamplesScreenViewModel(private val apps: List<DemoWithApp>) : ViewModel() {
    private val loading = MutableStateFlow(true)
    val loadingState: StateFlow<Boolean> = loading.asStateFlow()

    val demoEntriesWithStatus: MutableLiveData<List<DemoWithStatus>> by lazy { MutableLiveData<List<DemoWithStatus>>() }

    private suspend fun App.isAvailable() =
        try {
            // Try to perform some action to validate that the app exists
            emailPasswordAuth.resendConfirmationEmail("realm")
            true
        } catch (e: ServiceException) {
            e.message?.startsWith("[Service][Unknown(4351)] cannot find app") != true
        }

    private suspend fun getDemoEntriesWithStatus() =
        apps.map { demoWithApp ->
            DemoWithStatus(demoWithApp.first, demoWithApp.second.isAvailable())
        }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            demoEntriesWithStatus.postValue(getDemoEntriesWithStatus())
            loading.update { false }
        }
    }
}

data class ExampleEntry(
    val name: String,
    val activity: Class<*>
)
