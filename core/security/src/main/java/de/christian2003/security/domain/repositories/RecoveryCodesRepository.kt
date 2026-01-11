package de.christian2003.security.domain.repositories


/**
 * Repository used to access the encrypted key encryption key (KEK) items for recovery codes.
 */
interface RecoveryCodesRepository {

    /**
     * Returns the encrypted key encryption key (KEK) item for the specified index. If no KEK item
     * exists for the specified index, null is returned.
     *
     * @param index Index of the KEK item to return.
     * @return      KEK item for the specified index or null.
     */
    fun getEncryptedKekItem(index: Int): ByteArray?


    /**
     * Tests whether an encrypted key encryption key (KEK) item exists for the specified index.
     *
     * @param index Index for which to test whether an item exists.
     */
    fun hasEncryptedKekItem(index: Int): Boolean


    /**
     * Sets the encrypted key encryption key (KEK) item for the specified index.
     *
     * @param index         Index of the KEK item to set.
     * @param encryptedKek  New encrypted KEK to set.
     */
    fun setEncryptedKekItem(index: Int, encryptedKek: ByteArray)


    /**
     * Returns the number of encrypted key encryption key (KEK) items stored for recovery codes.
     *
     * @return  Number of KEK items.
     */
    fun getNumberOfEncryptedKekItems(): Int


    /**
     * Returns the maximum number of encrypted key encryption key (KEK) items that can be stored in
     * the repository. If the recovery codes are set, this should be identical to
     * 'getNumberOfEncryptedKekItems()'.
     * If recovery codes are not set, this is the number of KEK items that should be created when
     * setting up recovery codes.
     *
     * @return  Max number of KEK items.
     */
    fun getMaxNumberOfEncryptedKekItems(): Int


    /**
     * Deletes all encrypted key encryption key (KEK) items stored for recovery codes.
     */
    fun deleteAllEncryptedKekItems()

}
