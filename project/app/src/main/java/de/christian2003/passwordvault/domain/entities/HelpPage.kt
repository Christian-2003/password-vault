package de.christian2003.passwordvault.domain.entities

import android.net.Uri
import java.util.Locale


/**
 * Value object models a single help page.
 *
 * @param title             Title of the help page.
 * @param description       Description of the help page.
 * @param pageUrl           URL of the website where the help page can be viewed.
 * @param previewImageUrl   URL of a preview image for the help page.
 * @param lang              Language of the help page.
 * @param isInCurrentLang   Whether the page is in the current language.
 */
internal data class HelpPage(
    val title: String,
    val description: String,
    val pageUrl: Uri,
    val previewImageUrl: Uri,
    val lang: Locale,
    val isInCurrentLang: Boolean
)
