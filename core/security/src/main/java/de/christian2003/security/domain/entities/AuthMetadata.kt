package de.christian2003.security.domain.entities

import java.time.LocalDateTime


/**
 * Value object stores the metadata for the authentication. If it is unknown at which time some auth
 * data was edited, null is provided.
 *
 * @param masterPasswordEditedAt    Timestamp at which the master password was edited the last time.
 * @param recoveryCodesEditedAt     Timestamp at which the recovery codes were edited the last time.
 * @param biometricsEditedAt        Timestamp at which the biometrics were edited the last time.
 */
data class AuthMetadata(
    val masterPasswordEditedAt: LocalDateTime?,
    val recoveryCodesEditedAt: LocalDateTime?,
    val biometricsEditedAt: LocalDateTime?
)
