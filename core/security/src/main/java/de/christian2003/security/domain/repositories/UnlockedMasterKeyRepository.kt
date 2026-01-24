package de.christian2003.security.domain.repositories


/**
 * Repository through which to access the unlocked master key.
 */
interface UnlockedMasterKeyRepository {

    /**
     * Returns whether the master key is unlocked.
     *
     * @return  Whether the master key is unlocked.
     */
    fun isMasterKeyUnlocked(): Boolean


    /**
     * Returns the bytes of the unlocked master key. If the master key is not unlocked, this
     * returns null.
     *
     * @return  Bytes of the unlocked master key or null.
     */
    fun getUnlockedMasterKeyBytes(): ByteArray?


    /**
     * Sets the bytes of the unlocked master key. This should only be called by use cases which
     * handle the unlocking of the master key.
     *
     * @param masterKeyBytes    Bytes of the master key.
     */
    fun setUnlockedMasterKeyBytes(masterKeyBytes: ByteArray)

}
