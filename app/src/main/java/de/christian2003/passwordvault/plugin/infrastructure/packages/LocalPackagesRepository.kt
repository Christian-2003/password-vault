package de.christian2003.passwordvault.plugin.infrastructure.packages

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import de.christian2003.passwordvault.application.repository.PackagesRepository
import javax.inject.Inject


/**
 * Repository implementation to access the installed packages.
 *
 * @param packageManager    Android package manager.
 */
class LocalPackagesRepository @Inject constructor(
    private val packageManager: PackageManager
): PackagesRepository {

    /**
     * Cache for the list of installed packages.
     */
    private var installedPackagesCache: List<String>? = null

    /**
     * Cache for the ApplicationInfo instances of installed packages.
     */
    private val applicationInfoCache: MutableMap<String, ApplicationInfo> = hashMapOf()

    /**
     * Cache for the localized package names.
     */
    private val localizedPackageNamesCache: MutableMap<String, String> = hashMapOf()

    /**
     * Cache for the package icons.
     */
    private val packageIconsCache: MutableMap<String, Drawable> = hashMapOf()


    /**
     * Returns a list of all installed packages.
     *
     * @return  List of all installed packages.
     */
    override fun getInstalledPackages(): List<String> {
        if (installedPackagesCache == null) {
            if (applicationInfoCache.isEmpty()) {
                loadAllInstalledPackages()
            }

            val installedPackages: MutableList<String> = mutableListOf()
            applicationInfoCache.values.forEach { applicationInfo ->
                installedPackages.add(applicationInfo.packageName)
            }

            installedPackagesCache = installedPackages
        }
        return installedPackagesCache!!
    }


    /**
     * Returns the localized name of a package. If no localized name can be retrieved, null is returned.
     *
     * @param packageName   Package for which to return the localized name.
     * @return              Localized package name.
     */
    override fun getLocalizedNameForPackage(packageName: String): String? {
        if (localizedPackageNamesCache.containsKey(packageName)) {
            return localizedPackageNamesCache[packageName]
        }

        //Localized package name not cached:
        if (applicationInfoCache.isEmpty()) {
            loadAllInstalledPackages()
        }
        if (applicationInfoCache.containsKey(packageName)) {
            val applicationInfo: ApplicationInfo = applicationInfoCache[packageName]!!

            val localizedName: String = if (applicationInfo.nonLocalizedLabel != null) {
                applicationInfo.nonLocalizedLabel.toString()
            } else {
                try {
                    applicationInfo.loadLabel(packageManager).toString()
                } catch (_: Exception) {
                    packageName //Package name (e.g. "de.christian2003.passwordvault") is fallback
                }
            }

            localizedPackageNamesCache.put(packageName, localizedName)
            return localizedName
        }
        else {
            //Package not installed:
            return null
        }
    }


    /**
     * Returns the icon for a package. If no icon can be retrieved, null is returned.
     *
     * @param packageName   Package for which to return the icon.
     * @return              Package icon or null.
     */
    override fun getIconForPackage(packageName: String): Drawable? {
        if (packageIconsCache.containsKey(packageName)) {
            return packageIconsCache[packageName]
        }

        //Package icon not cached:
        if (applicationInfoCache.isEmpty()) {
            loadAllInstalledPackages()
        }
        if (applicationInfoCache.containsKey(packageName)) {
            val applicationInfo: ApplicationInfo = applicationInfoCache[packageName]!!

            val packageIcon: Drawable? = try {
                packageManager.getApplicationIcon(applicationInfo)
            } catch (_: Exception) {
                null
            }

            if (packageIcon != null) {
                packageIconsCache.put(packageName, packageIcon)
            }
            return packageIcon
        }
        else {
            //Package not installed:
            return null
        }
    }


    /**
     * Loads all installed packages and stores their ApplicationInfo instances in "applicationInfoCache".
     */
    private fun loadAllInstalledPackages() {
        try {
            val applicationInfos: List<ApplicationInfo> = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            applicationInfos.forEach { applicationInfo ->
                if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                    applicationInfoCache.put(applicationInfo.packageName, applicationInfo)
                }
            }
        }
        catch (_: Exception) { }
    }

}
