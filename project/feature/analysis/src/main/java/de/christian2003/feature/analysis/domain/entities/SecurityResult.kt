package de.christian2003.feature.analysis.domain.entities


/**
 * Result of the security analysis.
 *
 * @param passwordResults   List of results for each password.
 */
internal data class SecurityResult(
    val passwordResults: List<PasswordResult>
)
