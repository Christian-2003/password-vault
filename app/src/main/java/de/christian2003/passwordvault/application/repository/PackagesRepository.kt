package de.christian2003.passwordvault.application.repository

import android.graphics.drawable.Drawable


interface PackagesRepository {

    fun getInstalledPackages(): List<String>

    fun getLocalizedNameForPackage(packageName: String): String?

    fun getIconForPackage(packageName: String): Drawable?

}
