package io.realm.curatedsyncexamples.fieldencryption.models

import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import io.realm.curatedsyncexamples.fieldencryption.ANDROID_KEY_STORE_PROVIDER
import java.security.Key
import java.security.KeyStore
import javax.crypto.SecretKey

object AndroidKeyStoreHelper {
    private val keyStore: KeyStore =
        KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER)
            .apply {
                load(null)
            }

    suspend fun getKeyFromAndroidKeyStore(
        keyAlias: String,
        generateKey: suspend AndroidKeyStoreHelper.() -> SecretKey
    ): Key {
        if (!keyStore.isKeyEntry(keyAlias))
            storeKeyInAndroidKeyStore(keyAlias, generateKey())

        return keyStore
            .getKey(keyAlias, null)
    }

    private fun storeKeyInAndroidKeyStore(
        keyAlias: String,
        key: SecretKey
    ) {
        keyStore.setEntry(
            keyAlias,
            KeyStore.SecretKeyEntry(key),
            KeyProtection
                .Builder(
                    /* purposes = */ KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()
        )
    }
}