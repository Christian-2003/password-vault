package de.christian2003.data.accounts.application.usecases

import android.graphics.drawable.Drawable
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.Target
import de.christian2003.data.accounts.domain.repositories.PackagesRepository
import javax.inject.Inject


/**
 * Use case to get the icon for an account.
 *
 * @param repository    Repository through which to access the packages.
 */
class GetAccountIconUseCase @Inject internal constructor(
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
