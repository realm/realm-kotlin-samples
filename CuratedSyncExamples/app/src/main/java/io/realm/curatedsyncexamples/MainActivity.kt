package io.realm.curatedsyncexamples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.realm.curatedsyncexamples.fieldencryption.Dog
import io.realm.curatedsyncexamples.fieldencryption.EncryptedStringField
import io.realm.curatedsyncexamples.fieldencryption.cipherSpec
import io.realm.curatedsyncexamples.fieldencryption.computeHash
import io.realm.curatedsyncexamples.fieldencryption.fieldCipherSpec
import io.realm.curatedsyncexamples.fieldencryption.fieldEncryptionKeySpec
import io.realm.curatedsyncexamples.fieldencryption.generateKey
import io.realm.curatedsyncexamples.fieldencryption.hash
import io.realm.curatedsyncexamples.fieldencryption.key
import io.realm.keystore.RealmKeyStore
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.internal.platform.runBlocking
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    lateinit var app: App
    private val keyStore = RealmKeyStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runBlocking {
            app = App.create("cypher-scjvs")

            val email = "${Random.nextLong()}aaabb@bbcccc.com"
            val password = "123456"
            app.emailPasswordAuth.registerUser(email, password)
            val user = app.login(Credentials.emailPassword(email, password))

            val key = user.fieldEncryptionKeySpec().generateKey(password)
            cipherSpec = user.fieldCipherSpec()

            RealmKeyStore().setFieldLevelEncryptionKey(user, key)
        }

        // Get hold of the key
        key = keyStore.getFieldLevelEncryptionKey(app.currentUser!!)
        hash = key.computeHash()

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



