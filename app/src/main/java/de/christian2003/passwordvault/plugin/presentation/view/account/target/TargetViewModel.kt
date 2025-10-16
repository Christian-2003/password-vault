package de.christian2003.passwordvault.plugin.presentation.view.account.target

import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.application.usecases.packages.CreateAndroidTargetService
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

    private lateinit var createAndroidTargetService: CreateAndroidTargetService

    private var isInitialized: Boolean = false

    val targets: MutableList<Target> = mutableStateListOf()

    var isSelectPackageDialogVisible: Boolean by mutableStateOf(false)

    var allInstalledPackages: List<String>? by mutableStateOf(null)


    fun init(
        targets: List<Target>,
        getAllPackagesUseCase: GetAllPackagesUseCase,
        getLocalizedPackageNameUseCase: GetLocalizedPackageNameUseCase,
        getPackageIconUseCase: GetPackageIconUseCase,
        createAndroidTargetService: CreateAndroidTargetService
    ) {
        if (isInitialized) {
            return
        }

        this.getAllPackagesUseCase = getAllPackagesUseCase
        this.getLocalizedPackageNameUseCase = getLocalizedPackageNameUseCase
        this.getPackageIconUseCase = getPackageIconUseCase
        this.createAndroidTargetService = createAndroidTargetService
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


    fun dismissSelectPackageDialog(selectedPackages: Set<String>? = null) {
        isSelectPackageDialogVisible = false
        if (selectedPackages != null) {
            val targetsToRemove: MutableList<Target> = mutableListOf()
            targets.forEach { target ->
                if (target.isAndroidApp()) {
                    targetsToRemove.add(target)
                }
            }
            targetsToRemove.onEach { target ->
                targets.remove(target)
            }

            selectedPackages.forEach { selectedPackage ->
                val target: Target? = targets.find { it.name == selectedPackage }
                if (target != null) {
                    targets.remove(target)
                }
                val newTarget: Target? = createAndroidTargetService.createAndroidTarget(selectedPackage)
                if (newTarget != null) {
                    targets.add(newTarget)
                }
            }
        }
    }


    fun getAllSelectedPackages(): Set<String> {
        val selectedPackages: MutableSet<String> = mutableSetOf()
        targets.forEach { target ->
            if (target.isAndroidApp()) {
                selectedPackages.add(target.name)
            }
        }
        return selectedPackages
    }


    fun getLocalizedPackageName(packageName: String): String? {
        return getLocalizedPackageNameUseCase.getLocalizedPackageName(packageName)
    }

    fun getPackageIcon(packageName: String): Drawable? {
        return getPackageIconUseCase.getPackageIcon(packageName)
    }

}
