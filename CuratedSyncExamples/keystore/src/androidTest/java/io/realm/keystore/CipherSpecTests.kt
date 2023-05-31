package io.realm.keystore

import android.security.keystore.KeyProperties
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CipherSpecTests {
    private val key = SecretKeySpec(
        Random.nextBytes(32),
        "AES"
    )

    private val cypherSpec = CipherSpec(
        algorithm = KeyProperties.KEY_ALGORITHM_AES,
        block = KeyProperties.BLOCK_MODE_CBC,
        padding = KeyProperties.ENCRYPTION_PADDING_PKCS7,
    )

    @Test
    fun encryptAndDecryptData() {
        val input = "Hello world"

        val encryptedValue = cypherSpec.encrypt(
            input = input.toByteArray(StandardCharsets.UTF_8),
            key = key
        )

        val decryptedString =
            String(
                bytes = cypherSpec.decrypt(encryptedValue, key),
                charset = StandardCharsets.UTF_8
            )

        assertArrayNotEquals(input.toByteArray(StandardCharsets.UTF_8), encryptedValue)
        Assert.assertEquals(input, decryptedString)
    }
}