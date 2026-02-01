package de.christian2003.security.domain.services

import android.security.keystore.KeyGenParameterSpec


/**
 * Service can generate a cryptographic key.
 */
internal interface KeyGeneratorService {

    /**
     * Generates a cryptographic key.
     *
     * @return  Bytes of the generated key.
     */
    suspend fun generate(): ByteArray


    /**
     * Returns the spec for the key generation within a key store.
     *
     * @param alias Alias for the key store.
     * @return      Spec for the key generation.
     */
    fun getKeyGenParameterSpec(alias: String): KeyGenParameterSpec


    /**
     * Returns the spec for the key generation within a key store. The generated key will require
     * prior authentication (e.g. through biometrics) before release.
     *
     * @param alias     Alias for the key store.
     * @param timeout   Timeout until key is locked after authentication.
     * @return          Spec for the key generation.
     */
    fun getKeyGenParameterSpecForSecureKey(alias: String, timeout: Int): KeyGenParameterSpec

}
