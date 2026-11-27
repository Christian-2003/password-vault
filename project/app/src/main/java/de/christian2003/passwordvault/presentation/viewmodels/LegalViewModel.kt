package de.christian2003.passwordvault.presentation.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.common.application.services.DateTimeFormatterService
import de.christian2003.passwordvault.application.AcceptLegalPageUseCase
import de.christian2003.passwordvault.application.GetLegalPageUseCase
import de.christian2003.passwordvault.domain.entities.LegalPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


/**
 * View model for the page through which users are informed about updates to legal pages.
 *
 * @param application               Application.
 * @param getLegalPageUseCase       Use case to get the legal pages.
 * @param acceptLegalPageUseCase    Use case to accept the updated legal pages.
 * @param dateTimeFormatterService  Service to format dates and times.
 */
@HiltViewModel
internal class LegalViewModel @Inject constructor(
    application: Application,
    getLegalPageUseCase: GetLegalPageUseCase,
    private val acceptLegalPageUseCase: AcceptLegalPageUseCase,
    private val dateTimeFormatterService: DateTimeFormatterService,
): AndroidViewModel(application) {

    /**
     * Terms for the PP.
     */
    var privacyPolicyPage: LegalPage? by mutableStateOf(null)
        private set

    /**
     * Page for the TOS.
     */
    var termsOfServicePage: LegalPage? by mutableStateOf(null)
        private set

    /**
     * Date on which the updated legal pages take effect.
     */
    var validDate: LocalDate? by mutableStateOf(null)
        private set


    /**
     * Initializes the view model.
     */
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val privacyPolicy: LegalPage? = getLegalPageUseCase.getPrivacyPolicy()
            val termsOfService: LegalPage? = getLegalPageUseCase.getTermsOfService()

            var validDate: LocalDate? = when {
                privacyPolicy == null && termsOfService != null -> termsOfService.valid
                privacyPolicy != null && termsOfService == null -> privacyPolicy.valid
                privacyPolicy != null && termsOfService != null-> {
                    if (privacyPolicy.valid.isBefore(termsOfService.valid)) {
                        privacyPolicy.valid
                    } else {
                        termsOfService.valid
                    }
                }
                else -> null
            }

            privacyPolicyPage = privacyPolicy
            termsOfServicePage = termsOfService
            this@LegalViewModel.validDate = validDate
        }
    }


    /**
     * Formats the provided date.
     *
     * @param date  Date to format.
     * @return      Formatted date.
     */
    fun formatDate(date: LocalDate): String {
        return dateTimeFormatterService.format(date)
    }


    /**
     * Accepts the updated legal pages.
     */
    fun acceptLegalPages() = viewModelScope.launch(Dispatchers.IO) {
        acceptLegalPageUseCase.acceptTermsOfService()
        acceptLegalPageUseCase.acceptPrivacyPolicy()
    }

}
