package io.realm.curatedsyncexamples.fieldencryption

import io.realm.keystore.CipherSpec
import io.realm.keystore.decrypt
import io.realm.keystore.encrypt
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mongodb.kbson.BsonObjectId
import java.nio.charset.StandardCharsets
import java.security.Key
import kotlin.reflect.KProperty

lateinit var cipherSpec: CipherSpec
lateinit var key: Key
lateinit var hash: ByteArray

@Serializable
data class EncryptionKeySpec(
    val algorithm: String,
    val salt: ByteArray,
    @SerialName("iterations_count")
    val iterationsCount: Int,
    @SerialName("key_length")
    val keyLength: Int,
)

@Serializable
data class CustomData(
    @SerialName("field_encryption_key_spec")
    val fieldEncryptionKeySpec: EncryptionKeySpec?,
    @SerialName("encryption_transformation")
    val cipherSpec: CipherSpec?
)

class Dog : RealmObject {
    @PrimaryKey
    var _id: BsonObjectId = BsonObjectId()
    var name: EncryptedStringField? = EncryptedStringField()
}

class EncryptedStringField : EmbeddedRealmObject {
    var keyHash: ByteArray = byteArrayOf()
    var encryptedValue: ByteArray = byteArrayOf()

    @Ignore
    var value: String by DecryptionDelegate()

    inner class DecryptionDelegate {
        private fun EncryptedStringField.isEncryptionKeyValid() =
            keyHash.contentEquals(hash)

        operator fun getValue(thisRef: EncryptedStringField, property: KProperty<*>): String =
            if (!thisRef.isEncryptionKeyValid()) "Wrong encryption key"
            else String(
                bytes = cipherSpec.decrypt(thisRef.encryptedValue, key),
                charset = StandardCharsets.UTF_8
            )

        operator fun setValue(
            thisRef: EncryptedStringField,
            property: KProperty<*>,
            value: String
        ) {
            thisRef.keyHash = hash
            thisRef.encryptedValue = cipherSpec.encrypt(
                input = value.toByteArray(StandardCharsets.UTF_8),
                key = key
            )
        }
    }
}