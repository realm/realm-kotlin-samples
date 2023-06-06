package io.realm.curatedsyncexamples.fieldencryption.models

import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import io.realm.curatedsyncexamples.fieldencryption.ANDROID_KEY_STORE_PROVIDER
import io.realm.curatedsyncexamples.fieldencryption.FIELD_LEVEL_ENCRYPTION_KEY_ALIAS
import io.realm.curatedsyncexamples.fieldencryption.generateKey
import io.realm.curatedsyncexamples.fieldencryption.getKeyOrGenerate
import io.realm.curatedsyncexamples.fieldencryption.keyStore
import io.realm.curatedsyncexamples.fieldencryption.updateKeyStore
import io.realm.kotlin.mongodb.User
import java.security.Key
import java.security.KeyStore
import javax.crypto.SecretKey

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

suspend fun getFieldLevelEncryptionKey(user: User, password: String) =
    AndroidKeyStoreHelper
        .getKeyFromAndroidKeyStore(FIELD_LEVEL_ENCRYPTION_KEY_ALIAS) {
            // Key is missing in the Android keystore, retrieve it from the keystore
            val keyStore = user.keyStore()

            keyStore.getKeyOrGenerate(FIELD_LEVEL_ENCRYPTION_KEY_ALIAS, password) {
                // Key is missing in the User keystore, generate a new one
                user.generateKey()
            }.also {
                // We might have modified the user keystore, lets propagate the changes to the server
                if (keyStore.hasChanges) user.updateKeyStore(keyStore)
            }
        }