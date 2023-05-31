package io.realm.curatedsyncexamples.fieldencryption

import java.security.Key
import java.security.MessageDigest
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

const val HASH_ALGORITHM = "SHA-256"

fun EncryptionKeySpec.generateKey(password: String): SecretKey =
    PBEKeySpec(
        /* password = */ password.toCharArray(),
        /* salt = */ salt,
        /* iterationCount = */ iterationsCount,
        /* keyLength = */ keyLength
    ).let {
        SecretKeyFactory.getInstance(algorithm).generateSecret(it)
    }

fun Key.computeHash(): ByteArray =
    MessageDigest.getInstance(HASH_ALGORITHM).digest(encoded)
