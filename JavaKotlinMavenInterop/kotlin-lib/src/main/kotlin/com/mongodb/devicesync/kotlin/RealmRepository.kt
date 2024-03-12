package com.mongodb.devicesync.kotlin

import io.reactivex.rxjava3.core.Observable
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.types.BaseRealmObject
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.asObservable
import java.util.concurrent.Executors

class CancellationToken(val job: Job) {
    fun cancel() {
        job.cancel()
    }
}

class RealmRepository {
    init {
        val app = App.create("APP-ID")
        runBlocking {
            user = app.currentUser ?: app.login(Credentials.emailPassword("hello@world.com", "123456"))
        }
    }

    interface UpdateCallback {
        fun update(realm: MutableRealm)
    }

    interface EventCallback<E: BaseRealmObject> {
        fun update(item: ResultsChange<E>)
    }

    private lateinit var user: User
    lateinit var realm: Realm
    val realmDispatchers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1).asCoroutineDispatcher()
    val realmScope: CoroutineScope = CoroutineScope(CoroutineName("RealmScope") + realmDispatchers)

    fun openRealm() {
        val config = SyncConfiguration.Builder(user, schema = setOf(Person::class, Child::class))
            .initialSubscriptions { realm ->
                add(
                    realm.query<Person>()
                )
            }
            .build()
        Realm.deleteRealm(config) // Cleanup any old test data
        realm = Realm.open(config)
    }

    fun closeRealm() {
        checkRealm()
        realmScope.cancel("Closing Realm")
        realmDispatchers.close()
        realm.close()
    }

    fun writeData(person: Person): Person {
        return realm.writeBlocking {
            copyToRealm(person)
        }
    }

    fun writeData(list: List<Person>) {
        realm.writeBlocking {
            list.forEach { person: Person ->
                copyToRealm(person)
            }
        }
    }

    fun updateData(callback: UpdateCallback) {
        realm.writeBlocking {
            callback.update(this)
        }
    }

    fun readData(query: String): List<Person> {
        return realm.query<Person>(query).find()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun updatesAsRxJavaObserverable(): Observable<ResultsChange<Person>> {
        return realm.query<Person>("TRUEPREDICATE").asFlow().asObservable()
    }

    // Expose as callbacks with a cancellation token
    fun updatesAsCallbacks(callback: EventCallback<Person>): CancellationToken {
        val job: Job = realmScope.launch {
            realm.query<Person>("TRUEPREDICATE").asFlow()
                .collect { event: ResultsChange<Person> ->
                    callback.update(event)
                }
        }
        return CancellationToken(job)
    }

    private fun checkRealm() {
        if (!this::realm.isInitialized) {
            throw IllegalStateException("Realm must be opened before calling any other functions.")
        }
    }
}
