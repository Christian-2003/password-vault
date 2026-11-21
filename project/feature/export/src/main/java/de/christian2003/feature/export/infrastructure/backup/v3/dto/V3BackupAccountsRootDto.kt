package de.christian2003.feature.export.infrastructure.backup.v3.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Root DTO for the V3 backup that contains all exported account information.
 *
 * @param accounts  List of accounts.
 * @param tags      List of all tags.
 */
@Serializable
internal data class V3BackupAccountsRootDto(

    @SerialName("accounts")
    val accounts: List<V3BackupAccountDto>,

    @SerialName("tags")
    val tags: List<V3BackupTagDto>

)
