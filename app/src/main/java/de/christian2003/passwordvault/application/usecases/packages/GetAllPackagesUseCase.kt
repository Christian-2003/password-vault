package de.christian2003.passwordvault.application.usecases.packages

import de.christian2003.passwordvault.application.repository.PackagesRepository
import javax.inject.Inject


/**
 * Use case to get a list of all installed packages.
 *
 * @param repository    Repository to access installed packages.
 */
class GetAllPackagesUseCase @Inject constructor(
    private val repository: PackagesRepository
) {

    /**
     * Returns a list that contains the names of all installed packages.
     *
     * @return  List of the installed packages.
     */
    fun getInstalledPackages(): List<String> {
        return repository.getInstalledPackages()
    }

}
