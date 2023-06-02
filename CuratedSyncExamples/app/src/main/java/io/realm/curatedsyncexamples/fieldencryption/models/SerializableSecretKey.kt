package io.realm.curatedsyncexamples.fieldencryption.models

import kotlinx.serialization.Serializable
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Serializable
class SerializableSecretKey(
    val encoded: ByteArray,
    val algorithm: String,
) {
    constructor(key: SecretKey) : this(
        encoded = key.encoded,
        algorithm = key.algorithm
    )

    fun asSecretKey(): SecretKey = SecretKeySpec(encoded, algorithm)
}
