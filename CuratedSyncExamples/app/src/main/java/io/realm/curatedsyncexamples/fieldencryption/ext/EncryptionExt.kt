package io.realm.curatedsyncexamples.fieldencryption

import io.realm.curatedsyncexamples.fieldencryption.models.CipherSpec
import io.realm.curatedsyncexamples.fieldencryption.models.UserKeyStore
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
    generateNewKey: suspend () -> SecretKey
): SecretKey = use(password) {
    if (!contains(alias)) {
        set(alias, generateNewKey())
            .also {
                hasChanges = true
            }
    }

    get(alias)!!
}

fun CipherSpec.newKey(): SecretKey =
    KeyGenerator
        .getInstance(
            /* algorithm = */ algorithm
        ).apply {
            init(keyLength)
        }
        .generateKey()

fun User.generateKey(): SecretKey = fieldEncryptionCipherSpec().newKey()
