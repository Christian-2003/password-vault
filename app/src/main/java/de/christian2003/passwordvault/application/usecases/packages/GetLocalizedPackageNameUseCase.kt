package de.christian2003.passwordvault.application.usecases.packages

import de.christian2003.passwordvault.application.repository.PackagesRepository
import javax.inject.Inject


/**
 * Use case to get the localized display name from an Android package name.
 *
 * @param repository    Repository to access the installed packages.
 */
class GetLocalizedPackageNameUseCase @Inject constructor(
    private val repository: PackagesRepository
) {

    /**
     * Returns the localized display name from the specified package name.
     *
     * @param packageName   Name of the package whose localized display name to return.
     * @return              Localized display name for the specified package.
     */
    fun getLocalizedPackageName(packageName: String): String? {
        return repository.getLocalizedNameForPackage(packageName)
    }

}
