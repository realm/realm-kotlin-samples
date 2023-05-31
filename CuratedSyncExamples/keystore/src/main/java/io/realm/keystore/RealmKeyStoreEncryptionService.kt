package io.realm.keystore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

internal const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"

/**
 * Allows to securely encrypt/decrypt data to later store in FS
 */
internal object RealmKeystoreEncryptionService {
    private val androidKeyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER)
        .apply {
            load(null)
        }

    private val key: Key by lazy {
        if (!androidKeyStore.containsAlias(ENCRYPTION_KEY_ALIAS))
            generateKey()
        else
            androidKeyStore.getKey(ENCRYPTION_KEY_ALIAS, null)
    }

    private fun generateKey(): SecretKey =
        with(KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE_PROVIDER)) {
            val keyGenParameterSpec = KeyGenParameterSpec
                .Builder(
                    /* keystoreAlias = */ ENCRYPTION_KEY_ALIAS,
                    /* purposes = */ KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setRandomizedEncryptionRequired(false) // Set to true for enhanced security
                .build()
            init(keyGenParameterSpec)
            generateKey()
        }

    fun encrypt(unencryptedArray: ByteArray): ByteArray = cypherSpec.encrypt(unencryptedArray, key)

    fun decrypt(encryptedArray: ByteArray): ByteArray = cypherSpec.decrypt(encryptedArray, key)

    private const val ENCRYPTION_KEY_ALIAS = "REALM_KEYSTORE_KEY"

    private val cypherSpec = CipherSpec(
        algorithm = KeyProperties.KEY_ALGORITHM_AES,
        block = KeyProperties.BLOCK_MODE_CBC,
        padding = KeyProperties.ENCRYPTION_PADDING_PKCS7,
    )
}