package de.christian2003.feature.analysis.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
internal class SummaryPluginViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

}
