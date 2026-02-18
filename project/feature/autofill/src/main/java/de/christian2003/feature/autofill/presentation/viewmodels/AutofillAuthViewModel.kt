package de.christian2003.feature.autofill.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.service.autofill.Dataset
import android.service.autofill.Field
import android.service.autofill.FillResponse
import android.service.autofill.Presentations
import android.util.Log
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
import de.christian2003.feature.autofill.domain.entities.AutofillResponse
import de.christian2003.feature.autofill.domain.entities.AutofillType
import javax.inject.Inject
import kotlin.uuid.Uuid


@HiltViewModel
internal class AutofillAuthViewModel @Inject constructor(
    application: Application,
    areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase,
    private val unlockWithMasterPasswordUseCase: UnlockWithMasterPasswordUseCase,
    private val fetchAutofillDataUseCase: FetchAutofillDataUseCase
) : AndroidViewModel(application) {

    private val preferences: SharedPreferences = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    val useGlobalTheme: Boolean = preferences.getBoolean("global_theme", false)

    val themeContrast: ThemeContrast = ThemeContrast.entries[preferences.getInt("theme_contrast", 0)]

    val areBiometricsConfigured: Boolean = areBiometricsConfiguredUseCase.areBiometricsConfigured()

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


    suspend fun fetchAutofillData(packageName: String, autofillTypes: Map<AutofillType, List<AutofillId>>, capabilities: List<AccountCapability>): FillResponse {
        val accountIds: Set<Uuid> = capabilitiesToAccountIds(capabilities)
        val autofillTypesSet: Set<AutofillType> = autofillTypes.keys
        val data: List<AutofillResponse> = fetchAutofillDataUseCase.fetchData(accountIds, autofillTypesSet)

        //Generate fill response:
        val fillResponseBuilder = FillResponse.Builder()
        data.forEach { response ->
            Log.d("Autofill", "Response for ${response.accountId} with ${response.items.size} items")
            val datasetBuilder = Dataset.Builder()

            val autofillIdsWithFields: MutableList<AutofillId> = mutableListOf()
            var fieldsCount = 0

            response.items.forEach { item ->
                Log.d("Autofill", "Response item ${item.label}")
                val autofillIds: List<AutofillId>? = autofillTypes[item.type]
                autofillIds?.forEach { autofillId ->
                    if (!autofillIdsWithFields.contains(autofillId)) {
                        Log.d("Autofill", "Response item ${item.label} with ID $autofillId")
                        val presentations: Presentations = buildPresentations(packageName, item)
                        val field: Field = Field.Builder()
                            .setValue(AutofillValue.forText(item.content))
                            .setPresentations(presentations)
                            .build()
                        datasetBuilder.setField(autofillId, field)
                        autofillIdsWithFields.add(autofillId)
                        fieldsCount++
                    }
                    else {
                        Log.d("Autofill", "Response item ${item.label} with ID $autofillId: ID already used")
                    }
                }
            }
            if (fieldsCount > 0) {
                Log.d("Autofill", "Add dataset to fill response")
                fillResponseBuilder.addDataset(datasetBuilder.build())
            }
            autofillIdsWithFields.clear()
        }

        return fillResponseBuilder.build()
    }


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


    private fun capabilitiesToAccountIds(capabilities: List<AccountCapability>): Set<Uuid> {
        val accountIds: MutableSet<Uuid> = mutableSetOf()
        capabilities.forEach { capability ->
            accountIds.add(capability.account)
        }
        return accountIds
    }

}
