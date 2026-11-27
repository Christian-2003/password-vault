package de.christian2003.feature.analysis.domain.entities

import java.time.LocalDateTime


/**
 * Value object models a brief overview for a security result that can be stored for later use.
 *
 * @param timestamp             Timestamp of the analysis.
 * @param weakPasswordCount     Number of weak passwords.
 * @param reusedPasswordCount   Number of reused passwords.
 * @param analyzedAccountsCount Total number of analyzed accounts.
 */
internal data class SecurityResultOverview(
    val timestamp: LocalDateTime,
    val weakPasswordCount: Int,
    val reusedPasswordCount: Int,
    val analyzedAccountsCount: Int
)
