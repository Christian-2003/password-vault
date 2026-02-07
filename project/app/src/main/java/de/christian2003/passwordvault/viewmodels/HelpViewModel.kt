package de.christian2003.passwordvault.viewmodels

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.ui.model.HelpCard
import javax.inject.Inject


/**
 * View model for the screen through which all help messages are displayed.
 *
 * @param application   Application.
 */
@HiltViewModel
class HelpViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    /**
     * Maps the help cards to a boolean which indicates whether the card is visible or not.
     */
    val helpCards: MutableMap<HelpCard, Boolean> = mutableStateMapOf()


    /**
     * Initializes the view model.
     */
    init {
        HelpCard.entries.forEach { helpCard ->
            helpCards[helpCard] = helpCard.getVisible(application)
        }
    }


    /**
     * Toggles the visibility of a help card.
     *
     * @param helpCard  Help card whose visibility to toggle.
     */
    fun toggleHelpCardVisibility(helpCard: HelpCard) {
        val context: Context = getApplication<Application>().baseContext
        helpCard.setVisible(context, !helpCard.getVisible(context))
        helpCards[helpCard] = helpCard.getVisible(context)
    }


    /**
     * Dismisses the help card on the page.
     */
    fun dismissHelpCard() {
        val context: Context = getApplication<Application>().baseContext
        HelpCard.Help.setVisible(context, false)
        helpCards[HelpCard.Help] = false
    }

}
