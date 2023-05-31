package io.realm.keystore

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.internal.platform.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class RealmKeyStoreTests {
    private val key = SecretKeySpec(
        Random.nextBytes(32),
        "AES"
    )

    @Test
    fun storeGeneratedPBKDF2KeySecurely() {
        val realmKeyStore = RealmKeyStore()
        val user = UserMock("myid")

        runBlocking {
            realmKeyStore.setFieldLevelEncryptionKey(user, key)
        }

        val retrievedKey = realmKeyStore.getFieldLevelEncryptionKey(user)!!

        // Validate that we can store and retrieve a key
        assertArrayEquals(key.encoded, retrievedKey.encoded)
        assertEquals(key.algorithm, retrievedKey.algorithm)

        // Now validate that the stored value is encrypted
        val realm = Realm.open(
            configuration = RealmConfiguration
                .Builder(setOf(UserFieldEncryptionKeys::class, EncryptedKey::class))
                .name("keystore")
                .initialData {
                    copyToRealm(UserFieldEncryptionKeys())
                }
                .build()
        )

        val storedKey: EncryptedKey = realm.query<UserFieldEncryptionKeys>()
            .first()
            .find()!!
            .userKeyStore[user.id]!!


        // Validate that the value is encrypted
        assertArrayNotEquals(key.encoded, storedKey.key)
        assertEquals(key.algorithm, storedKey.algorithm)
    }
}
