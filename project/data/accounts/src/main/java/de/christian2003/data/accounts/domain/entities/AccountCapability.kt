package de.christian2003.data.accounts.domain.entities

import kotlin.uuid.Uuid


/**
 * Value object models the pre-query result for accounts. A pre-query result is a query that runs
 * on the database and returns data that does not need to be decrypted. This pre-query returns
 * information that can be utilized without requiring to unlock the master key.
 *
 * @param account   ID of the account.
 * @param details   List of the IDs of the details.
 * @param targetUrl URL of the target.
 */
data class AccountCapability(
    val account: Uuid,
    val details: List<Uuid>,
    val targetUrl: String
)
