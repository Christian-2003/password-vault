package de.christian2003.passwordvault.infrastructure.legal.dto

import android.net.Uri
import androidx.core.net.toUri
import de.christian2003.core.common.infrastructure.serializer.LocaleSerializer
import de.christian2003.core.common.infrastructure.serializer.UriSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Locale


/**
 * DTO for a single legal page of the REST API.
 *
 * @param locale    Language of the page.
 * @param pageUrl   URL of the web page for the legal document.
 */
@Serializable
internal data class LegalPageItemRestDto(

    @SerialName("lang")
    @Serializable(with = LocaleSerializer::class)
    val locale: Locale = Locale.getDefault(),

    @SerialName("url")
    @Serializable(with = UriSerializer::class)
    val pageUrl: Uri = "about:blank".toUri()

)
