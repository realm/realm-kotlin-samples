package org.example

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.notifications.ListChange
import java.util.stream.Stream
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.types.BaseRealmObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.rx3.asFlowable
import kotlinx.coroutines.rx3.asObservable

class CancellationToken(val job: Job) {
    fun cancel() {
        job.cancel()
    }
}

class RealmRepository {

    interface UpdateCallback {
        fun update(realm: MutableRealm)
    }

    interface EventCallback<E: BaseRealmObject> {
        fun update(item: ResultsChange<E>)
    }

    lateinit var realm: Realm

    fun openRealm() {
        val config = RealmConfiguration.Builder(schema = setOf(Person::class, Child::class))
            .name("javainterop-example.realm")
            .build()
        realm = Realm.open(config)
    }

    fun closeRealm() {
        checkRealm()
        realm.close()
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

    fun updatesAsRxJavaObserverable(): Observable<ResultsChange<Person>> {
        return realm.query<Person>("TRUEPREDICATE").asFlow().asObservable()
    }

    fun updatesAsCoroutineFlow(): Flow<ResultsChange<Person>> {
        return realm.query<Person>("TRUEPREDICATE").asFlow()
    }

    // Expose as callbacks with a cancellation token
    fun updatesAsCallbacks(callback: EventCallback<Person>): CancellationToken {
        val job = CoroutineScope(Dispatchers.Default).launch {
            async {
                realm.query<Person>("TRUEPREDICATE").asFlow().collect { event: ResultsChange<Person> ->
                    callback.update(event)
                }
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
