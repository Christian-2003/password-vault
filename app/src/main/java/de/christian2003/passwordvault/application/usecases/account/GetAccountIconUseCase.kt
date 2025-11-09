package de.christian2003.passwordvault.application.usecases.account

import android.graphics.drawable.Drawable
import de.christian2003.passwordvault.application.repository.PackagesRepository
import de.christian2003.passwordvault.domain.model.account.AccountDescriptor
import de.christian2003.passwordvault.domain.model.target.Target
import javax.inject.Inject


/**
 * Use case to get the icon for an account.
 *
 * @param repository    Repository through which to access the packages.
 */
class GetAccountIconUseCase @Inject constructor(
    private val repository: PackagesRepository
) {

    /**
     * Returns the icon for the specified account.
     *
     * @param account   Account for which to return the icon.
     * @return          Icon drawable.
     */
    fun getAccountIcon(account: AccountDescriptor): Drawable? {
        return getAccountIcon(account.targets)
    }


    /**
     * Returns the icon for the list of targets.
     *
     * @param targets   List of targets for which to return an icon.
     * @return          Icon drawable.
     */
    fun getAccountIcon(targets: List<Target>): Drawable? {
        targets.forEach { target ->
            if (target.isAndroidApp()) {
                val drawable: Drawable? = repository.getIconForPackage(target.name)
                if (drawable != null) {
                    return drawable
                }
            }
        }
        return null
    }

}
