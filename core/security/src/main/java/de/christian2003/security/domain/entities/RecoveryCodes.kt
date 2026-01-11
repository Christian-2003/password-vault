package de.christian2003.security.domain.entities


/**
 * Value object is the result from the recovery code generation. It contains a list of recovery
 * codes that can be used to restore the master password.
 *
 * @param codes List of recovery codes that can be used to restore the master password.
 */
data class RecoveryCodes(
    val codes: List<CharArray>
)
