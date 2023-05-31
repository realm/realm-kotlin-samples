package io.realm.keystore

import io.realm.kotlin.ext.realmDictionaryOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmDictionary
import io.realm.kotlin.types.RealmObject

class UserFieldEncryptionKeys : RealmObject {
    var userKeyStore: RealmDictionary<EncryptedKey?> = realmDictionaryOf()
}

class EncryptedKey : EmbeddedRealmObject {
    var key: ByteArray = byteArrayOf()
    var algorithm: String = ""
}
