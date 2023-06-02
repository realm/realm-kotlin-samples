package io.realm.curatedsyncexamples.fieldencryption.models

import io.realm.curatedsyncexamples.fieldencryption.computeHash
import io.realm.kotlin.internal.platform.runBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.security.Key
import javax.crypto.SecretKey


@Serializable
class UserKeyStore(
    /**
     * Spec to generate the keystore encryption key
     */
    @SerialName("encryption_key_spec")
    val encryptionKeySpec: EncryptionKeySpec,

    /**
     * Cipher spec used to encrypt the keys
     */
    @SerialName("cipher_spec")
    val cipherSpec: CipherSpec,

    /**
     * Contents
     */
    @SerialName("secure_contents")
    var secureContents: ByteArray?,

    /**
     * Computed hash for the encryption key used to encode the contents. It allows to
     * identify if the right key was used to decode the contents.
     */
    @SerialName("key_hash")
    var keyHash: ByteArray?,

    @Transient
    var hasChanges: Boolean = false
) {

    private fun loadContents(key: Key): MutableMap<String, SecretKey> =
        secureContents?.let { byteArray ->
            keyHash?.let {
                require(keyHash.contentEquals(key.computeHash())) { "Wrong password" }
            }

            val serializedKeyStore = String(
                cipherSpec.decrypt(byteArray, key),
                StandardCharsets.UTF_8
            )

            Json.decodeFromString<Map<String, SerializableSecretKey>>(serializedKeyStore)
                .map { entry -> entry.key to entry.value.asSecretKey() }
                .toMap()
                .toMutableMap()
        } ?: mutableMapOf()

    private fun saveContents(contents: MutableMap<String, SecretKey>, key: Key) {
        val serializableContents =
            contents.map { entry -> entry.key to SerializableSecretKey(entry.value) }
        val updatedKeyStore = Json.encodeToString(serializableContents)
            .toByteArray(StandardCharsets.UTF_8)

        keyHash = key.computeHash()

        secureContents = cipherSpec.encrypt(updatedKeyStore, key)
    }

    suspend fun <T> use(
        password: String,
        update: suspend MutableMap<String, SecretKey>.() -> T
    ): T =
        // Encryption/decryption can take a while
        runBlocking(Dispatchers.IO) {
            val key = encryptionKeySpec.generateKey(password)

            loadContents(key).let { contents ->
                try {
                    contents.update()
                } finally {
                    saveContents(contents, key)
                }
            }
        }
}
