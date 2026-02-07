package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.repositories.PackagesRepository
import javax.inject.Inject


/**
 * Use case to get a list of all installed packages.
 *
 * @param repository    Repository to access installed packages.
 */
class GetAllPackagesUseCase @Inject internal constructor(
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
