package de.christian2003.security.infrastructure.repositories

import de.christian2003.security.domain.repositories.UnlockedMasterKeyRepository
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Implementation of the repository through which to access the unlocked master key.
 */
@Singleton
class UnlockedMasterKeyRepositoryImpl @Inject constructor(): UnlockedMasterKeyRepository {

    private var masterKeyBytes: ByteArray? = null


    /**
     * Returns whether the master key is unlocked.
     *
     * @return  Whether the master key is unlocked.
     */
    override fun isMasterKeyUnlocked(): Boolean {
        return masterKeyBytes != null
    }


    /**
     * Returns the bytes of the unlocked master key. If the master key is not unlocked, this
     * returns null.
     *
     * @return  Bytes of the unlocked master key or null.
     */
    override fun getUnlockedMasterKeyBytes(): ByteArray? {
        return masterKeyBytes
    }


    /**
     * Sets the bytes of the unlocked master key. This should only be called by use cases which
     * handle the unlocking of the master key.
     *
     * @param masterKeyBytes    Bytes of the master key.
     */
    override fun setUnlockedMasterKeyBytes(masterKeyBytes: ByteArray) {
        this.masterKeyBytes = masterKeyBytes
    }

}
