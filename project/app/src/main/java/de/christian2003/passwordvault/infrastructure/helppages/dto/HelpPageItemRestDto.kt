package de.christian2003.passwordvault.infrastructure.helppages.dto

import android.net.Uri
import androidx.core.net.toUri
import de.christian2003.core.common.infrastructure.serializer.LocaleSerializer
import de.christian2003.core.common.infrastructure.serializer.UriSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Locale


/**
 * DTO for a help page item of the help pages REST API.
 *
 * @param locale        Locale of the item.
 * @param title         Title of the item.
 * @param description   Description of the item.
 * @param url           URL for the web page of the item.
 */
@Serializable
internal data class HelpPageItemRestDto(

    @SerialName("lang")
    @Serializable(with = LocaleSerializer::class)
    val locale: Locale = Locale.getDefault(),

    @SerialName("title")
    val title: String = "",

    @SerialName("description")
    val description: String = "",

    @SerialName("url")
    @Serializable(with = UriSerializer::class)
    val url: Uri = "about:blank".toUri()

)
