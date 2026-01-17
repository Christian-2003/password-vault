package de.christian2003.security.domain.repositories

import android.security.keystore.KeyGenParameterSpec
import javax.crypto.SecretKey


/**
 * Repository to access hardware-backed keys.
 */
interface HardwareBackedKeyRepository {

    /**
     * Gets the hardware-backed key with the specified alias. If no key with the specified alias
     * exists, null is returned.
     *
     * @param alias Alias of the key to get.
     * @return      Secret key with the specified alias or null.
     */
    fun getKey(alias: String): SecretKey?


    /**
     * Generates a new hardware-backed key with the specified alias and returns it. The key will be
     * retrievable using getKey(String) for subsequent uses.
     *
     * @param alias                 Alias for the new key.
     * @param algorithm             Algorithm for the generated key (e.g. "AES").
     * @param keyGenParameterSpec   Specs for the key generation.
     * @return                      New secret key with the specified alias.
     */
    fun generateNewKey(
        alias: String,
        algorithm: String,
        keyGenParameterSpec: KeyGenParameterSpec
    ): SecretKey


    /**
     * Tests whether a hardware-backed key with the specified alias exists.
     *
     * @alias   Alias of the key to check.
     * @return  Whether a key with the specified alias exists.
     */
    fun containsKey(alias: String): Boolean


    /**
     * Deletes the hardware-backed key with the specified alias and returns true if the key was
     * deleted successfully. If the key cannot be removed (e.g. because a key with the provided alias
     * does not exists), false is returned.
     *
     * @param alias Alias of the key to delete.
     * @return      Whether the key was deleted successfully.
     */
    fun deleteKey(alias: String): Boolean

}
