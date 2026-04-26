package de.christian2003.feature.analysis.domain.entities

import kotlin.uuid.Uuid


/**
 * Result of the security analysis.
 *
 * @param allPasswordResults    List of all results for the analyzed passwords.
 * @param passwordResults       Password results mapped to a strength category.
 * @param reusedPasswords       Reused passwords mapped to the lists of accounts that use them.
 */
internal data class SecurityResult(
    val allPasswordResults: List<PasswordResult>,
    val passwordResults: Map<PasswordStrength, List<PasswordResult>>,
    val reusedPasswords: Map<String, List<Uuid>>
)
