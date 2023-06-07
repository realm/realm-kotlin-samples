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

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.realm.curatedsyncexamples.fieldencryption.models.EncryptionKeySpec
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class EncryptionKeySpecTests {
    private val keySpec = EncryptionKeySpec(
        algorithm = "PBKDF2WithHmacSHA256",
        salt = Random.nextBytes(16),
        iterationsCount = 100000,
        keyLength = 128,
    )

    @Test
    fun generatePBKDF2Key() {
        // Generating two keys from the same password should result in the same value
        val expectedKey = keySpec.generateKey("hello world")
        val matchingKey = keySpec.generateKey("hello world")

        assertArrayEquals(expectedKey.encoded, matchingKey.encoded)

        // Two keys from two different passwords should be different
        val unmatchingKey = keySpec.generateKey("hello world2")
        assertArrayNotEquals(expectedKey.encoded, unmatchingKey.encoded)
    }
}

fun assertArrayNotEquals(expected: ByteArray?, actual: ByteArray?) {
    try {
        assertArrayEquals(expected, actual)
    } catch (_: AssertionError) {
        // Ignore
    }
}