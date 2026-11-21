package de.christian2003.feature.export.infrastructure.backup.v3.dto

import android.net.Uri
import de.christian2003.core.common.infrastructure.serializer.UriSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


/**
 * DTO for targets in V3 backups.
 *
 * @param name          Name of the target: Package name or domain name
 *                      (e.g. "de.christian2003.petrolindex" or "github.com").
 * @param url           URL for the target: Android package URL or website URL
 *                      (e.g. "android://..." or "https://github.com/login").
 * @param id            ID of the target.
 * @param faviconFile   File (inside the backup) of the favicon (if target is website, otherwise null).
 */
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
