package de.christian2003.feature.autofill.infrastructure.services

import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.content.Intent
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.Presentations
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillId
import android.widget.RemoteViews
import androidx.core.net.toUri
import dagger.hilt.android.AndroidEntryPoint
import de.christian2003.data.accounts.application.usecases.GetAccountCapabilitiesUseCase
import de.christian2003.data.accounts.application.usecases.ValidatePackageSignatureUseCase
import de.christian2003.data.accounts.domain.entities.AccountCapability
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.feature.autofill.application.usecases.FillRequestUseCase
import de.christian2003.feature.autofill.domain.entities.AutofillType
import de.christian2003.feature.autofill.presentation.ui.auth.AutofillAuthActivity
import de.christian2003.feature.autofill.R
import de.christian2003.feature.autofill.infrastructure.services.dto.AutofillFieldMap
import de.christian2003.feature.autofill.infrastructure.services.mapper.AutofillTypeMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PasswordVaultAutofillService: AutofillService() {

    @Inject
    internal lateinit var fillRequestUseCase: FillRequestUseCase

    @Inject
    internal lateinit var assistStructureFetcher: AssistStructureFetcher

    @Inject
    internal lateinit var assistStructureParser: AssistStructureParser

    @Inject
    internal lateinit var getAccountCapabilitiesUseCase: GetAccountCapabilitiesUseCase

    @Inject
    internal lateinit var validatePackageSignatureUseCase: ValidatePackageSignatureUseCase

    @Inject
    internal lateinit var autofillTypeMapper: AutofillTypeMapper

    private val serviceScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    /**
     * Method handles the system request to provide data in order to fill remote views in another app.
     *
     * @param request               Request.
     * @param cancellationSignal    Signal for cancellation.
     * @param callback              Callback to finish the request.
     */
    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        serviceScope.launch {
            //Get assist structure:
            val assistStructure: AssistStructure? = assistStructureFetcher.fetchAssistStructure(request.fillContexts)
            val remotePackageName: String? = assistStructure?.activityComponent?.packageName
            if (assistStructure == null || remotePackageName == null || remotePackageName == this@PasswordVaultAutofillService.packageName) {
                //Assist structure either unavailable or requesting app is Password Vault:
                callback.onFailure("Fill request invalid")
                return@launch
            }

            //Get autofill types from assist structure:
            val autofillTypes: Map<AutofillId, List<AutofillType>> = assistStructureParser.parse(assistStructure)

            //Get accounts:
            val detailTypes: List<DetailType> = autofillTypesToDetailTypes(autofillTypes)
            val capabilities: List<AccountCapability> = getAccountCapabilitiesUseCase.getAccountCapabilities(
                targetName = remotePackageName,
                detailTypes
            )
            if (capabilities.isEmpty()) {
                callback.onSuccess(null)
                return@launch
            }

            //Verify validity of target:
            val isPackageValid: Boolean = validatePackageSignatureUseCase.validate(remotePackageName, capabilities.first().targetUrl.toUri())
            if (!isPackageValid) {
                callback.onFailure("Blocked because of app signature")
                return@launch
            }

            //Authentication:
            val authIntent= Intent(this@PasswordVaultAutofillService, AutofillAuthActivity::class.java)
            authIntent.putExtra("autofill", AutofillFieldMap(autofillTypes))
            val authPendingIntent: PendingIntent = PendingIntent.getActivity(this@PasswordVaultAutofillService, 0, authIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

            val autofillIds: List<AutofillId> = autofillTypes.entries.map { (autofillId, _) -> autofillId }

            val presentations: Presentations = Presentations.Builder()
                .setMenuPresentation(RemoteViews(this@PasswordVaultAutofillService.packageName, R.layout.autofill_presentation_auth))
                .build()
            val response: FillResponse = FillResponse.Builder()
                .setAuthentication(autofillIds.toTypedArray(), authPendingIntent.intentSender, presentations)
                .build()

            callback.onSuccess(response)
        }

        cancellationSignal.setOnCancelListener {
            serviceScope.coroutineContext.cancelChildren()
        }
    }


    /**
     * Method handles the system request to save data from the remove views of another app.
     *
     * @param request   Request.
     * @param callback  Callback to finish the request.
     */
    override fun onSaveRequest(
        request: SaveRequest,
        callback: SaveCallback
    ) {
        //TODO
    }



    private fun autofillTypesToDetailTypes(autofillTypes: Map<AutofillId, List<AutofillType>>): List<DetailType> {
        val detailTypes: MutableList<DetailType> = mutableListOf()

        autofillTypes.entries.forEach { (_, autofillTypes) ->
            autofillTypes.forEach { autofillType ->
                val detailType: DetailType? = autofillTypeMapper.toDetailType(autofillType)
                if (detailType != null && !detailTypes.contains(detailType)) {
                    detailTypes.add(detailType)
                }
            }
        }

        return detailTypes
    }

}
