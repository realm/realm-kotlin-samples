package io.realm.curatedsyncexamples.fieldencryption

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.realm.curatedsyncexamples.ExampleEntry
import io.realm.curatedsyncexamples.Greeting
import io.realm.curatedsyncexamples.MessageList
import io.realm.curatedsyncexamples.app
import io.realm.curatedsyncexamples.entries
import io.realm.curatedsyncexamples.fieldencryption.models.AndroidKeyStoreHelper
import io.realm.curatedsyncexamples.fieldencryption.models.Dog
import io.realm.curatedsyncexamples.fieldencryption.models.EncryptedStringField
import io.realm.curatedsyncexamples.fieldencryption.models.cipherSpec
import io.realm.curatedsyncexamples.fieldencryption.models.key
import io.realm.curatedsyncexamples.ui.theme.CuratedSyncExamplesTheme
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.internal.platform.runBlocking
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlin.random.Random

const val FIELD_LEVEL_ENCRYPTION_KEY_ALIAS = "fieldLevelEncryptionKey"
const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"

class MainActivity : ComponentActivity() {
    private lateinit var app: App

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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MessageList(entries)
                }
            }
        }

        app = application.app()

        runBlocking {
            val email = "${Random.nextLong()}aaabb@bbcccc.com"
            val password = "123456"
            app.emailPasswordAuth.registerUser(email, password)
            val user = app.login(Credentials.emailPassword(email, password))

            key = getFieldLevelEncryptionKey(user, password)
            cipherSpec = user.fieldEncryptionCipherSpec()
        }

        val realm = Realm.open(
            SyncConfiguration
                .Builder(app.currentUser!!, setOf(Dog::class, EncryptedStringField::class))
                .initialSubscriptions {
                    add(it.query<Dog>())
                }
                .build()
        )

        val dog = realm.writeBlocking {
            val dog = copyToRealm(Dog())

            dog.name?.let { name ->
                name.value = "hello world"
            }

            val ev = dog.name!!.encryptedValue

            val name = dog.name!!.value
        }
    }
}
