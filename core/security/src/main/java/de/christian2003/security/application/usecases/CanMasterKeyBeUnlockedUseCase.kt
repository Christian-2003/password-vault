package de.christian2003.security.application.usecases

import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import javax.inject.Inject


/**
 * Use case to test whether the master key can be unlocked.
 *
 * @param readonlyAuthRepository    Repository to access auth data.
 */
class CanMasterKeyBeUnlockedUseCase @Inject constructor(
    private val readonlyAuthRepository: ReadonlyAuthRepository,
) {

    /**
     * Tests whether the master key can be unlocked. If this method returns false, there are multiple causes:
     *  - Setup has not been performed.
     *  - Setup has been performed or is running currently, but changes were not yet committed.
     *
     * @return  Whether the master key can be unlocked.
     */
    fun canBeUnlocked(): Boolean {
        val masterPasswordKek: ByteArray? = readonlyAuthRepository.getMasterPasswordKek()
        val masterPasswordSalt: ByteArray? = readonlyAuthRepository.getMasterPasswordSalt()
        val masterKey: ByteArray? = readonlyAuthRepository.getEncryptedMasterKey()

        val canBeUnlocked: Boolean = masterPasswordKek != null && masterPasswordSalt != null && masterKey != null

        masterPasswordKek?.fill(0)
        masterPasswordSalt?.fill(0)
        masterKey?.fill(0)

        return canBeUnlocked
    }

}
