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
package io.realm.appservicesusagesamples

import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.realm.appservicesusagesamples.propertyencryption.ext.generateKey
import io.realm.appservicesusagesamples.propertyencryption.models.SerializableCipherSpec
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyStore
import java.security.KeyStore.SecretKeyEntry
import javax.crypto.SecretKey
import kotlin.test.BeforeTest

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
    private val androidKeyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER).apply {
        load(null)
    }

    private val cipherSpec = SerializableCipherSpec(
        algorithm = KeyProperties.KEY_ALGORITHM_AES,
        block = KeyProperties.BLOCK_MODE_CBC,
        padding = KeyProperties.ENCRYPTION_PADDING_PKCS7,
        keyLength = 128
    )

    @BeforeTest
    fun begin() {
        androidKeyStore.deleteEntry(KEY_ALIAS)
    }

    @Test
    fun importKeyToAndroidKeyStore() {
        val keyAlias = "alias"
        val secretKey = cipherSpec.generateKey()

        val inMemoryKeyStore = KeyStore.getInstance("BKS")
            .apply {
                load(null)
                setEntry(
                    keyAlias,
                    SecretKeyEntry(secretKey),
                    null
                )
            }

        // Retrieve stored key
        val key = inMemoryKeyStore.getKey(keyAlias, null) as SecretKey

        // Store the key locally
        androidKeyStore.setEntry(
            KEY_ALIAS,
            SecretKeyEntry(key),
            KeyProtection
                .Builder(
                    /* purposes = */ KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                .setBlockModes(cipherSpec.block)
                .setEncryptionPaddings(cipherSpec.padding)
                .build()
        )
    }
}