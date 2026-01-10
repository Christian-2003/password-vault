package de.christian2003.security.domain.repositories

import de.christian2003.security.domain.entities.KekEntry


/**
 * Repository through which to access the encrypted key encryption key (KEK).
 */
interface KekRepository {

    /**
     * Gets the encrypted key encryption key (KEK) or returns null if no KEK is available.
     *
     * @param entry KEK entry to return.
     * @return      Encrypted KEK or null.
     */
    fun getEncryptedKek(entry: KekEntry): ByteArray?


    /**
     * Changes the encrypted key encryption key (KEK).
     *
     * @param entry         KEK entry to set.
     * @param encryptedKek  New encrypted KEK.
     */
    fun setEncryptedKek(entry: KekEntry, encryptedKek: ByteArray)


    /**
     * Returns whether the encrypted key encryption key (KEK) is available.
     *
     * @param entry KEK entry to test.
     * @return      Whether the KEK is available.
     */
    fun hasEncryptedKek(entry: KekEntry): Boolean

}
