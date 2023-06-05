package io.realm.curatedsyncexamples.fieldencryption.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.lang.Exception
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

private const val IV_SIZE: Int = 16  // should be ok for most of the cases

@Serializable
data class CipherSpec(
    val algorithm: String,
    val block: String,
    val padding: String,
    @SerialName("key_length")
    val keyLength: Int
) {
    @Transient
    private val transformation = "$algorithm/$block/$padding"

    fun encrypt(input: ByteArray, key: Key): ByteArray =
        with(Cipher.getInstance(transformation)) {
            init(Cipher.ENCRYPT_MODE, key)
            iv + doFinal(input)
        }

    fun decrypt(encryptedData: ByteArray, key: Key): ByteArray =
        with(Cipher.getInstance(transformation)) {
            init(
                /* opmode = */ Cipher.DECRYPT_MODE,
                /* key = */ key,
                /* params = */ IvParameterSpec(encryptedData, 0, IV_SIZE)
            )
            try {
                doFinal(
                    /* input = */ encryptedData,
                    /* inputOffset = */ IV_SIZE,
                    /* inputLen = */ encryptedData.size - IV_SIZE
                )
            } catch (e: Exception) {
                byteArrayOf()
            }

        }
}
