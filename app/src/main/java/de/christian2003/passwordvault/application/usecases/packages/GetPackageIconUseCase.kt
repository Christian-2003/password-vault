package de.christian2003.passwordvault.application.usecases.packages

import android.graphics.drawable.Drawable
import de.christian2003.passwordvault.application.repository.PackagesRepository

class GetPackageIconUseCase(
    private val repository: PackagesRepository
) {

    fun getPackageIcon(packageName: String): Drawable? {
        return repository.getIconForPackage(packageName)
    }

}
