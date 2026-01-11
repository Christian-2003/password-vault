package de.christian2003.security.application.usecases

import de.christian2003.security.domain.repositories.CommitRepository
import de.christian2003.security.domain.repositories.MasterKeyRepository
import de.christian2003.security.domain.repositories.MasterPasswordRepository
import javax.inject.Inject


/**
 * Use case to test whether the master key can be unlocked.
 *
 * @param masterKeyRepository       Repository to access the master key.
 * @param masterPasswordRepository  Repository to access the master password.
 * @param commitRepository          Repository to check whether changes are staged for a commit.
 */
class CanMasterKeyBeUnlockedUseCase @Inject constructor(
    private val masterKeyRepository: MasterKeyRepository,
    private val masterPasswordRepository: MasterPasswordRepository,
    private val commitRepository: CommitRepository
) {

    /**
     * Tests whether the master key can be unlocked. If this method returns false, there are multiple causes:
     *  - Setup has not been performed.
     *  - Setup has been performed or is running currently, but changes were not yet committed.
     *
     * @return  Whether the master key can be unlocked.
     */
    fun canBeUnlocked(): Boolean {
        return masterKeyRepository.hasEncryptedMasterKey()
                && masterPasswordRepository.hasEncryptedMasterPasswordKek()
                && masterPasswordRepository.hasMasterPasswordSalt()
                && !commitRepository.areChangesStaged()
    }

}
