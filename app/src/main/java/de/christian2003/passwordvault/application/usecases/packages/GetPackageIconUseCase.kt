package de.christian2003.passwordvault.application.usecases.packages

import android.graphics.drawable.Drawable
import de.christian2003.passwordvault.application.repository.PackagesRepository
import javax.inject.Inject


/**
 * Use case to get the icon for an installed Android package.
 *
 * @param repository    Repository to access the installed packages.
 */
class GetPackageIconUseCase @Inject constructor(
    private val repository: PackagesRepository
) {

    /**
     * Returns the app icon for the specified package. If the package is not installed, null is
     * returned.
     *
     * @param packageName   Name of the package for which to return the icon.
     * @return              App icon for the specified package or null if the package is not installed.
     */
    fun getPackageIcon(packageName: String): Drawable? {
        return repository.getIconForPackage(packageName)
    }

}
