package de.christian2003.auth.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.auth.models.other.AuthRecommendation
import de.christian2003.common.formatter.DateTimeFormatterService
import de.christian2003.security.application.usecases.AreBiometricsAvailableUseCase
import de.christian2003.security.application.usecases.AreBiometricsConfiguredUseCase
import de.christian2003.security.application.usecases.DisableBiometricsUseCase
import de.christian2003.security.application.usecases.GetAuthMetadataUseCase
import de.christian2003.security.domain.entities.AuthMetadata
import de.christian2003.ui.model.ColorGenerator
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
class AuthSettingsViewModel @Inject constructor(
    application: Application,
    areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase,
    areBiometricsAvailableUseCase: AreBiometricsAvailableUseCase,
    private val colorGenerator: ColorGenerator,
    private val disableBiometricsUseCase: DisableBiometricsUseCase,
    private val getAuthMetadataUseCase: GetAuthMetadataUseCase,
    private val dateTimeFormatterService: DateTimeFormatterService
): AndroidViewModel(application) {

    val areBiometricsAvailable: Boolean = areBiometricsAvailableUseCase.areBiometricsAvailable()

    var areBiometricsConfigured: Boolean by mutableStateOf(areBiometricsConfiguredUseCase.areBiometricsConfigured())
        private set

    var authRecommendation: AuthRecommendation by mutableStateOf(AuthRecommendation.None)

    var authMetadata: AuthMetadata by mutableStateOf(getAuthMetadataUseCase.getMetadata())
        private set


    init {
        determineAuthRecommendation()
    }


    fun generatePositiveColorFromNegativeColor(negative: Color, darkTheme: Boolean): Color {
        return colorGenerator.generatePositiveColorFromNegativeColor(negative, darkTheme)
    }

    fun generateNeutralColorFromSeedColor(seed: Color, darkTheme: Boolean): Color {
        return colorGenerator.generateNeutralColorFromSeed(seed, darkTheme)
    }

    fun formatTime(time: LocalDateTime): String {
        return dateTimeFormatterService.format(time)
    }

    fun disableBiometrics() {
        disableBiometricsUseCase.disable()
        areBiometricsConfigured = false
        determineAuthRecommendation()
    }


    private fun determineAuthRecommendation() {
        val now: LocalDate = LocalDate.now()
        authRecommendation = when {
            areBiometricsAvailable && !areBiometricsConfigured -> AuthRecommendation.EnableBiometrics
            daysBetween(authMetadata.masterPasswordEditedAt, now) > 182 -> AuthRecommendation.ChangePassword //Half a year
            daysBetween(authMetadata.recoveryCodesEditedAt, now) > 365 -> AuthRecommendation.RegenerateRecoveryCodes //Full year
            else -> AuthRecommendation.None
        }
    }


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
