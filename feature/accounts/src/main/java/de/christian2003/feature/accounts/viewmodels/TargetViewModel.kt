package de.christian2003.feature.accounts.viewmodels

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.ui.model.HelpCard
import de.christian2003.data.accounts.application.usecases.GetAllPackagesUseCase
import de.christian2003.data.accounts.application.usecases.GetLocalizedPackageNameUseCase
import de.christian2003.data.accounts.application.usecases.GetPackageIconUseCase
import de.christian2003.data.accounts.domain.services.PackageFingerprintService
import de.christian2003.data.accounts.domain.entities.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model for the sheet through which to select autofill targets for an account.
 *
 * @param application                       Application.
 * @param getPackageIconUseCase             Use case to get a list of all installed packages.
 * @param getLocalizedPackageNameUseCase    Use case to get the localized name for a package.
 * @param packageFingerprintService         Service to generate the fingerprint for a package.
 */
@HiltViewModel
class TargetViewModel @Inject constructor(
    application: Application,
    private val getAllPackagesUseCase: GetAllPackagesUseCase,
    private val getLocalizedPackageNameUseCase: GetLocalizedPackageNameUseCase,
    private val getPackageIconUseCase: GetPackageIconUseCase,
    private val packageFingerprintService: PackageFingerprintService
): AndroidViewModel(application) {

    /**
     * Targets that were selected when the init-function was called.
     */
    private var targetsAtInit: List<Target>? = null

    /**
     * List of all targets that are selected.
     */
    val targets: MutableList<Target> = mutableStateListOf()

    /**
     * List contains all installed Android packages. This is null if the packages have not been loaded.
     */
    var allInstalledPackages: List<String>? by mutableStateOf(null)

    /**
     * Indicates whether the dialog to select installed Android packages is visible.
     */
    var isSelectPackageDialogVisible: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the dialog to select a website is visible.
     */
    var isSelectWebsiteDialogVisible: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the dialog through which to confirm dismissing without saving is visible.
     */
    var isDiscardDialogVisible: Boolean by mutableStateOf(false)

    /**
     * Target that is currently being removed by the user.
     */
    var targetToRemove: Target? by mutableStateOf(null)

    /**
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.Targets.getVisible(application))
        private set


    /**
     * Initializes the view model.
     *
     * @param targets   Targets that are currently selected.
     */
    fun init(targets: List<Target>) {
        if (targetsAtInit == null) {
            this.targetsAtInit = targets
            this.targets.addAll(targets)
        }
    }


    /**
     * Determines whether changes were made to the selected targets.
     *
     * @return  Whether changes were made.
     */
    fun areChangesMade(): Boolean {
        targets.forEach { target ->
            if (targetsAtInit!!.find { it.url == target.url } == null) {
                return true //Target added
            }
        }
        targetsAtInit!!.forEach { target ->
            if (targets.find { it.url == target.url } == null) {
                return true //Target removed
            }
        }
        return false
    }


    /**
     * Begins loading all installed packages and stores them in "allInstalledPackages".
     */
    fun loadAllInstalledPackages() = viewModelScope.launch(Dispatchers.Default) {
        if (allInstalledPackages == null) {
            val allInstalledPackages: List<String> = getAllPackagesUseCase.getInstalledPackages()

            val sortedPackages: List<String> = allInstalledPackages.sortedBy { packageName ->
                getLocalizedPackageName(packageName)
            }

            this@TargetViewModel.allInstalledPackages = sortedPackages
        }
    }


    /**
     * Dismisses the dialog through which to select installed Android packages.
     *
     * @param selectedPackages  Set of selected packages to save or null to dismiss without saving
     *                          anything.
     */
    fun dismissSelectPackageDialog(selectedPackages: Set<String>? = null) {
        isSelectPackageDialogVisible = false
        if (selectedPackages != null) {
            viewModelScope.launch(Dispatchers.Default) {
                //Remove all Android targets that are not part of the selected packages:
                val targetsToRemove: MutableList<Target> = mutableListOf()
                targets.forEach { target ->
                    if (target.isAndroidApp() && !selectedPackages.contains(target.name)) {
                        targetsToRemove.add(target)
                    }
                }
                targetsToRemove.onEach { target ->
                    targets.remove(target)
                }

                //Add all Android targets from the selected packages that do not already exist:
                selectedPackages.forEach { selectedPackage ->
                    val target: Target? = targets.find { it.name == selectedPackage }
                    if (target == null) {
                        val newTarget: Target? = Target.createAndroidTarget(selectedPackage, packageFingerprintService)
                        if (newTarget != null) {
                            targets.add(newTarget)
                        }
                    }
                }
            }
        }
    }


    /**
     * Dismisses the dialog through which to select a website.
     *
     * @param url   URL of the website to save or null to dismiss without saving anything.
     */
    fun dismissSelectWebsiteDialog(url: String? = null) {
        isSelectWebsiteDialogVisible = false
        if (url != null) {
            val target: Target? = Target.createWebsiteTarget(url)
            if (target != null) {
                targets.add(target)
            }
        }
    }


    /**
     * Returns a set of all packages tat are selected currently.
     *
     * @return  Set containing all selected packages.
     */
    fun getAllSelectedPackages(): Set<String> {
        val selectedPackages: MutableSet<String> = mutableSetOf()
        targets.forEach { target ->
            if (target.isAndroidApp()) {
                selectedPackages.add(target.name)
            }
        }
        return selectedPackages
    }


    /**
     * Fetches the localized name for the specified Android package. This returns null if the
     * localized name cannot be fetched.
     *
     * @param packageName   Package for which to fetch the localized name.
     * @return              Localized name or null.
     */
    fun getLocalizedPackageName(packageName: String): String? {
        return getLocalizedPackageNameUseCase.getLocalizedPackageName(packageName)
    }


    /**
     * Fetches the icon for the specified Android package. Returns null if the icon cannot be fetched.
     *
     * @param packageName   Package for which to fetch the icon.
     * @return              Package icon or null.
     */
    fun getPackageIcon(packageName: String): Drawable? {
        return getPackageIconUseCase.getPackageIcon(packageName)
    }


    /**
     * Dismisses the dialog through which to confirm the removal of a target.
     *
     * @param targetToRemove    Target to remove or null to dismiss without removing.
     */
    fun dismissRemoveTargetDialog(targetToRemove: Target? = null) {
        this.targetToRemove = null
        if (targetToRemove != null) {
            targets.remove(targetToRemove)
        }
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        HelpCard.Targets.setVisible(application, false)
        isHelpCardVisible = false
    }

}
