/*
 * Copyright 2023 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.realm.curatedsyncexamples

import android.security.keystore.KeyProperties
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.realm.curatedsyncexamples.fieldencryption.ext.getKeyOrGenerate
import io.realm.curatedsyncexamples.fieldencryption.models.SystemKeyStore
import io.realm.curatedsyncexamples.fieldencryption.models.SerializableCipherSpec
import io.realm.curatedsyncexamples.fieldencryption.models.SerializablePBEKeySpec
import io.realm.curatedsyncexamples.fieldencryption.models.SecretRecord
import io.realm.curatedsyncexamples.fieldencryption.models.SerializableSecretKey
import io.realm.curatedsyncexamples.fieldencryption.models.UserKeyStore
import io.realm.curatedsyncexamples.fieldencryption.models.key
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
import io.realm.curatedsyncexamples.fieldencryption.models.cipherSpec as modelsCipherSpec

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

const val KEY_ALIAS = "Testing"
const val ALGORITHM = "AES"

private const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"

@RunWith(AndroidJUnit4::class)
class KeyHelperTests {
    private val keyGenerator = KeyGenerator.getInstance(ALGORITHM).apply {
        init(128)
    }
    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER).apply {
        load(null)
    }

    private val keySpec = SerializablePBEKeySpec(
        algorithm = "PBKDF2WithHmacSHA256",
        salt = Random.nextBytes(16),
        iterationsCount = 100000,
        keyLength = 128,
    )

    private val cipherSpec = SerializableCipherSpec(
        algorithm = KeyProperties.KEY_ALGORITHM_AES,
        block = KeyProperties.BLOCK_MODE_CBC,
        padding = KeyProperties.ENCRYPTION_PADDING_PKCS7,
        keyLength = 128
    )

    private val userKeyStore = UserKeyStore(
        encryptionKeySpec = keySpec,
        cipherSpec = cipherSpec,
        secureContents = null,
        keyHash = null,
    )

    @BeforeTest
    fun begin() {
        keyStore.deleteEntry(KEY_ALIAS)
    }

    @Test
    fun storeAndRetrieveKeyInAndroidKeystore() = runTest {
        // Store a key
        SystemKeyStore
            .getKey(KEY_ALIAS) {
                SerializableSecretKey(
                    key = keyGenerator.generateKey(),
                    cipherSpec = cipherSpec
                )
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
        key = SystemKeyStore
            .getKey(KEY_ALIAS) {
                SerializableSecretKey(
                    key = keyGenerator.generateKey(),
                    cipherSpec = cipherSpec
                )
            }
        modelsCipherSpec = cipherSpec

        val record = SecretRecord().apply {
            content = "testing a string"
        }

        assertEquals("testing a string", record.content)
    }

    @Test
    fun storeUserKeyStore() = runTest {
        val key: SecretKey = keyGenerator.generateKey()

        assertFalse(userKeyStore.hasChanges)

        val retrievedKey = userKeyStore
            .getKeyOrGenerate(KEY_ALIAS, "password") {
                SerializableSecretKey(
                    key = key,
                    cipherSpec = cipherSpec
                )
            }

        assertEquals(key.algorithm, retrievedKey.cipherSpec.algorithm)
        assertArrayEquals(key.encoded, retrievedKey.encoded)

        assertTrue(userKeyStore.hasChanges)
    }

    @Test
    fun userKeyStore_wrongPasswordThrows() = runTest {
        userKeyStore
            .getKeyOrGenerate(KEY_ALIAS, "password") {
                SerializableSecretKey(
                    key = keyGenerator.generateKey(),
                    cipherSpec = cipherSpec
                )
            }

        assertFailsWith<IllegalArgumentException> {
            userKeyStore
                .getKeyOrGenerate(KEY_ALIAS, "password2") {
                    SerializableSecretKey(
                        key = keyGenerator.generateKey(),
                        cipherSpec = cipherSpec
                    )
                }
        }
    }
}