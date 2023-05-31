package io.realm.keystore

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

fun CipherSpec.encrypt(input: ByteArray, key: Key): ByteArray =
    with(Cipher.getInstance(TRANSFORMATION)) {
        init(Cipher.ENCRYPT_MODE, key)
        iv + doFinal(input)
    }

fun CipherSpec.decrypt(encryptedData: ByteArray, key: Key): ByteArray =
    with(Cipher.getInstance(TRANSFORMATION)) {
        init(
            /* opmode = */ Cipher.DECRYPT_MODE,
            /* key = */ key,
            /* params = */ IvParameterSpec(encryptedData, 0, IV_SIZE)
        )
        doFinal(
            /* input = */ encryptedData,
            /* inputOffset = */ IV_SIZE,
            /* inputLen = */ encryptedData.size - IV_SIZE
        )
    }

val CipherSpec.TRANSFORMATION
    get() = "$algorithm/$block/$padding"

val CipherSpec.IV_SIZE: Int  // should be ok for most of the cases
    get() = 16
