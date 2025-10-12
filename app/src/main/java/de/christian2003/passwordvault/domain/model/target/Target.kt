package de.christian2003.passwordvault.domain.model.target

import android.net.Uri
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import kotlin.uuid.Uuid


/**
 * Domain value object models a target for the autofill system. Each entry can have multiple targets,
 * for which the app should autofill login data. A target describes which app or website should
 * receive autofill information.
 *
 * @param name          Name of the target. For Android apps, this is the package name
 *                      (e.g. "de.christian2003.passwordvault"). For websites, this is the domain name
 *                      (e.g. "passwordvault.christian2003.de").
 * @param url           URL for the target. For Android apps, this is of the following format:
 *                      "android://<certificate hash>@<package name>" (example:
 *                      "android://Y8IbhARlHA0OYfIi72WsJSkEeiYPlWiu-agbEF2_B0tzeO6rIq1wHRtlLcS-pfoPgOlfZitB9x0NeNbO88dBLQ==@de.christian2003.passwordvault/").
 *                      For websites, this is the URL which to autofill.
 *                      (e.g. "https://passwordvault.chrisitan2003.de/login").
 * @param id            ID for the target.
 * @param faviconFile   If the target is a website, this is the name of the file in which the website
 *                      favicon is stored in the app. Favicons are stored in the internal app storage
 *                      in "favicons/<name>.png". If the target is an Android app, this is null.
 */
data class Target(
    val name: String,
    val url: Uri,
    val id: Uuid = Uuid.random(),
    val faviconFile: String? = null
) {

    /**
     * Initializes the target.
     */
    init {
        require(id != Uuid.NIL) { "ID cannot be NIL" }
        require(name.isNotBlank()) { "Name cannot be blank" }
    }


    /**
     * Returns whether the target is an Android app.
     *
     * @return  Whether the target is an Android app.
     */
    fun isAndroidApp(): Boolean {
        return url.scheme?.toLowerCase(Locale.current) == "android"
    }

}
