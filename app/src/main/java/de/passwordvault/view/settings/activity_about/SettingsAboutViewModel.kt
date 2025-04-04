package de.passwordvault.view.settings.activity_about

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.passwordvault.model.rest.RestCallback
import de.passwordvault.model.rest.RestError
import de.passwordvault.model.rest.legal.LegalRestClient
import de.passwordvault.model.rest.legal.LocalizedLegalPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Class implements the view model for the screen which displays settings about the app.
 */
class SettingsAboutViewModel: ViewModel() {

    /**
     * REST client fetching info about the privacy policy.
     */
    private val privacyRestClient = LegalRestClient("privacy", "privacy")

    /**
     * REST client fetching info about the terms of service.
     */
    private val tosRestClient = LegalRestClient("tos", "tos")

    /**
     * REST error which occurred when fetching the privacy policy.
     */
    var privacyError: RestError? by mutableStateOf(null)

    /**
     * REST error which occurred when fetching the terms of service.
     */
    var tosError: RestError? by mutableStateOf(null)

    /**
     * Info about the privacy policy fetched from the REST API.
     */
    var privacyPage: LocalizedLegalPage? by mutableStateOf(null)

    /**
     * Info about the terms of service fetched from the REST API.
     */
    var tosPage: LocalizedLegalPage? by mutableStateOf(null)


    /**
     * Method initializes the view model.
     */
    fun init() {
        fetchData()
    }


    /**
     * Method fetches data from the REST API.
     */
    fun fetchData() = viewModelScope.launch(Dispatchers.IO) {
        if (!privacyRestClient.isFetching && !privacyRestClient.isFinished) {
            val privacyCallback = RestCallback { _, error ->
                privacyError = error
                if (error == RestError.SUCCESS) {
                    privacyPage = privacyRestClient.legalPage
                }
            }
            privacyRestClient.fetch(privacyCallback)
        }

        if (!tosRestClient.isFetching && !tosRestClient.isFinished) {
            val tosCallback = RestCallback { _, error ->
                tosError = error
                if (error == RestError.SUCCESS) {
                    tosPage = tosRestClient.legalPage
                }
            }
            tosRestClient.fetch(tosCallback)
        }
    }

}
