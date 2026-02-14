package de.christian2003.feature.autofill.infrastructure.services

import android.app.PendingIntent
import android.content.Intent
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.util.Log
import android.view.autofill.AutofillId
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import de.christian2003.data.accounts.application.usecases.GetAccountCapabilitiesUseCase
import de.christian2003.feature.autofill.application.usecases.FillRequestUseCase
import de.christian2003.feature.autofill.domain.entities.AutofillType
import de.christian2003.feature.autofill.presentation.ui.auth.AutofillAuthActivity
import de.christian2003.feature.autofill.R
import javax.inject.Inject


@AndroidEntryPoint
class PasswordVaultAutofillService: AutofillService() {

    @Inject internal lateinit var fillRequestUseCase: FillRequestUseCase
    @Inject internal lateinit var fillContextParser: FillContextParser

    @Inject internal lateinit var getAccountCapabilitiesUseCase: GetAccountCapabilitiesUseCase


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
        Log.d("AUTOFILL", "Autofill service called")

        val autofillTypes: Map<AutofillId, List<AutofillType>> = fillContextParser.parse(request.fillContexts)

        val authIntent = Intent(this, AutofillAuthActivity::class.java)
        val authPendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, authIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val autofillIds: List<AutofillId> = autofillTypes.entries.map { (autofillId, _) -> autofillId }

        val response: FillResponse = FillResponse.Builder()
            .setAuthentication(autofillIds.toTypedArray(), authPendingIntent.intentSender, RemoteViews(packageName, R.layout.autofill_presentation_auth))
            .build()

        Log.d("AUTOFILL", "OnSuccess")
        callback.onSuccess(response)
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

}
