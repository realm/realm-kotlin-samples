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

import io.realm.kotlin.demo.ui.SharedViewModel
import io.realm.kotlin.demo.util.CommonFlow

/**
 * Interface describing the ViewModel on both the `shared` and `platform` side.
 */
interface CounterViewModel: SharedViewModel {
    val platform: String
        get() = Platform().platform
    fun observeCounter(): CommonFlow<String>
    fun increment()
    fun decrement()
}