package io.realm.appservicesusagesamples.dynamicdata.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.appservicesusagesamples.dynamicdata.models.DynamicDataEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmAnyDictionaryOf
import io.realm.kotlin.ext.realmAnyListOf
import io.realm.kotlin.ext.realmDictionaryOf
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.types.RealmAny
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class DynamicDataViewModel(
    private val app: App,
): ViewModel() {
    lateinit var user: User
    private lateinit var realm: Realm
    init {
        viewModelScope.launch(Dispatchers.IO) {
            app.login(Credentials.anonymous())
                .let { user ->
                    this@DynamicDataViewModel.user = user

                    val syncConfig = SyncConfiguration
                        .Builder(app.currentUser!!, setOf(DynamicDataEntity::class))
                        .initialSubscriptions {
                            add(it.query<DynamicDataEntity>())
                        }

                        .waitForInitialRemoteData()
                        .initialData {
                            if (this.query<DynamicDataEntity>().find().isEmpty()) {
                                val client1 = copyToRealm(DynamicDataEntity().apply {
                                    name = "Client 1"
                                    configuration = RealmAny.Companion.create("Hello, World!")
                                })
                                copyToRealm(DynamicDataEntity().apply {
                                    name = "Client 2"
                                    configuration = realmAnyListOf(
                                        realmDictionaryOf("key1" to realmAnyListOf(1, ObjectId(), "Nested collection")),
                                        "String",
                                        client1
                                    )
                                })
                                copyToRealm(DynamicDataEntity().apply {
                                    name = "Client 3"
                                    configuration = realmAnyDictionaryOf(
                                        "key1" to realmAnyListOf(
                                            1,
                                            ObjectId(),
                                            "Nested collection"
                                        )
                                    )
                                } )
                            }
                        }
                        .build()
                    Realm.deleteRealm(syncConfig)

                    realm = Realm.open(syncConfig)

                    val job = async {
                        realm.query<DynamicDataEntity>()
                            .sort("name")
                            .asFlow()
                            .collect {
                                instances.postValue(it.list)
                            }
                    }

                    addCloseable {
                        job.cancel()
                        realm.close()
                    }
                }
        }
    }

    val instances: MutableLiveData<List<DynamicDataEntity>> by lazy {
        MutableLiveData<List<DynamicDataEntity>>()
    }
}
