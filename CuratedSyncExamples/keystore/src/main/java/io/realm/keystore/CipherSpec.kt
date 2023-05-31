package io.realm.keystore

import kotlinx.serialization.Serializable

@Serializable
data class CipherSpec(
    val algorithm: String,
    val block: String,
    val padding: String
)
