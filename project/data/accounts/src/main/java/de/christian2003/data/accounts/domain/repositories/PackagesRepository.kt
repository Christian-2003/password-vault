package de.christian2003.data.accounts.domain.repositories

import android.graphics.drawable.Drawable


/**
 * Repository through which to access the installed packages.
 */
internal interface PackagesRepository {

    /**
     * Returns a list of all installed packages.
     *
     * @return  List of all installed packages.
     */
    fun getInstalledPackages(): List<String>


    /**
     * Returns the localized name of a package. If no localized name can be retrieved, null is returned.
     *
     * @param packageName   Package for which to return the localized name.
     * @return              Localized package name.
     */
    fun getLocalizedNameForPackage(packageName: String): String?


    /**
     * Returns the icon for a package. If no icon can be retrieved, null is returned.
     *
     * @param packageName   Package for which to return the icon.
     * @return              Package icon or null.
     */
    fun getIconForPackage(packageName: String): Drawable?

}
