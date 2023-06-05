package io.realm.curatedsyncexamples.fieldencryption.models

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.annotations.Ignore
import java.security.Key
import kotlin.reflect.KProperty


lateinit var cipherSpec: CipherSpec
lateinit var key: Key

class EncryptedStringField : EmbeddedRealmObject {
    var encryptedValue: ByteArray = byteArrayOf()

    /**
     * This delegated property provides seamless access to the encrypted data.
     */
    @Ignore
    var value: String by DecryptionDelegate()

    inner class DecryptionDelegate {
        operator fun getValue(thisRef: EncryptedStringField, property: KProperty<*>): String =
            String(
                bytes = cipherSpec.decrypt(thisRef.encryptedValue, key)
            )

        operator fun setValue(
            thisRef: EncryptedStringField,
            property: KProperty<*>,
            value: String
        ) {
            thisRef.encryptedValue = cipherSpec.encrypt(
                input = value.toByteArray(),
                key = key
            )
        }
    }
}