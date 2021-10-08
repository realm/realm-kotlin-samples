/*
 * Copyright 2021 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.realm.kotlin.demo.ui.counter

import io.realm.kotlin.demo.model.CounterRepository
import io.realm.kotlin.demo.util.CommonFlow
import io.realm.kotlin.demo.util.asCommonFlow
import kotlinx.coroutines.flow.map

/**
 * Class for the shared parts of the ViewModel.
 *
 * ViewModels are split into two parts:
 * - `SharedViewModel`, which contains the business logic and communication with
 *   the repository / model layer.
 * - `PlatformViewModel`, which is only a thin wrapper for hooking the SharedViewModel
 *   up to either SwiftUI (through `@ObservedObject`) or to Compose (though Flows).
 *
 * The boundary between these two classes must only be [CommonFlow]'s, which emit
 * on the UI or Main thread.
 *
 * This allows the UI to be fully tested by injecting a mocked ViewModel on the
 * platform side.
 */
class SharedCounterViewModel: CounterViewModel {

    // Implementation note: With a ViewModel this simple, just merging it with
    // Repository would probably be simpler, but by splitting the Repository
    // and ViewModel, we only need to enforce CommonFlows at the boundary, and
    // it means the CounterViewModel can be mocked easily in the View Layer.
    private val repository = CounterRepository()

    override fun observeCounter(): CommonFlow<String> {
        return repository.observeCounter()
            .map { count -> count.toString() }
            .asCommonFlow()
    }

    override fun increment() {
        repository.adjust(1)
    }

    override fun decrement() {
        repository.adjust(-1)
    }
}
