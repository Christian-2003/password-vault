package de.christian2003.passwordvault.infrastructure.update.dto

import de.christian2003.core.common.infrastructure.serializer.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate


/**
 * DTO for the update REST API which contains information about the newest release.
 *
 * @param versionCode   Version code (e.g. 10) of the newest release.
 * @param versionName   Version name (e.g. "v1.3.0") of the newest release.
 * @param releaseDate   Date on which the version was released.
 * @param downloadUrl   URL to download the newest release.
 */
@Serializable
internal data class UpdateVersionRestDto(

    @SerialName("code")
    val versionCode: Long = -1,

    @SerialName("name")
    val versionName: String = "",

    @SerialName("date")
    @Serializable(with = LocalDateSerializer::class)
    val releaseDate: LocalDate = LocalDate.MIN,

    @SerialName("download")
    val downloadUrl: String = ""

)
