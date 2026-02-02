package de.christian2003.auth.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.auth.models.dialogs.AuthSettingsScreenDialog
import de.christian2003.auth.models.other.AuthRecommendation
import de.christian2003.common.formatter.DateTimeFormatterService
import de.christian2003.security.application.usecases.AreBiometricsAvailableUseCase
import de.christian2003.security.application.usecases.AreBiometricsConfiguredUseCase
import de.christian2003.security.application.usecases.DisableBiometricsUseCase
import de.christian2003.security.application.usecases.GetAuthMetadataUseCase
import de.christian2003.security.domain.entities.AuthMetadata
import de.christian2003.ui.model.ColorGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject


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

    val areBiometricsAvailable: Boolean = areBiometricsAvailableUseCase.areBiometricsAvailable()

    val areBiometricsConfigured: Flow<Boolean> = areBiometricsConfiguredUseCase.areBiometricsConfiguredAsFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    val authMetadata: Flow<AuthMetadata?> = getAuthMetadataUseCase.getMetadata().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    var authRecommendation: AuthRecommendation by mutableStateOf(AuthRecommendation.None)
        private set

    var dialog: AuthSettingsScreenDialog by mutableStateOf(AuthSettingsScreenDialog.None)


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
