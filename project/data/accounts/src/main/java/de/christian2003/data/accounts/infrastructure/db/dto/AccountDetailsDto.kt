package de.christian2003.data.accounts.infrastructure.db.dto

import androidx.room.ColumnInfo
import kotlin.uuid.Uuid


/**
 * DTO maps an account ID to a detail ID. This is required for the pre query of accounts.
 *
 * @param account   ID of the account.
 * @param detail    ID of the detail.
 * @param url       URL of the autofill target.
 */
data class AccountDetailsDto(
    @ColumnInfo("accountId") val account: Uuid,
    @ColumnInfo("detailId") val detail: Uuid,
    @ColumnInfo("targetUrl") val url: String
)
