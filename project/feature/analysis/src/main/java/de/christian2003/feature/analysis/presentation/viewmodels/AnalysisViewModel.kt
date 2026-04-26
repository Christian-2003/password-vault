package de.christian2003.feature.analysis.presentation.viewmodels

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.ui.model.ColorGenerator
import de.christian2003.core.ui.model.HelpCard
import de.christian2003.data.accounts.application.usecases.GetAccountDescriptorByIdUseCase
import de.christian2003.data.accounts.application.usecases.GetAccountIconUseCase
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.feature.analysis.application.usecases.AnalyzePasswordsUseCase
import de.christian2003.feature.analysis.domain.entities.SecurityResult
import de.christian2003.feature.analysis.presentation.models.dialogs.AnalysisScreenDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * View model for the screen on which the password security analysis is displayed.
 *
 * @param application                       Application.
 * @param analyzePasswordsUseCase           Use case to analyze the password security.
 * @param getAccountDescriptorByIdUseCase   Use case to get account descriptors by their ID.
 * @param getAccountIconUseCase             Use case to query account icons.
 * @param colorGenerator                    Color generator.
 */
@HiltViewModel
internal class AnalysisViewModel @Inject constructor(
    application: Application,
    private val analyzePasswordsUseCase: AnalyzePasswordsUseCase,
    private val getAccountDescriptorByIdUseCase: GetAccountDescriptorByIdUseCase,
    private val getAccountIconUseCase: GetAccountIconUseCase,
    private val colorGenerator: ColorGenerator
): AndroidViewModel(application) {

    /**
     * Cache of the account descriptors that were already queried.
     */
    private val accountDescriptorsCache: MutableMap<Uuid, AccountDescriptor> = mutableMapOf()

    /**
     * Result of the analysis. This is null while the analysis is running.
     */
    var securityResult: SecurityResult? by mutableStateOf(null)
        private set

    /**
     * Dialog currently displayed on the screen.
     */
    var dialog: AnalysisScreenDialog by mutableStateOf(AnalysisScreenDialog.None)
        private set

    /**
     * Whether the help card on the sheet for weak passwords is visible.
     */
    var isWeakPasswordsHelpCardVisible: Boolean by mutableStateOf(HelpCard.AnalysisWeakPasswords.getVisible(application))
        private set

    /**
     * Whether the help card on the sheet for reused passwords is visible.
     */
    var isReusedPasswordsHelpCardVisible: Boolean by mutableStateOf(HelpCard.AnalysisWeakPasswords.getVisible(application))
        private set


    /**
     * Initializes the view model.
     */
    init {
        startAnalysis(true)
    }


    /**
     * Queries the account descriptor for the specified ID.
     *
     * @param accountId ID of the account whose descriptor to query.
     * @return          Account descriptor of the specified ID or null.
     */
    suspend fun queryAccountDescriptor(accountId: Uuid): AccountDescriptor? {
        if (accountDescriptorsCache.contains(accountId)) {
            return accountDescriptorsCache[accountId]
        }

        val accountDescriptor: AccountDescriptor? = getAccountDescriptorByIdUseCase.getAccountDescriptorById(accountId)
        if (accountDescriptor != null) {
            accountDescriptorsCache.put(accountId, accountDescriptor)
        }
        return accountDescriptor
    }


    /**
     * Queries the account icon for the specified account descriptor.
     *
     * @param accountDescriptor Descriptor for which to query the icon.
     * @return                  Account icon or null if no icon can be retrieved.
     */
    fun queryAccountIcon(accountDescriptor: AccountDescriptor): Drawable? {
        return getAccountIconUseCase.getAccountIcon(accountDescriptor)
    }


    /**
     * Generates a positive color.
     *
     * @param negativeColor Negative color to use as seed.
     * @param darkTheme     Whether the app is in dark mode.
     * @return              Positive color.
     */
    fun generatePositiveColor(negativeColor: Color, darkTheme: Boolean): Color {
        return colorGenerator.generatePositiveColorFromNegativeColor(negativeColor, darkTheme)
    }


    /**
     * Starts the analysis. The analysis will only be started if it is not running currently (unless
     * force = true is passed).
     *
     * @param force Whether to force start the analysis regardless of whether it is currently running.
     */
    fun startAnalysis(force: Boolean = false) {
        viewModelScope.launch(Dispatchers.Main) {
            if (securityResult != null || force) {
                securityResult = null
                securityResult = analyzePasswordsUseCase.analyzePasswords()
            }
        }
    }


    /**
     * Shows the sheet through which weak passwords are displayed.
     */
    fun showWeakPasswordsDialog() {
        dialog = AnalysisScreenDialog.WeakPasswords
    }

    /**
     * Dismisses the sheet through which weak passwords are displayed.
     */
    fun dismissWeakPasswordsDialog() {
        dialog = AnalysisScreenDialog.None
    }


    /**
     * Shows the sheet through which reused passwords are displayed.
     */
    fun showReusedPasswordsDialog() {
        dialog = AnalysisScreenDialog.ReusedPasswords
    }

    /**
     * Dismisses the sheet through which reused passwords are displayed.
     */
    fun dismissReusedPasswordsDialog() {
        dialog = AnalysisScreenDialog.None
    }


    /**
     * Shows the sheet through which all passwords are displayed.
     */
    fun showAllPasswordsDialog() {
        dialog = AnalysisScreenDialog.AllPasswords
    }

    /**
     * Dismisses the sheet through which all passwords are displayed.
     */
    fun dismissAllPasswordsDialog() {
        dialog = AnalysisScreenDialog.None
    }


    /**
     * Dismisses the help card displayed on the sheet for weak passwords.
     */
    fun dismissWeakPasswordsHelpCard() {
        isWeakPasswordsHelpCardVisible = false
        HelpCard.AnalysisWeakPasswords.setVisible(application, false)
    }

    /**
     * Dismisses the help card displayed on the sheet for reused passwords.
     */
    fun dismissReusedPasswordsHelpCard() {
        isReusedPasswordsHelpCardVisible = false
        HelpCard.AnalysisReusedPasswords.setVisible(application, false)
    }

}
