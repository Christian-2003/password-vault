package de.christian2003.passwordvault.domain.entities

import android.net.Uri
import java.time.LocalDate


/**
 * Value objects models a legal page (e.g. "Privacy Policy" or "Terms of Service").
 *
 * @param valid     Date at which the legal document takes effect.
 * @param version   Version of the legal document.
 * @param pageUrl   URL to the web page showing the legal document.
 */
internal data class LegalPage(
    val valid: LocalDate,
    val version: Int,
    val pageUrl: Uri
)
