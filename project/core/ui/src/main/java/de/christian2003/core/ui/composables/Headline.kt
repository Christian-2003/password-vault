package de.christian2003.core.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import de.christian2003.core.ui.R


/**
 * Displays a headline.
 *
 * @param title                 Title of the headline.
 * @param modifier              Modifier.
 * @param isEyecatcherVisible   Whether an eyecatcher is displayed for the endIcon.
 * @param indentation           Indentation of the headline text.
 * @param tooltip               Optional tooltip.
 * @param endIcon               End icon for the headline.
 * @param onClick               Callback invoked once the headline is clicked.
 */
@Composable
fun Headline(
    title: String,
    modifier: Modifier = Modifier,
    isEyecatcherVisible: Boolean = false,
    indentation: HeadlineIndentation = HeadlineIndentation.None,
    tooltip: String? = null,
    endIcon: Painter? = null,
    onClick: (() -> Unit)? = null
) {
    val headlineContent: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick != null) {
                    onClick!!()
                }
                .padding(
                    start = when (indentation) {
                        HeadlineIndentation.None -> dimensionResource(R.dimen.margin_horizontal)
                        HeadlineIndentation.TextLevel -> dimensionResource(R.dimen.margin_horizontal) + dimensionResource(R.dimen.padding_horizontal)
                        HeadlineIndentation.PrefixIconLevel -> dimensionResource(R.dimen.margin_horizontal) + dimensionResource(R.dimen.image_xs) + dimensionResource(R.dimen.padding_horizontal) * 2
                    },
                    top = dimensionResource(R.dimen.padding_vertical),
                    end = dimensionResource(R.dimen.margin_horizontal),
                    bottom = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            if (endIcon != null) {
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Icon(
                        painter = endIcon,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "",
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
                    )
                    if (isEyecatcherVisible) {
                        Eyecatcher()
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.padding_vertical))
    ) {
        if (tooltip != null) {
            Tooltip(
                tooltip = tooltip
            ) {
                headlineContent()
            }
        }
        else {
            headlineContent()
        }
    }
}


/**
 * Indentations for the headline.
 *
 * @property None               No indentation.
 * @property TextLevel          Indentation to the text of list items.
 * @property PrefixIconLevel    Indentation to the prefix icon of list items.
 */
enum class HeadlineIndentation {
    None,
    TextLevel,
    PrefixIconLevel
}
