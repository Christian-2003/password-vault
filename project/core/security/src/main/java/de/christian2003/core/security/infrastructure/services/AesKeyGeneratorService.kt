package de.christian2003.core.security.infrastructure.services

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import de.christian2003.core.security.domain.services.KeyGeneratorService
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * Generator can generate cryptographic key for AES.
 */
internal class AesKeyGeneratorService @Inject constructor(): KeyGeneratorService {

    /**
     * Generates a cryptographic key.
     *
     * @return  Bytes of the generated key.
     */
    override suspend fun generate(): ByteArray {
        val random = SecureRandom()

        val keyGenerator: KeyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256, random)

        val secretKey: SecretKey = keyGenerator.generateKey()

        return secretKey.encoded
    }


    /**
     * Returns the spec for the key generation within a key store.
     *
     * @param alias Alias for the key store.
     * @return      Spec for the key generation.
     */
    override fun getKeyGenParameterSpec(alias: String): KeyGenParameterSpec {
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        return keyGenParameterSpec
    }


    /**
     * Returns the spec for the key generation within a key store. The generated key will require
     * prior authentication (e.g. through biometrics) before release.
     *
     * @param alias     Alias for the key store.
     * @param timeout   Timeout until key is locked after authentication.
     * @return          Spec for the key generation.
     */
    override fun getKeyGenParameterSpecForSecureKey(alias: String, timeout: Int): KeyGenParameterSpec {
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(false)
            .setUserAuthenticationParameters(timeout, KeyProperties.AUTH_BIOMETRIC_STRONG)
            .build()

        return keyGenParameterSpec
    }

}
