package de.christian2003.passwordvault.application.usecases.packages

import de.christian2003.passwordvault.application.repository.PackagesRepository

class GetLocalizedPackageNameUseCase(
    private val repository: PackagesRepository
) {

    fun getLocalizedPackageName(packageName: String): String? {
        return repository.getLocalizedNameForPackage(packageName)
    }

}
