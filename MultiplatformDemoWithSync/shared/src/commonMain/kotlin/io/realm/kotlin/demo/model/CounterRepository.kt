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
package io.realm.kotlin.demo.model

import io.realm.kotlin.Realm
import io.realm.kotlin.internal.platform.runBlocking
import io.realm.kotlin.demo.model.entity.Counter
import io.realm.kotlin.demo.util.Constants.MONGODB_REALM_APP_ID
import io.realm.kotlin.demo.util.Constants.MONGODB_REALM_APP_PASSWORD
import io.realm.kotlin.demo.util.Constants.MONGODB_REALM_APP_USER
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.AppConfiguration
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.notifications.SingleQueryChange
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Repository class. Responsible for storing the io.realm.kotlin.demo.model.entity.Counter and expose updates to it.
 */
class CounterRepository {
    private var realm: Realm
    private val counterObj: Counter

    private val app: App = App.create(AppConfiguration.Builder(MONGODB_REALM_APP_ID).build())

    init {
        realm = runBlocking {
            // Enable Realm with Sync support
            val user = app.login(Credentials.emailPassword(MONGODB_REALM_APP_USER, MONGODB_REALM_APP_PASSWORD))
            val config = SyncConfiguration.Builder(
                schema = setOf(Counter::class),
                user = user,
                partitionValue = "demo-partition",
            )
                .log(LogLevel.ALL)
                .build()

            Realm.open(config)
        }

        // With no support for setting up initial values, we just do it manually.
        // WARNING: Writing directly on the UI thread is not encouraged.
        counterObj = realm.writeBlocking {
            val objects = query<Counter>().find()
            when (objects.size) {
                0 -> copyToRealm(Counter())
                1 -> objects.first()
                else -> throw IllegalStateException("Too many counters: ${objects.size}")
            }
        }
    }

    /**
     * Adjust the counter up and down.
     */
    fun adjust(change: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            realm.write {
                findLatest(counterObj)?.run {
                    operations.add(change)
                } ?: println("Could not update io.realm.kotlin.demo.model.entity.Counter")
            }
        }
    }

    /**
     * Listen to changes to the counter.
     */
    fun observeCounter(): Flow<Long> {
        return realm.query<Counter>("_id = 'primary'").first().asFlow()
            .map { it: SingleQueryChange<Counter> ->
                it.obj?.operations!!.fold(0L,) { sum, el -> sum + el }
            }
    }
}
