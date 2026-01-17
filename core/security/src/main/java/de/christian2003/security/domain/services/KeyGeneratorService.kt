package de.christian2003.security.domain.services

import android.security.keystore.KeyGenParameterSpec


/**
 * Service can generate a cryptographic key.
 */
interface KeyGeneratorService {

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

}
