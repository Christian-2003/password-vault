package de.christian2003.passwordvault.plugin.presentation.view.help

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application


/**
 * View model for the screen through which all help messages are displayed.
 */
class HelpViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Indicates whether the view model is initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * Maps the help cards to a boolean which indicates whether the card is visible or not.
     */
    val helpCards: MutableMap<HelpCard, Boolean> = mutableStateMapOf()


    /**
     * Initializes the view model.
     */
    fun init() {
        if (isInitialized) {
            return
        }
        HelpCard.entries.forEach { helpCard ->
            helpCards[helpCard] = helpCard.getVisible(application)
        }
        isInitialized = true
    }


    /**
     * Toggles the visibility of a help card.
     *
     * @param helpCard  Help card whose visibility to toggle.
     */
    fun toggleHelpCardVisibility(helpCard: HelpCard) {
        helpCard.setVisible(application, !helpCard.getVisible(application))
        helpCards[helpCard] = helpCard.getVisible(application)
    }


    /**
     * Dismisses the help card on the page.
     */
    fun dismissHelpCard() {
        HelpCard.HELP.setVisible(application, false)
        helpCards[HelpCard.HELP] = false
    }

}
