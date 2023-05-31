package io.realm.curatedsyncexamples.fieldencryption

import io.realm.keystore.CipherSpec
import io.realm.kotlin.annotations.ExperimentalRealmSerializerApi
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.ext.customData

@OptIn(ExperimentalRealmSerializerApi::class)
fun User.fieldEncryptionKeySpec(): EncryptionKeySpec {
    return customData<CustomData>()?.fieldEncryptionKeySpec!!
}

@OptIn(ExperimentalRealmSerializerApi::class)
fun User.fieldCipherSpec(): CipherSpec {
    return customData<CustomData>()?.cipherSpec!!
}
