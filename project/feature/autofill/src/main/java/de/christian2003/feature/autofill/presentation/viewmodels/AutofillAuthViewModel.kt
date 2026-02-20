package de.christian2003.feature.autofill.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.service.autofill.Dataset
import android.service.autofill.Field
import android.service.autofill.FillResponse
import android.service.autofill.Presentations
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.security.application.usecases.AreBiometricsConfiguredUseCase
import de.christian2003.core.security.application.usecases.UnlockWithMasterPasswordUseCase
import de.christian2003.core.ui.theme.ThemeContrast
import de.christian2003.data.accounts.domain.entities.AccountCapability
import de.christian2003.feature.autofill.R
import de.christian2003.feature.autofill.application.usecases.FetchAutofillDataUseCase
import de.christian2003.feature.autofill.domain.entities.AutofillItem
import de.christian2003.feature.autofill.domain.entities.AutofillPartition
import de.christian2003.feature.autofill.domain.entities.AutofillResponse
import de.christian2003.feature.autofill.domain.entities.AutofillType
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * View model for the activity through which the user can authenticate before any other app is
 * auto-filled.
 *
 * @param application                       Application.
 * @param areBiometricsConfiguredUseCase    Use case to get whether biometrics are configured for
 *                                          unlocking the master key.
 * @param unlockWithMasterPasswordUseCase   Use case to unlock the master key with the master password.
 * @param fetchAutofillDataUseCase          Use case to fetch autofill data.
 */
@HiltViewModel
internal class AutofillAuthViewModel @Inject constructor(
    application: Application,
    areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase,
    private val unlockWithMasterPasswordUseCase: UnlockWithMasterPasswordUseCase,
    private val fetchAutofillDataUseCase: FetchAutofillDataUseCase
) : AndroidViewModel(application) {

    /**
     * Shared preferences.
     */
    private val preferences: SharedPreferences = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    /**
     * Whether to use global theme.
     */
    val useGlobalTheme: Boolean = preferences.getBoolean("global_theme", false)

    /**
     * Theme contrast.
     */
    val themeContrast: ThemeContrast = ThemeContrast.entries[preferences.getInt("theme_contrast", 0)]

    /**
     * Indicates whether biometrics are configured for unlocking the master key.
     */
    val areBiometricsConfigured: Boolean = areBiometricsConfiguredUseCase.areBiometricsConfigured()

    /**
     * Password entered by the user.
     */
    var password: String by mutableStateOf("")

    /**
     * Indicates whether the password entered by the user is valid.
     */
    var isPasswordValid: Boolean by mutableStateOf(true)
        private set

    /**
     * Indicates whether the master key is currently being unlocked.
     */
    var isUnlockingMasterKey: Boolean by mutableStateOf(false)
        private set

    /**
     * Verifies the password entered by the user.
     */
    suspend fun unlockMasterKey() {
        if (!isUnlockingMasterKey) {
            isUnlockingMasterKey = true
            isPasswordValid = try {
                unlockWithMasterPasswordUseCase.unlock(password.toCharArray())
            } catch (_: Exception) {
                false
            }
            isUnlockingMasterKey = false
        }
    }


    /**
     * Fetches the autofill data from the app and returns a FillResponse that can be returned to the
     * app which should be auto-filled.
     *
     * @param packageName               Package name of the app that is being auto-filled.
     * @param autofillTypes             Autofill types mapped to their autofill IDs.
     * @param focusedAutofillPartition  Autofill partition whose data to autofill.
     * @param capabilities              List of account capabilities.
     * @return                          FillResponse for the auto-filled app.
     */
    suspend fun fetchAutofillData(
        packageName: String,
        autofillTypes: Map<AutofillType, List<AutofillId>>,
        focusedAutofillPartition: AutofillPartition,
        capabilities: List<AccountCapability>
    ): FillResponse {
        val accountIds: Set<Uuid> = capabilitiesToAccountIds(capabilities)
        val autofillTypesSet: Set<AutofillType> = autofillTypes.keys
        val autofillResponses: List<AutofillResponse> = fetchAutofillDataUseCase.fetchData(accountIds, autofillTypesSet)

        val usedAutofillIds: MutableList<AutofillId> = mutableListOf()

        //Generate fill response:
        val fillResponseBuilder = FillResponse.Builder()
        autofillResponses.forEach { response ->
            val datasetBuilder = Dataset.Builder()

            response.items.forEach { item ->
                val autofillIds: List<AutofillId>? = autofillTypes[item.type]
                autofillIds?.forEach { autofillId ->
                    if (item.type.partition == focusedAutofillPartition) {
                        val presentations: Presentations = buildPresentations(packageName, item)
                        val field: Field = Field.Builder()
                            .setValue(AutofillValue.forText(item.content))
                            .setPresentations(presentations)
                            .build()
                        datasetBuilder.setField(autofillId, field)
                    }
                    usedAutofillIds.add(autofillId)
                }
            }
            fillResponseBuilder.addDataset(datasetBuilder.build())
        }

        val unusedAutofillIds: List<AutofillId> = getUnusedAutofillIds(autofillTypes, usedAutofillIds)
        if (unusedAutofillIds.isNotEmpty()) {
            fillResponseBuilder.setIgnoredIds(*unusedAutofillIds.toTypedArray())
        }

        return fillResponseBuilder.build()
    }


    /**
     * Gets a list of unused autofill IDs from the provided map of autofill types.
     *
     * @param autofillTypes     Maps lists of autofill IDs to their corresponding autofill type.
     * @param usedAutofillIds   List of autofill IDs that are auto-filled by the service.
     * @return                  List of unused autofill IDs.
     */
    private fun getUnusedAutofillIds(autofillTypes: Map<AutofillType, List<AutofillId>>, usedAutofillIds: List<AutofillId>): List<AutofillId> {
        val unusedAutofillIds: MutableList<AutofillId> = mutableListOf()
        autofillTypes.values.forEach { autofillIds ->
            autofillIds.forEach { autofillId ->
                if (!usedAutofillIds.contains(autofillId)) {
                    unusedAutofillIds.add(autofillId)
                }
            }
        }
        return unusedAutofillIds
    }


    /**
     * Builds the presentations for the specified autofill item.
     *
     * @param packageName   Package name of this app (i.e. Password Vault).
     * @param item          Autofill item for which to build the presentation.
     * @return              Presentations for the specified autofill item.
     */
    private fun buildPresentations(packageName: String, item: AutofillItem): Presentations {
        val presentationView = RemoteViews(packageName, R.layout.autofill_presentation_item)
        presentationView.setTextViewText(R.id.label, item.label)
        if (item.isObfuscated) {
            presentationView.setTextViewText(R.id.content, "*****")
        }
        else {
            presentationView.setTextViewText(R.id.content, item.content)
        }

        val presentation: Presentations = Presentations.Builder()
            .setMenuPresentation(presentationView)
            .build()

        return presentation
    }


    /**
     * Converts the provided list of account capabilities to a set of IDs of accounts.
     *
     * @param capabilities  Account capabilities.
     * @return              Set of account IDs.
     */
    private fun capabilitiesToAccountIds(capabilities: List<AccountCapability>): Set<Uuid> {
        val accountIds: MutableSet<Uuid> = mutableSetOf()
        capabilities.forEach { capability ->
            accountIds.add(capability.account)
        }
        return accountIds
    }

}
