package de.christian2003.feature.analysis.presentation.ui.summary

import androidx.compose.runtime.Composable
import de.christian2003.core.common.domain.abstraction.LandingPlugin
import de.christian2003.core.common.domain.abstraction.LandingPluginContext
import javax.inject.Inject


/**
 * Plugin for the landing page showing the summary of the last password security analysis.
 */
internal class SummaryPlugin @Inject constructor(): LandingPlugin {

    /**
     * ID for the plugin.
     */
    override val id: String = "AnalysisSummary"

    /**
     * Priority for the plugin determines its position on the landing page.
     */
    override val priority: Int = 10

    /**
     * Composable content of the plugin.
     *
     * @param context   Context for the plugin.
     */
    @Composable
    override fun Content(context: LandingPluginContext) = SummaryPluginView(context)

}
