package de.christian2003.feature.accounts.viewmodels

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.common.application.services.DateTimeFormatterService
import de.christian2003.core.ui.model.ColorGenerator
import de.christian2003.core.ui.model.HelpCard
import de.christian2003.data.accounts.application.usecases.GetAllPackagesUseCase
import de.christian2003.data.accounts.application.usecases.GetLocalizedPackageNameUseCase
import de.christian2003.data.accounts.application.usecases.GetPackageIconUseCase
import de.christian2003.data.accounts.application.usecases.GetTargetSigningCertificateUseCase
import de.christian2003.data.accounts.application.usecases.ValidatePackageSignatureUseCase
import de.christian2003.data.accounts.domain.services.PackageFingerprintService
import de.christian2003.data.accounts.domain.entities.Target
import de.christian2003.feature.accounts.models.dialogs.TargetSheetDialog
import de.christian2003.feature.accounts.models.states.TargetSheetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.cert.X509Certificate
import java.time.LocalDate
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * View model for the sheet through which to select autofill targets for an account.
 *
 * @param application                           Application.
 * @param getPackageIconUseCase                 Use case to get a list of all installed packages.
 * @param getLocalizedPackageNameUseCase        Use case to get the localized name for a package.
 * @param validatePackageAgainstTargetUseCase   Use case to validate an installed package against an
 *                                              autofill target.
 * @param getTargetSigningCertificateUseCase    Use case to get the signing certificate of a target.
 * @param packageFingerprintService             Service to generate the fingerprint for a package.
 * @param dateTimeFormatterService              Service to format dates and times.
 * @param colorGenerator                        Color generator.
 */
