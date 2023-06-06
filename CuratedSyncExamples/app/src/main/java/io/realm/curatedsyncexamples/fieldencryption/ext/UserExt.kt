package io.realm.curatedsyncexamples.fieldencryption.ext

import io.realm.curatedsyncexamples.fieldencryption.models.CipherSpec
import io.realm.curatedsyncexamples.fieldencryption.models.CustomData
import io.realm.curatedsyncexamples.fieldencryption.models.UserKeyStore
import io.realm.kotlin.annotations.ExperimentalRealmSerializerApi
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.ext.call
import io.realm.kotlin.mongodb.ext.customData

@OptIn(ExperimentalRealmSerializerApi::class)
fun User.keyStore(): UserKeyStore {
    return customData<CustomData>()?.keyStore!!
}

@OptIn(ExperimentalRealmSerializerApi::class)
suspend fun User.updateKeyStore(keyStore: UserKeyStore) {
    functions.call<Boolean>("updateKeyStore") {
        add(keyStore)
    }
}

@OptIn(ExperimentalRealmSerializerApi::class)
fun User.fieldEncryptionCipherSpec(): CipherSpec {
    return customData<CustomData>()?.fieldEncryptionCipherSpec!!
}
