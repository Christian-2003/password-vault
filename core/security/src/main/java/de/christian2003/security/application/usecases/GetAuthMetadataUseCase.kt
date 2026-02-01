package de.christian2003.security.application.usecases

import de.christian2003.security.domain.entities.AuthMetadata
import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import javax.inject.Inject


/**
 * Use case to get the metadata for the authentication data.
 *
 * @param readonlyAuthRepository    Repository for auth data.
 */
class GetAuthMetadataUseCase @Inject internal constructor(
    private val readonlyAuthRepository: ReadonlyAuthRepository
) {

    /**
     * Returns the metadata for the auth data.
     *
     * @return  Metadata.
     */
    fun getMetadata(): AuthMetadata {
        val metadata = AuthMetadata(
            masterPasswordEditedAt = readonlyAuthRepository.getMasterPasswordTimestamp(),
            recoveryCodesEditedAt = readonlyAuthRepository.getRecoveryCodesTimestamp(),
            biometricsEditedAt = readonlyAuthRepository.getBiometricsTimestamp()
        )
        return metadata
    }

}
