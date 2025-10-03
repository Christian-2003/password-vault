package de.christian2003.passwordvault.domain.model.target

import android.webkit.URLUtil
import kotlin.uuid.Uuid


/**
 * Domain value object models a target for the autofill system. Each entry can have multiple targets,
 * for which the app should autofill login data. A target describes which app or website should
 * receive autofill information.
 *
 * @param id            ID for the target.
 * @param name          Name of the target. For Android apps, this is the package name
 *                      (e.g. "de.christian2003.passwordvault"). For websites, this is the domain name
 *                      (e.g. "passwordvault.christian2003.de").
 * @param url           URL for the target. For Android apps, this is of the following format:
 *                      "android://<certificate hash>@<package name>" (example:
 *                      "android://Y8IbhARlHA0OYfIi72WsJSkEeiYPlWiu-agbEF2_B0tzeO6rIq1wHRtlLcS-pfoPgOlfZitB9x0NeNbO88dBLQ==@de.christian2003.passwordvault/").
 *                      For websites, this is the URL which to autofill.
 *                      (e.g. "https://passwordvault.chrisitan2003.de/login").
 * @param faviconFile   If the target is a website, this is the name of the file in which the website
 *                      favicon is stored in the app. Favicons are stored in the internal app storage
 *                      in "favicons/<name>.png". If the target is an Android app, this is null.
 */
data class Target(
    val id: Uuid,
    val name: String,
    val url: String,
    val faviconFile: String?
) {

    /**
     * Initializes the target.
     */
    init {
        require(id != Uuid.NIL) { "ID cannot be NIL" }
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(URLUtil.isValidUrl(url)) { "Invalid URL" }
    }

}
