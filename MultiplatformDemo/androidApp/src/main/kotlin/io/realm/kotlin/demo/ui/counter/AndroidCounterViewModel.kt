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

import androidx.lifecycle.ViewModel
import io.realm.kotlin.demo.util.CommonFlow

class AndroidCounterViewModel: CounterViewModel, ViewModel() {
    private val vm = SharedCounterViewModel()
    override fun observeCounter(): CommonFlow<String> = vm.observeCounter()
    override fun increment() = vm.increment()
    override fun decrement() = vm.decrement()

    /**
     * Implementation note: We could avoid the need for doing this
     * by making it possible to inject the `viewModelScope` into the
     * [SharedCounterViewModel], but doing it manually means that the
     * pattern is the same between Android and iOS which lessens the
     * cognitive load when switching between implementations.
     */
    override fun onCleared() {
        vm.close()
    }
}