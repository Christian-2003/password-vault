package de.christian2003.feature.auth.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.feature.auth.models.dialogs.AuthSettingsScreenDialog
import de.christian2003.feature.auth.models.other.AuthRecommendation
import de.christian2003.core.common.application.services.DateTimeFormatterService
import de.christian2003.core.security.application.usecases.AreBiometricsAvailableUseCase
import de.christian2003.core.security.application.usecases.AreBiometricsConfiguredUseCase
import de.christian2003.core.security.application.usecases.DisableBiometricsUseCase
import de.christian2003.core.security.application.usecases.GetAuthMetadataUseCase
import de.christian2003.core.security.domain.entities.AuthMetadata
import de.christian2003.core.ui.model.ColorGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject


/**
 * View model for the screen showing authentication settings.
 *
 * @param application                       Application.
 * @param areBiometricsConfiguredUseCase    Use case to get whether biometrics are configured.
 * @param areBiometricsAvailableUseCase     Use case to get whether biometrics are available.
 * @param getAuthMetadataUseCase            Use case to get metadata for the auth data.
 * @param colorGenerator                    Color generator.
 * @param disableBiometricsUseCase          Use case to disable biometrics.
 * @param dateTimeFormatterService          Service for format dates and times.
 */
@HiltViewModel
internal class AuthSettingsViewModel @Inject constructor(
    application: Application,
    areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase,
    areBiometricsAvailableUseCase: AreBiometricsAvailableUseCase,
    getAuthMetadataUseCase: GetAuthMetadataUseCase,
    private val colorGenerator: ColorGenerator,
    private val disableBiometricsUseCase: DisableBiometricsUseCase,
    private val dateTimeFormatterService: DateTimeFormatterService
): AndroidViewModel(application) {

    /**
     * Whether biometrics are available.
     */
    val areBiometricsAvailable: Boolean = areBiometricsAvailableUseCase.areBiometricsAvailable()

    /**
     * Whether biometrics are configured.
     */
    val areBiometricsConfigured: Flow<Boolean> = areBiometricsConfiguredUseCase.areBiometricsConfiguredAsFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    /**
     * Metadata for the auth data.
     */
    val authMetadata: Flow<AuthMetadata?> = getAuthMetadataUseCase.getMetadata().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    /**
     * Action that is recommended to the user.
     */
    var authRecommendation: AuthRecommendation by mutableStateOf(AuthRecommendation.None)
        private set

    /**
     * Dialog displayed currently by the screen.
     */
    var dialog: AuthSettingsScreenDialog by mutableStateOf(AuthSettingsScreenDialog.None)


    /**
     * Initializes the view model.
     */
    init {
        viewModelScope.launch {
            combine(authMetadata, areBiometricsConfigured) { metadata, areBiometricsConfigured ->
                Pair<AuthMetadata?, Boolean>(metadata, areBiometricsConfigured)
            }.collect { (metadata, areBiometricsConfigured) ->
                val now: LocalDate = LocalDate.now()
                if (metadata != null) {
                    authRecommendation = when {
                        areBiometricsAvailable && !areBiometricsConfigured -> AuthRecommendation.EnableBiometrics
                        daysBetween(metadata.masterPasswordEditedAt, now) > 182 -> AuthRecommendation.ChangePassword //Half a year
                        daysBetween(metadata.recoveryCodesEditedAt, now) > 365 -> AuthRecommendation.RegenerateRecoveryCodes //Full year
                        else -> AuthRecommendation.None
                    }
                }
            }
        }
    }


    /**
     * Generates a positive color from the provided negative color.
     *
     * @param negative  Negative color.
     * @param darkTheme Whether the system is in dark theme.
     * @return          Generated neutral color.
     */
    fun generatePositiveColorFromNegativeColor(negative: Color, darkTheme: Boolean): Color {
        return colorGenerator.generatePositiveColorFromNegativeColor(negative, darkTheme)
    }


    /**
     * Generates a neutral color from the provided seed color.
     *
     * @param seed      Seed color.
     * @param darkTheme Whether the system is in dark theme.
     * @return          Generated neutral color.
     */
    fun generateNeutralColorFromSeedColor(seed: Color, darkTheme: Boolean): Color {
        return colorGenerator.generateNeutralColorFromSeed(seed, darkTheme)
    }


    /**
     * Formats the specified time.
     *
     * @param time  Time to format.
     * @return      Formatted time.
     */
    fun formatTime(time: LocalDateTime): String {
        return dateTimeFormatterService.format(time)
    }


    /**
     * Disables the biometrics.
     */
    fun disableBiometrics() {
        disableBiometricsUseCase.disable()
    }


    /**
     * Calculates the number of days between the specified time and now.
     *
     * @param time  Time.
     * @param now   Now.
     * @return      Number of days between time and now.
     */
    private fun daysBetween(time: LocalDateTime?, now: LocalDate): Long {
        if (time != null) {
            val days: Long = now.toEpochDay() - time.toLocalDate().toEpochDay()
            if (days > 0) {
                return days
            }
        }
        return 0
    }

}
