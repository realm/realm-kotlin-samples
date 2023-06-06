package io.realm.curatedsyncexamples.fieldencryption.ext

import io.realm.curatedsyncexamples.fieldencryption.models.CipherSpec
import io.realm.curatedsyncexamples.fieldencryption.models.SerializableSecretKey
import io.realm.curatedsyncexamples.fieldencryption.models.UserKeyStore
import io.realm.curatedsyncexamples.fieldencryption.models.cipherSpec
import io.realm.kotlin.mongodb.User
import java.security.Key
import java.security.MessageDigest
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

const val HASH_ALGORITHM = "SHA-256"

fun Key.computeHash(): ByteArray =
    MessageDigest.getInstance(HASH_ALGORITHM).digest(encoded)

suspend fun UserKeyStore.getKeyOrGenerate(
    alias: String,
    password: String,
    generateNewKey: suspend () -> SerializableSecretKey
): SerializableSecretKey = use(password) {
    if (!contains(alias)) {
        set(alias, generateNewKey())
            .also {
                hasChanges = true
            }
    }

    get(alias)!!
}

fun CipherSpec.newKey(): SerializableSecretKey =
    KeyGenerator
        .getInstance(
            /* algorithm = */ algorithm
        ).apply {
            init(keyLength)
        }
        .generateKey()
        .let { key->
            SerializableSecretKey(
                key = key,
                cipherSpec = this@newKey
            )
        }

fun User.generateKey(): SerializableSecretKey = fieldEncryptionCipherSpec().newKey()
