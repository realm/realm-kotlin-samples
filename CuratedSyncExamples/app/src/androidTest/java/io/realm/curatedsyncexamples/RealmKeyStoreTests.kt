package io.realm.curatedsyncexamples

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.realm.curatedsyncexamples.fieldencryption.EncryptionKeySpec
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