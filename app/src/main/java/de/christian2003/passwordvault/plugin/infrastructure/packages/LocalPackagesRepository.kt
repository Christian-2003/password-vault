package de.christian2003.passwordvault.plugin.infrastructure.packages

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import de.christian2003.passwordvault.application.repository.PackagesRepository


class LocalPackagesRepository(
    private val packageManager: PackageManager
): PackagesRepository {

    private var installedPackagesCache: List<String>? = null

    private val applicationInfoCache: MutableMap<String, ApplicationInfo> = hashMapOf()

    private val localizedPackageNamesCache: MutableMap<String, String> = hashMapOf()

    private val packageIconsCache: MutableMap<String, Drawable> = hashMapOf()


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



    private fun loadAllInstalledPackages() {
        try {
            val applicationInfos: List<ApplicationInfo> = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            applicationInfos.forEach { applicationInfo ->
                if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                    applicationInfoCache.put(applicationInfo.packageName, applicationInfo)
                    Log.d("Packages", "Loaded package ${applicationInfo.packageName}")
                }
            }
        }
        catch (e: Exception) {
            Log.e("Packages", e.message ?: "Unknown error")
        }
    }

}
