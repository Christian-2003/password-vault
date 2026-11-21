package de.christian2003.passwordvault.domain.entities

import android.net.Uri


/**
 * In settings, a list is displayed that advertises other software developed by Christian-2003.
 * Each of these software is modeled by an instance of this value object.
 *
 * @param packageName   If the other software is an Android app, this stores the package name.
 *                      E.g. "de.christian2003.petrolindex"
 * @param websiteUrl    This stores the URL of the website which advertises the other software.
 *                      E.g. "https://christian2003.de/petrolindex"
 * @param softwareName  Display name of the other software, e.g. "Petrol Index".
 *                      Not localized because name should be identical accross all languages.
 * @param iconUrl       URL to the SVG displaying the icon of the other app.
 */
internal data class OtherSoftware(
    val packageName: String,
    val websiteUrl: Uri,
    val softwareName: String,
    val iconUrl: Uri
)
