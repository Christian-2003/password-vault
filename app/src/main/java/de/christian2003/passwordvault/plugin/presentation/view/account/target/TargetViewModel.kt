package de.christian2003.passwordvault.plugin.presentation.view.account.target

import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.application.usecases.packages.GetAllPackagesUseCase
import de.christian2003.passwordvault.application.usecases.packages.GetLocalizedPackageNameUseCase
import de.christian2003.passwordvault.application.usecases.packages.GetPackageIconUseCase
import de.christian2003.passwordvault.domain.model.target.PackageFingerprintService
import de.christian2003.passwordvault.domain.model.target.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * View model for the sheet through which to select autofill targets for an account.
 */
class TargetViewModel: ViewModel() {

    /**
     * Use case to get a list of all installed Android packages.
     */
    private lateinit var getAllPackagesUseCase: GetAllPackagesUseCase

    /**
     * Use case to get the localized name for an installed Android package.
     */
    private lateinit var getLocalizedPackageNameUseCase: GetLocalizedPackageNameUseCase

    /**
     * Use case to get the icon for an installed Android package.
     */
    private lateinit var getPackageIconUseCase: GetPackageIconUseCase

    /**
     * Service to get the fingerprint for an installed Android package.
     */
    private lateinit var packageFingerprintService: PackageFingerprintService

    /**
     * Indicates whether the view model has been initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * List of all targets that are selected.
     */
    val targets: MutableList<Target> = mutableStateListOf()

    /**
     * List contains all installed Android packages. This is null if the packages have not been loaded.
     */
    var allInstalledPackages: List<String>? by mutableStateOf(null)

    /**
     * Indicates whether the dialog to select installed Android packges is visible.
     */
    var isSelectPackageDialogVisible: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the dialog to select a website is visible.
     */
    var isSelectWebsiteDialogVisible: Boolean by mutableStateOf(false)


    /**
     * Initializes the view model.
     *
     * @param targets                           Targets that are currently selected.
     * @param getAllPackagesUseCase             Use case to get a list of all installed Android
     *                                          packages.
     * @param getLocalizedPackageNameUseCase    Use case to get the localized name for an installed
     *                                          Android package.
     * @param getPackageIconUseCase             Use case to get the icon for an installed Android
     *                                          package.
     * @param packageFingerprintService         Service to get the fingerprint for an installed
     *                                          Android package.
     */
    fun init(
        targets: List<Target>,
        getAllPackagesUseCase: GetAllPackagesUseCase,
        getLocalizedPackageNameUseCase: GetLocalizedPackageNameUseCase,
        getPackageIconUseCase: GetPackageIconUseCase,
        packageFingerprintService: PackageFingerprintService
    ) {
        if (isInitialized) {
            return
        }

        this.getAllPackagesUseCase = getAllPackagesUseCase
        this.getLocalizedPackageNameUseCase = getLocalizedPackageNameUseCase
        this.getPackageIconUseCase = getPackageIconUseCase
        this.packageFingerprintService = packageFingerprintService
        this.targets.addAll(targets)
        isInitialized = true
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

}
