package io.realm.curatedsyncexamples.fieldencryption.models

import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import io.realm.curatedsyncexamples.fieldencryption.generateKey
import io.realm.curatedsyncexamples.fieldencryption.getKeyOrGenerate
import io.realm.curatedsyncexamples.fieldencryption.keyStore
import io.realm.curatedsyncexamples.fieldencryption.updateKeyStore
import io.realm.kotlin.mongodb.User
import java.security.Key
import java.security.KeyStore
import javax.crypto.SecretKey

private const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"

object AndroidKeyStoreHelper {
    private val keyStore: KeyStore =
        KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER)
            .apply {
                load(null)
            }

    fun containsKey(keyAlias: String) = keyStore.isKeyEntry(keyAlias)

    fun removeKey(keyAlias: String) = keyStore.deleteEntry(keyAlias)

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

suspend fun getFieldLevelEncryptionKey(keyAlias: String, user: User, password: String) =
    AndroidKeyStoreHelper
        .getKeyFromAndroidKeyStore(keyAlias) {
            // Key is missing in the Android keystore, retrieve it from the keystore
            val keyStore = user.keyStore()

            keyStore.getKeyOrGenerate(keyAlias, password) {
                // Key is missing in the User keystore, generate a new one
                user.generateKey()
            }.also {
                // We might have modified the user keystore, lets propagate the changes to the server
                if (keyStore.hasChanges) user.updateKeyStore(keyStore)
            }
        }