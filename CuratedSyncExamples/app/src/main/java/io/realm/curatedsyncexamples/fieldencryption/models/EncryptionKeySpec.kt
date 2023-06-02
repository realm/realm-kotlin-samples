package io.realm.curatedsyncexamples.fieldencryption.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Serializable
class EncryptionKeySpec(
    val algorithm: String,
    val salt: ByteArray,
    @SerialName("iterations_count")
    val iterationsCount: Int,
    @SerialName("key_length")
    val keyLength: Int,
) {
    fun generateKey(password: String): SecretKey =
        PBEKeySpec(
            /* password = */ password.toCharArray(),
            /* salt = */ salt,
            /* iterationCount = */ iterationsCount,
            /* keyLength = */ keyLength
        ).let {
            SecretKeyFactory.getInstance(algorithm).generateSecret(it)
        }
}
