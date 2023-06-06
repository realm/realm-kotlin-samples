package io.realm.curatedsyncexamples.fieldencryption.models

import kotlinx.serialization.Serializable
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Serializable
class SerializableSecretKey(
    private val encoded: ByteArray,
    val cipherSpec: CipherSpec
) {
    constructor(key: SecretKey, cipherSpec: CipherSpec) : this(
        encoded = key.encoded,
        cipherSpec = cipherSpec
    )

    fun asSecretKey(): SecretKey = SecretKeySpec(encoded, cipherSpec.algorithm)
}