@HiltViewModel
internal class TargetViewModel @Inject constructor(
    application: Application,
    private val getAllPackagesUseCase: GetAllPackagesUseCase,
    private val getLocalizedPackageNameUseCase: GetLocalizedPackageNameUseCase,
    private val getPackageIconUseCase: GetPackageIconUseCase,
    private val validatePackageAgainstTargetUseCase: ValidatePackageSignatureUseCase,
    private val getTargetSigningCertificateUseCase: GetTargetSigningCertificateUseCase,
    private val packageFingerprintService: PackageFingerprintService,
    private val dateTimeFormatterService: DateTimeFormatterService,
    private val colorGenerator: ColorGenerator
): AndroidViewModel(application) {

    /**
     * Targets that were selected when the init-function was called.
     */
    private var targetsAtInit: List<Target>? = null

    /**
     * Stores the IDs of invalid targets. A target is invalid if the installed package's singing
     * certificates do not match the fingerprint stored in the autofill target.
     */
    private val invalidTargets: State<Set<Uuid>> = derivedStateOf {
        val invalidTargetIds: MutableSet<Uuid> = mutableSetOf()
        targets.forEach { target ->
            if (target.isAndroidApp()) {
                val isValid: Boolean = validatePackageAgainstTargetUseCase.validate(target.name, target)
                if (!isValid) {
                    invalidTargetIds.add(target.id)
                }
            }
        }
        return@derivedStateOf invalidTargetIds
    }

    /**
     * List of all targets that are selected.
     */
    val targets: MutableList<Target> = mutableStateListOf()

    /**
     * List contains all installed Android packages. This is null if the packages have not been loaded.
     */
    var allInstalledPackages: List<String>? by mutableStateOf(null)

    /**
     * Target that is currently being deleted or edited using a dialog. This can be null if no
     * dialog is currently using this dialog.
     */
    var targetForDialog: Target? by mutableStateOf(null)
        private set

    /**
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.Targets.getVisible(application))
        private set

    /**
     * If dialog == TargetSheetDialog.CertificateDetails, this is the certificate whose details to
     * display. Otherwise this is null.
     */
    var certificateToDisplay: X509Certificate? = null
        private set

    /**
     * Dialogs for the sheet.
     */
    var dialog: TargetSheetDialog by mutableStateOf(TargetSheetDialog.None)
        private set

    /**
     * Current state of the sheet.
     */
    var state: TargetSheetState by mutableStateOf(TargetSheetState.Default)
        private set

    /**
     * Targets that are currently selected by the user (e.g. in multiselect state).
     */
    val selectedTargets: MutableSet<Uuid> = mutableStateSetOf()


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


    fun startMultiselectState(target: Target) {
        selectedTargets.clear()
        selectedTargets.add(target.id)
        state = TargetSheetState.Multiselect
    }

    fun dismissMultiselectState() {
        state = TargetSheetState.Default
        selectedTargets.clear()
    }

    fun selectAllTargets() {
        targets.forEach { target ->
            selectedTargets.add(target.id)
        }
    }


    /**
     * Returns whether the specified target is valid.
     *
     * @param target    Target.
     * @return          Whether the specified target is valid.
     */
    fun isTargetValid(target: Target): Boolean {
        return !invalidTargets.value.contains(target.id)
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


    fun generatePositiveColor(negativeColor: Color, darkTheme: Boolean): Color {
        return colorGenerator.generatePositiveColorFromNegativeColor(negativeColor, darkTheme)
    }


    fun formatDate(date: LocalDate): String {
        return dateTimeFormatterService.format(date)
    }


    fun showSelectPackageDialog() {
        dialog = TargetSheetDialog.SelectPackage
    }


    /**
     * Dismisses the dialog through which to select installed Android packages.
     *
     * @param selectedPackages  Set of selected packages to save or null to dismiss without saving
     *                          anything.
     */
    fun dismissSelectPackageDialog(selectedPackages: Set<String>? = null) {
        dialog = TargetSheetDialog.None
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


    fun showSelectWebsiteDialog() {
        dialog = TargetSheetDialog.SelectWebsite
    }


    /**
     * Dismisses the dialog through which to select a website.
     *
     * @param url   URL of the website to save or null to dismiss without saving anything.
     */
    fun dismissSelectWebsiteDialog(url: String? = null) {
        dialog = TargetSheetDialog.None
        if (url != null) {
            val target: Target? = Target.createWebsiteTarget(url)
            if (target != null) {
                targets.add(target)
            }
        }
    }


    fun showEditWebsiteDialog(target: Target) {
        if (!target.isAndroidApp()) {
            this.targetForDialog = target
            dialog = TargetSheetDialog.EditWebsite
        }
    }


    fun dismissEditWebsiteDialog(url: String? = null) {
        dialog = TargetSheetDialog.None
        val target: Target? = targetForDialog
        targetForDialog = null
        if (url != null && target != null) {
            val editedTarget: Target = target.copyWithNewUrl(url)
            val index = targets.indexOf(target)
            if (index >= 0 && index < targets.size) {
                targets[index] = editedTarget
            }
        }
    }


    fun showConfirmRemoveTargetDialog(target: Target) {
        targetForDialog = target
        dialog = TargetSheetDialog.ConfirmRemoveTarget
    }

    fun showConfirmRemoveTargetDialog() {
        //Do not set target - When this is called, sheet is in multiselect state!
        dialog = TargetSheetDialog.ConfirmRemoveTarget
    }


    fun dismissConfirmRemoveTargetDialog(remove: Boolean = false) {
        dialog = TargetSheetDialog.None
        val targetToRemove: Target? = targetForDialog
        targetForDialog = null
        if (remove) {
            if (targetToRemove != null) {
                targets.remove(targetToRemove)
            }
            else if (selectedTargets.isNotEmpty()) {
                selectedTargets.forEach { id ->
                    val t: Target? = targets.firstOrNull() { target -> target.id == id }
                    if (t != null) {
                        targets.remove(t)
                    }
                }
                selectedTargets.clear()
                dismissMultiselectState()
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
     * Shows the dialog through which the certificate details are displayed for the signing certificate
     * of the specified target.
     *
     * @param target    Target whose certificate to display.
     */
    fun showCertificateDetailsDialog(target: Target) {
        val certificate: X509Certificate? = getTargetSigningCertificateUseCase.getSigningCertificate(target)
        if (certificate != null) {
            certificateToDisplay = certificate
            dialog = TargetSheetDialog.CertificateDetails
        }
    }


    /**
     * Dismisses the dialog through which the certificate details are displayed.
     */
    fun dismissCertificateDetailsDialog() {
        dialog = TargetSheetDialog.None
        certificateToDisplay = null
    }


    fun showDiscardChangesDialog() {
        dialog = TargetSheetDialog.DiscardChanges
    }


    fun dismissDiscardChangesDialog() {
        dialog = TargetSheetDialog.None
    }


    fun showCertificatesDoNotMatchDialog() {
        dialog = TargetSheetDialog.CertificatesDoNotMatch
    }

    fun dismissCertificatesDoNotMatchDialog() {
        dialog = TargetSheetDialog.None
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        HelpCard.Targets.setVisible(application, false)
        isHelpCardVisible = false
    }

}
