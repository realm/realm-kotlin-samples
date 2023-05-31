package io.realm.keystore

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.User
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Class that allows to store keys in a Realm file
 */
class RealmKeyStore(
    keyStoreName: String = "keystore"
) {
    private val realm = Realm.open(
        configuration = RealmConfiguration
            .Builder(setOf(UserFieldEncryptionKeys::class, EncryptedKey::class))
            .deleteRealmIfMigrationNeeded()
            .name(keyStoreName)
            .initialData {
                copyToRealm(UserFieldEncryptionKeys())
            }
            .build()
    )

    /**
     * Returns the stored key for a user, null otherwise
     */
    fun getFieldLevelEncryptionKey(user: User): SecretKey =
        realm.query<UserFieldEncryptionKeys>()
            .first()
            .find()!!
            .userKeyStore[user.id]!!
            .let { secretKey: EncryptedKey ->
                val decryptedKey = RealmKeystoreEncryptionService.decrypt(secretKey.key)

                SecretKeySpec(
                    decryptedKey,
                    secretKey.algorithm
                )
            }

    /**
     * Stores a key for a given user
     */
    suspend fun setFieldLevelEncryptionKey(user: User, secretKey: SecretKey) {
        realm.write {
            query<UserFieldEncryptionKeys>()
                .first()
                .find()!!
                .userKeyStore[user.id] = EncryptedKey()
                .apply {
                    key = RealmKeystoreEncryptionService.encrypt(secretKey.encoded)
                    algorithm = secretKey.algorithm
                }
        }
    }
}
