package io.realm.curatedsyncexamples.fieldencryption

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import io.realm.curatedsyncexamples.app
import io.realm.curatedsyncexamples.fieldencryption.models.AndroidKeyStoreHelper
import io.realm.curatedsyncexamples.fieldencryption.models.Dog
import io.realm.curatedsyncexamples.fieldencryption.models.EncryptedStringField
import io.realm.curatedsyncexamples.fieldencryption.models.cipherSpec
import io.realm.curatedsyncexamples.fieldencryption.models.key
import io.realm.curatedsyncexamples.fieldencryption.ui.LoginScreen
import io.realm.curatedsyncexamples.fieldencryption.ui.NavGraph
import io.realm.curatedsyncexamples.fieldencryption.ui.dogs.DogsViewModel
import io.realm.curatedsyncexamples.ui.theme.CuratedSyncExamplesTheme
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.internal.platform.runBlocking
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.launch
import kotlin.random.Random

const val FIELD_LEVEL_ENCRYPTION_KEY_ALIAS = "fieldLevelEncryptionKey"
const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"

class MainActivity : ComponentActivity() {
    private lateinit var app: App

    private val model: DogsViewModel by viewModels()

    private suspend fun getFieldLevelEncryptionKey(user: User, password: String) =
        AndroidKeyStoreHelper
            .getKeyFromAndroidKeyStore(FIELD_LEVEL_ENCRYPTION_KEY_ALIAS) {
                // Key is missing in the Android keystore, retrieve it from the keystore
                val keyStore = user.keyStore()

                keyStore.getKeyOrGenerate(FIELD_LEVEL_ENCRYPTION_KEY_ALIAS, password) {
                    // Key is missing in the User keystore, generate a new one
                    user.generateKey()
                }.also {
                    // We might have modified the user keystore, lets propagate the changes to the server
                    if (keyStore.hasChanges) user.updateKeyStore(keyStore)
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CuratedSyncExamplesTheme {
                NavGraph()
            }
        }
    }

    private fun init() {
        app = application.app()

        runBlocking {
            val email = "${Random.nextLong()}aaabb@bbcccc.com"
            val password = "123456"
            app.emailPasswordAuth.registerUser(email, password)
            val user = app.login(Credentials.emailPassword(email, password))

            cipherSpec = user.fieldEncryptionCipherSpec()
            key = getFieldLevelEncryptionKey(user, password)
        }
        val syncConfig = SyncConfiguration
            .Builder(app.currentUser!!, setOf(Dog::class, EncryptedStringField::class))
            .initialSubscriptions {
                // Subscribe to all dogs
                add(it.query<Dog>())
            }
            .waitForInitialRemoteData()
            .build()

        val realm = Realm.open(syncConfig)

        lifecycleScope.launch {
            realm.query<Dog>()
                .asFlow()
                .collect {
                    model.dogs.value = it.list
                }
        }

        realm.writeBlocking {

            copyToRealm(Dog().apply {
                ownerId = app.currentUser!!.id
                name?.let { name ->
                    name.value = "hello world1 sdfasdfasdfas"
                }
            })
            copyToRealm(Dog().apply {
                ownerId = app.currentUser!!.id
                name?.let { name ->
                    name.value = "hello world2 sdfasdfasdfas"
                }
            })
            copyToRealm(Dog().apply {
                ownerId = app.currentUser!!.id
                name?.let { name ->
                    name.value = "hello world3 sdfasdfasdfas"
                }
            })
        }
    }
}
