package de.christian2003.data.accounts.domain.entities

import android.net.Uri
import android.util.Base64
import android.webkit.URLUtil
import androidx.core.net.toUri
import de.christian2003.data.accounts.domain.services.PackageFingerprintService
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
        return url.scheme?.lowercase() == "android"
    }


    companion object {

        /**
         * Helper function creates a target for an Android app. If the target cannot be created (for
         * example because the Android app is not installed), null is returned.
         *
         * @param packageName               Package name of the Android app for which to create the
         *                                  target.
         * @param packageFingerprintService Service through which to get the fingerprint of an
         *                                  installed Android package.
         * @return                          Android app target or null.
         */
        fun createAndroidTarget(
            packageName: String,
            packageFingerprintService: PackageFingerprintService
        ): Target? {
            val fingerprint: ByteArray? = packageFingerprintService.getPackageFingerprint(packageName)
            if (fingerprint == null) {
                return null
            }
            val fingerprintBase64: String = Base64.encodeToString(fingerprint, Base64.DEFAULT)

            val url: Uri = Uri.Builder()
                .scheme("android")
                .encodedAuthority("$fingerprintBase64@$packageName")
                .path("/")
                .build()

            return Target(
                name = packageName,
                url = url
            )
        }


        /**
         * Helper function creates a target for a website. If the target cannot be created (for
         * example because the URL is invalid), null is returned.
         *
         * @param url   URL of the website for which to create the target.
         * @return      Website target or null.
         */
        fun createWebsiteTarget(url: String): Target? {
            if (!URLUtil.isValidUrl(url)) {
                return null
            }

            val parsedUrl: Uri = url.toUri()
            val name: String? = parsedUrl.host

            if (name == null) {
                return null
            }

            return Target(
                name = name,
                url = parsedUrl
            )
        }

    }

}
