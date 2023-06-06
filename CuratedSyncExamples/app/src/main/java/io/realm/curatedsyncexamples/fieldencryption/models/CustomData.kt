package io.realm.curatedsyncexamples.fieldencryption.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomData(
    @SerialName("field_encryption_cipher_spec")
    val fieldEncryptionCipherSpec: CipherSpec?,
    @SerialName("key_store")
    val keyStore: UserKeyStore
)
