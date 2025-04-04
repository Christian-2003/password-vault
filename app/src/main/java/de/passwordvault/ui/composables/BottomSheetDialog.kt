package de.passwordvault.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import de.passwordvault.R


/**
 * Composable displays a bottom sheet dialog.
 *
 * @param sheetState        Sheet state.
 * @param title             Title for the dialog.
 * @param onDismiss         Callback invoked once the dialog closes.
 * @param icon              Optional icon to display in front of the title.
 * @param iconComposable    Optional composable to display in front of the title instead of the icon.
 * @param content           Composable content for the dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDialog(
    sheetState: SheetState,
    title: String,
    onDismiss: () -> Unit,
    icon: Painter? = null,
    iconComposable: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val header: @Composable () -> Unit = if (icon != null) {
        @Composable {
            BottomSheetDialogHeaderWithIcon(title, icon, modifier = Modifier.fillMaxWidth())
        }
    } else if (iconComposable != null) {
        @Composable {
            BottomSheetDialogHeaderWithComposable(title, iconComposable, Modifier.fillMaxWidth())
        }
    } else {
        @Composable {
            BottomSheetDialogHeader(title, Modifier.fillMaxWidth())
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            header()
            HorizontalDivider()
            content()
        }
    }
}


/**
 * Composable displays a bottom sheet header with an icon.
 *
 * @param title     Title to display.
 * @param icon      Icon to display in front of the title.
 * @param modifier  Modifier.
 */
@Composable
private fun BottomSheetDialogHeaderWithIcon(
    title: String,
    icon: Painter,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical) * 2
            )
    ) {
        Icon(
            painter = icon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(dimensionResource(R.dimen.image_xs))
        )
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal_between))
        )
    }
}


/**
 * Composable displays a bottom sheet header with a composable.
 *
 * @param title         Title to display.
 * @param composable    Composable displayed in front of the title.
 * @param modifier      Modifier.
 */
@Composable
private fun BottomSheetDialogHeaderWithComposable(
    title: String,
    composable: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical) * 2
            )
    ) {
        composable()
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
        )
    }
}


/**
 * Composable displays a bottom sheet header without icon or composable.
 *
 * @param title     Title to display.
 * @param modifier  Modifier.
 */
@Composable
private fun BottomSheetDialogHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical) * 2
            )
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}
