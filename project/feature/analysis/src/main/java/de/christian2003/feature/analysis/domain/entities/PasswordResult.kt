package de.christian2003.feature.analysis.domain.entities

import kotlin.uuid.Uuid


/**
 * Result for a single password.
 *
 * @param detailId      ID of the detail that was analyzed.
 * @param accountId     ID of the account of which the detail is a part.
 * @param securityScore Security score generated for the detail.
 * @param weaknesses    List of weaknesses for the analyzed detail.
 */
internal data class PasswordResult(
    val detailId: Uuid,
    val accountId: Uuid,
    val securityScore: Int,
    val weaknesses: List<SecurityCriteria>
)
