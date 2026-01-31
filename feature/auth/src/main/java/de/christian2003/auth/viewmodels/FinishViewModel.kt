package de.christian2003.auth.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.auth.models.states.FinishScreenState
import de.christian2003.auth.navigation.FinishDestination
import de.christian2003.ui.model.ColorGenerator
import javax.inject.Inject


/**
 * View model for the screen through which authentication workflows are being finished.
 *
 * @param savedStateHandle  Saved state handle.
 * @param colorGenerator    Color generator.
 */
@HiltViewModel
internal class FinishViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val colorGenerator: ColorGenerator
): ViewModel() {

    /**
     * State in which the screen is displayed.
     */
    val state: FinishScreenState = savedStateHandle.toRoute<FinishDestination>().state


    /**
     * Generates a positive color from the specified negative color.
     *
     * @param negative  Negative color used as seed for generating a positive color.
     * @param darkTheme Whether the app is in dark theme currently.
     * @return          Positive color generated.
     */
    fun generatePositiveColorFromNegativeColor(negative: Color, darkTheme: Boolean): Color {
        return colorGenerator.generatePositiveColorFromNegativeColor(negative, darkTheme)
    }

}
