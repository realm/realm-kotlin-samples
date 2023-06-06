package io.realm.curatedsyncexamples.fieldencryption.ui.records

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.curatedsyncexamples.fieldencryption.FIELD_LEVEL_ENCRYPTION_KEY_ALIAS
import io.realm.curatedsyncexamples.fieldencryption.fieldEncryptionCipherSpec
import io.realm.curatedsyncexamples.fieldencryption.models.AndroidKeyStoreHelper
import io.realm.curatedsyncexamples.fieldencryption.models.SecretRecord
import io.realm.curatedsyncexamples.fieldencryption.models.EncryptedStringField
import io.realm.curatedsyncexamples.fieldencryption.models.cipherSpec
import io.realm.curatedsyncexamples.fieldencryption.models.getFieldLevelEncryptionKey
import io.realm.curatedsyncexamples.fieldencryption.models.key
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.internal.platform.runBlocking
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SecretRecordsUiState(
    val loading: Boolean = true,
    val loggingOut: Boolean = false,
    val loggedOut: Boolean = false
)

class SecretRecordsViewModel(
    private val app: App,
) : ViewModel() {
    private lateinit var realm: Realm
    private lateinit var user: User

    private val _uiState = MutableStateFlow(SecretRecordsUiState())
    val uiState: StateFlow<SecretRecordsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            user = app.currentUser!!
            cipherSpec = user.fieldEncryptionCipherSpec()
            runBlocking {
                key = getFieldLevelEncryptionKey(user, "password")
            }

            val syncConfig = SyncConfiguration
                .Builder(app.currentUser!!, setOf(SecretRecord::class, EncryptedStringField::class))
                .initialSubscriptions {
                    // Subscribe to all secret records
                    add(it.query<SecretRecord>())
                }
                .waitForInitialRemoteData()
                .build()

            realm = Realm.open(syncConfig)

            val job = async {
                realm.query<SecretRecord>()
                    .asFlow()
                    .collect {
                        records.value = it.list
                    }
            }

            addCloseable {
                job.cancel()
                realm.close()
            }

            _uiState.update {
                it.copy(loading = false)
            }
        }
    }

    val records: MutableLiveData<List<SecretRecord>> by lazy {
        MutableLiveData<List<SecretRecord>>()
    }

    fun logout() {
        _uiState.update {
            it.copy(loggingOut = true)
        }
        viewModelScope.launch {
            user.logOut()
            AndroidKeyStoreHelper.removeKey(FIELD_LEVEL_ENCRYPTION_KEY_ALIAS)

            _uiState.update {
                it.copy(loggedOut = true)
            }
        }
    }
    fun addRecord(content: String) {
        viewModelScope.launch {
            realm.write {
                copyToRealm(
                    SecretRecord().apply {
                        this.ownerId = user.id
                        this.content!!.value = content
                    }
                )
            }
        }
    }
}