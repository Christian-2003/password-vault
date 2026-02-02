package de.christian2003.security.application.usecases

import de.christian2003.security.domain.entities.AuthMetadata
import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDateTime
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
    fun getMetadata(): Flow<AuthMetadata> {
        val masterPasswordTimestampFlow: Flow<LocalDateTime?> = readonlyAuthRepository.getMasterPasswordTimestamp()
        val recoveryCodesTimestampFlow: Flow<LocalDateTime?> = readonlyAuthRepository.getRecoveryCodesTimestamp()
        val biometricsTimestampFlow: Flow<LocalDateTime?> = readonlyAuthRepository.getBiometricsTimestamp()

        val metadataFlow: Flow<AuthMetadata> = combine(
            flow = masterPasswordTimestampFlow,
            flow2 = recoveryCodesTimestampFlow,
            flow3 = biometricsTimestampFlow
        ) { masterPasswordEditedAt, recoveryCodesEditedAt, biometricsEditedAt ->
            AuthMetadata(
                masterPasswordEditedAt = masterPasswordEditedAt,
                recoveryCodesEditedAt = recoveryCodesEditedAt,
                biometricsEditedAt = biometricsEditedAt
            )
        }

        return metadataFlow
    }

}
