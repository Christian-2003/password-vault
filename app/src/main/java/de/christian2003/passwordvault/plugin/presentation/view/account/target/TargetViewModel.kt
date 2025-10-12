package de.christian2003.passwordvault.plugin.presentation.view.account.target

import android.graphics.drawable.Drawable
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.application.usecases.packages.GetAllPackagesUseCase
import de.christian2003.passwordvault.application.usecases.packages.GetLocalizedPackageNameUseCase
import de.christian2003.passwordvault.application.usecases.packages.GetPackageIconUseCase
import de.christian2003.passwordvault.domain.model.target.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TargetViewModel: ViewModel() {

    private lateinit var getAllPackagesUseCase: GetAllPackagesUseCase

    private lateinit var getLocalizedPackageNameUseCase: GetLocalizedPackageNameUseCase

    private lateinit var getPackageIconUseCase: GetPackageIconUseCase

    private var isInitialized: Boolean = false

    val targets: MutableList<Target> = mutableStateListOf()

    var isSelectPackageDialogVisible: Boolean by mutableStateOf(false)

    var allInstalledPackages: List<String>? by mutableStateOf(null)


    fun init(
        targets: List<Target>,
        getAllPackagesUseCase: GetAllPackagesUseCase,
        getLocalizedPackageNameUseCase: GetLocalizedPackageNameUseCase,
        getPackageIconUseCase: GetPackageIconUseCase
    ) {
        if (isInitialized) {
            return
        }

        this.getAllPackagesUseCase = getAllPackagesUseCase
        this.getLocalizedPackageNameUseCase = getLocalizedPackageNameUseCase
        this.getPackageIconUseCase = getPackageIconUseCase
        this.targets.addAll(targets)
        isInitialized = true
    }


    fun loadAllInstalledPackages() = viewModelScope.launch(Dispatchers.Default) {
        if (allInstalledPackages == null) {
            val allInstalledPackages: List<String> = getAllPackagesUseCase.getInstalledPackages()

            val sortedPackages: List<String> = allInstalledPackages.sortedBy { packageName ->
                getLocalizedPackageName(packageName)
            }

            this@TargetViewModel.allInstalledPackages = sortedPackages
        }
    }


    fun dismissSelectPackageDialog(selectedPackages: List<String>? = null) {
        isSelectPackageDialogVisible = false
        if (selectedPackages != null) {
            //TODO: Save selected packages
        }
    }


    fun getLocalizedPackageName(packageName: String): String? {
        return getLocalizedPackageNameUseCase.getLocalizedPackageName(packageName)
    }

    fun getPackageIcon(packageName: String): Drawable? {
        return getPackageIconUseCase.getPackageIcon(packageName)
    }

}
