package de.christian2003.feature.export.infrastructure.backup.v3.dto

import android.net.Uri
import de.christian2003.core.common.infrastructure.serializer.UriSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Serializable
internal data class V3BackupTargetDto(

    @SerialName("name")
    val name: String,

    @SerialName("url")
    @Serializable(UriSerializer::class)
    val url: Uri,

    @SerialName("id")
    val id: Uuid,

    @SerialName("favicon")
    val faviconFile: String? = null

)
