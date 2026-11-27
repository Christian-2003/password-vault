package de.christian2003.passwordvault.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.presentation.models.dto.MainNavigationItemUiDto
import javax.inject.Inject


/**
 * View model for the main screen.
 *
 * @param application   Application.
 */
@HiltViewModel
internal class MainViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    /**
     * Destinations for the main navigation
     */
    val navigationDestinations: List<MainNavigationItemUiDto> = listOf(
        MainNavigationItemUiDto(
            label = R.string.main_destination_home,
            icon = R.drawable.ic_home,
            selectedIcon = R.drawable.ic_home_filled
        ),
        MainNavigationItemUiDto(
            label = R.string.main_destination_accounts,
            icon = R.drawable.ic_accounts,
            selectedIcon = R.drawable.ic_accounts_filled
        )
    )

    var selectedDestination: Int = 0


}
