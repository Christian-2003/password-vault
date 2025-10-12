package de.christian2003.passwordvault.application.usecases.packages

import de.christian2003.passwordvault.application.repository.PackagesRepository

class GetAllPackagesUseCase(
    private val repository: PackagesRepository
) {

    fun getInstalledPackages(): List<String> {
        return repository.getInstalledPackages()
    }

}
