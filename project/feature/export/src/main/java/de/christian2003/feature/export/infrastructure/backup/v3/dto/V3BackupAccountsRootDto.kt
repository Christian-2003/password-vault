package de.christian2003.feature.export.infrastructure.backup.v3.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class V3BackupAccountsRootDto(

    @SerialName("accounts")
    val accounts: List<V3BackupAccountDto>,

    @SerialName("tags")
    val tags: List<V3BackupTagDto>

)
