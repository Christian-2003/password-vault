package de.christian2003.feature.analysis.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.common.application.services.DateTimeFormatterService
import de.christian2003.feature.analysis.application.usecases.GetLatestSecurityResultOverviewUseCase
import de.christian2003.feature.analysis.domain.entities.SecurityResultOverview
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
internal class SummaryPluginViewModel @Inject constructor(
    application: Application,
    private val getLatestSecurityResultOverviewUseCase: GetLatestSecurityResultOverviewUseCase,
    private val dateTimeFormatterService: DateTimeFormatterService
): AndroidViewModel(application) {

    var latestOverview: SecurityResultOverview? = getLatestSecurityResultOverviewUseCase.getLatestSecurityResultOverview()
        private set


    fun formatDateTime(time: LocalDateTime): String {
        return dateTimeFormatterService.format(time)
    }

}
