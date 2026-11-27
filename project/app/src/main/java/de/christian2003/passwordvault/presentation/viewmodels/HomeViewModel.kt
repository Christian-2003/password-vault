package de.christian2003.passwordvault.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * View model for the home view (which is the landing view for the entire app.)
 *
 * @param application   Application.
 */
@HiltViewModel
internal class HomeViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

}
