package de.christian2003.feature.export.infrastructure.backup.v3.dto

import de.christian2003.core.common.infrastructure.serializer.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


/**
 * DTO for the exported metadata of a V3 backup.
 *
 * @param backupVersion Version code for the backup. Whenever changes to the backup file are made,
 *                              this is incremented.
 * @param deviceName            Device name where the backup was created (e.g. "A72 by Christian-2003").
 * @param appVersionName        Version name of the app version where the backup was created (e.g. "v3.8.0").
 * @param appVersionCode        Version code of the app version where the backup was created (e.g. 45).
 * @param createdAt             When the backup was created.
 * @param includedAccountsCount Number of accounts included in the backup.
 * @param includedFilesCount    Number of files (from storage) included in the backup. Currently
 *                              unused until file-support is implemented.
 */
@Serializable
internal data class V3BackupMetadataDto(

    @SerialName("version")
    val backupVersion: Int = 1,

    @SerialName("deviceName")
    val deviceName: String,

    @SerialName("appVersionName")
    val appVersionName: String,

    @SerialName("appVersionCode")
    val appVersionCode: Int,

    @SerialName("createdAt")
    @Serializable(LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.MIN,

    @SerialName("includedAccounts")
    val includedAccountsCount: Int,

    @SerialName("includedFiles")
    val includedFilesCount: Int,

)
