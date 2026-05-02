package de.christian2003.feature.export.infrastructure.backup.v3.dto

import de.christian2003.core.common.infrastructure.serializer.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


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
