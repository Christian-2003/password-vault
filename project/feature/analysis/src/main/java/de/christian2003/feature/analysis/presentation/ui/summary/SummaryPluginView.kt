package de.christian2003.feature.analysis.presentation.ui.summary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.graphics.shapes.RoundedPolygon
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import de.christian2003.core.common.domain.abstraction.LandingPluginContext
import de.christian2003.core.ui.composables.Shape
import de.christian2003.feature.analysis.R
import de.christian2003.feature.analysis.domain.entities.SecurityResultOverview
import de.christian2003.feature.analysis.navigation.AnalysisDestination
import de.christian2003.feature.analysis.presentation.viewmodels.SummaryPluginViewModel


/**
 * View for the analysis result summary plugin.
 *
 * @param context   Plugin context.
 * @param viewModel View model.
 */
@Composable
internal fun SummaryPluginView(
    context: LandingPluginContext,
    viewModel: SummaryPluginViewModel = hiltViewModel()
) {
    val overview: SecurityResultOverview? = viewModel.latestOverview
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        if (overview == null) {
            //User did not run the analysis yet:
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.analysis),
                    contentDescription = "",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                        .width(dimensionResource(de.christian2003.core.ui.R.dimen.image_xxl))
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.result_info_notRun),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(
                        onClick = {
                            context.navigate(AnalysisDestination)
                        }
                    ) {
                        Text(stringResource(R.string.result_button_runAnalysis))
                    }
                }
            }
        }
        else {
            //Show overview:
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                //Weak passwords:
                ResultItem(
                    text = stringResource(R.string.result_label_weakPasswords),
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_error),
                    count = overview.weakPasswordCount,
                    shape = MaterialShapes.Flower,
                    modifier = Modifier.weight(1f)
                )

                Box(modifier = Modifier.width(dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)))

                //Reused passwords:
                ResultItem(
                    text = stringResource(R.string.result_label_reusedPasswords),
                    painter = painterResource(R.drawable.ic_reuse),
                    count = overview.reusedPasswordCount,
                    shape = MaterialShapes.SoftBurst,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = stringResource(R.string.result_info_time, viewModel.formatDateTime(overview.timestamp)),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            TextButton(
                onClick = {
                    context.navigate(AnalysisDestination)
                }
            ) {
                Text(stringResource(R.string.result_button_showMore))
            }
        }
    }

}


/**
 * Result item for the analysis.
 *
 * @param text      Text label.
 * @param painter   Painter for the icon.
 * @param count     Count of passwords.
 * @param shape     Shape for the icon.
 * @param modifier  Modifier.
 */
@Composable
private fun ResultItem(
    text: String,
    painter: Painter,
    count: Int,
    shape: RoundedPolygon,
    modifier: Modifier = Modifier
) {
    val highlightColor: Color = if (count == 0) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }
    val onHighlightColor: Color = if (count == 0) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onError
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        Shape(
            shape = shape,
            color = highlightColor,
            modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
        ) {
            Icon(
                painter = painter,
                contentDescription = "",
                tint = onHighlightColor,
                modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
            )
        }
        Text(
            text = text,
            color = highlightColor,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = count.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
