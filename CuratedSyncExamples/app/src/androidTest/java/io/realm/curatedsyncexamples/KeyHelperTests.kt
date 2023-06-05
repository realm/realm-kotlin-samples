package io.realm.curatedsyncexamples

import android.security.keystore.KeyProperties
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.realm.curatedsyncexamples.fieldencryption.ANDROID_KEY_STORE_PROVIDER
import io.realm.curatedsyncexamples.fieldencryption.getKeyOrGenerate
import io.realm.curatedsyncexamples.fieldencryption.models.AndroidKeyStoreHelper
import io.realm.curatedsyncexamples.fieldencryption.models.CipherSpec
import io.realm.curatedsyncexamples.fieldencryption.models.Dog
import io.realm.curatedsyncexamples.fieldencryption.models.EncryptionKeySpec
import io.realm.curatedsyncexamples.fieldencryption.models.UserKeyStore
import io.realm.curatedsyncexamples.fieldencryption.models.key
import io.realm.curatedsyncexamples.fieldencryption.models.cipherSpec as modelsCipherSpec
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.assertFailsWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

const val KEY_ALIAS = "Testing"
const val ALGORITHM = "AES"

@RunWith(AndroidJUnit4::class)
class KeyHelperTests {
    private val keyGenerator = KeyGenerator.getInstance(ALGORITHM).apply {
        init(128)
    }
    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER).apply {
        load(null)
    }

    @BeforeTest
    fun begin() {
        keyStore.deleteEntry(KEY_ALIAS)
    }

    @Test
    fun storeAndRetrieveKeyInAndroidKeystore() = runTest {
        // Store a key
        AndroidKeyStoreHelper
            .getKeyFromAndroidKeyStore(KEY_ALIAS) {
                keyGenerator.generateKey()
            }

        // The key exists
        assertTrue(keyStore.isKeyEntry(KEY_ALIAS))
        val retrievedKey = keyStore.getKey(KEY_ALIAS, null)

        // We can retrieve it
        assertNotNull(retrievedKey)
        // Matching algorithm
        assertEquals(ALGORITHM, retrievedKey.algorithm)
        // Secured contents
        assertNull(retrievedKey.encoded)
    }

    @Test
    fun useAndroidKeyStoreKeyToEncryptDecrypt() = runTest {
        key = AndroidKeyStoreHelper
            .getKeyFromAndroidKeyStore(KEY_ALIAS) {
                keyGenerator.generateKey()
            }
        modelsCipherSpec = cipherSpec

        val dog = Dog().apply {
            name!!.value = "testing a string"
        }

        assertEquals("testing a string", dog.name!!.value)
    }

    private val keySpec = EncryptionKeySpec(
        algorithm = "PBKDF2WithHmacSHA256",
        salt = Random.nextBytes(16),
        iterationsCount = 100000,
        keyLength = 128,
    )

    private val cipherSpec = CipherSpec(
        algorithm = KeyProperties.KEY_ALGORITHM_AES,
        block = KeyProperties.BLOCK_MODE_CBC,
        padding = KeyProperties.ENCRYPTION_PADDING_PKCS7,
        keyLength = 128
    )

    val userKeyStore = UserKeyStore(
        encryptionKeySpec = keySpec,
        cipherSpec = cipherSpec,
        secureContents = null,
        keyHash = null,
    )

    @Test
    fun storeUserKeyStore() = runTest {
        val key: SecretKey = keyGenerator.generateKey()

        assertFalse(userKeyStore.hasChanges)

        val retrievedKey = userKeyStore
            .getKeyOrGenerate(KEY_ALIAS, "password") {
                key
            }

        assertEquals(key.algorithm, retrievedKey.algorithm)
        assertEquals(key.format, retrievedKey.format)
        assertArrayEquals(key.encoded, retrievedKey.encoded)

        assertTrue(userKeyStore.hasChanges)
    }

    @Test
    fun userKeyStore_wrongPasswordThrows() = runTest {
        userKeyStore
            .getKeyOrGenerate(KEY_ALIAS, "password") {
                keyGenerator.generateKey()
            }

        assertFailsWith<IllegalArgumentException> {
            userKeyStore
                .getKeyOrGenerate(KEY_ALIAS, "password2") {
                    keyGenerator.generateKey()
                }
        }
    }
}