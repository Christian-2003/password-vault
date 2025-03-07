package de.passwordvault.view.passwords.activity_analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.graphics.drawable.toBitmap
import de.passwordvault.R
import de.passwordvault.model.analysis.passwords.AnalyzedPassword
import de.passwordvault.ui.composables.EmptyPlaceholder
import de.passwordvault.ui.theme.LocalPasswordVaultColors
import de.passwordvault.view.utils.Utils


/**
 * Composable displays the tab containing a list of all weak passwords.
 *
 * @param weakPasswords         List of weak passwords.
 * @param onWeakPasswordClicked Callback invoked once a weak password is clicked.
 * @param thresholdGood         Threshold with which a password is considered 'good'.
 * @param thresholdNeutral      Threshold with which a password is considered 'neutral'.
 * @param maxSecurityScore      Max possible security score.
 */
@Composable
fun WeakPasswordsTab(
    weakPasswords: List<AnalyzedPassword>,
    onWeakPasswordClicked: (AnalyzedPassword) -> Unit,
    thresholdGood: Float,
    thresholdNeutral: Float,
    maxSecurityScore: Int
) {
    if (weakPasswords.isEmpty()) {
        EmptyPlaceholder(
            title = stringResource(R.string.password_all_empty_title),
            text = stringResource(R.string.password_all_empty_info),
            painter = painterResource(R.drawable.el_passwords),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.space_horizontal),
                    vertical = dimensionResource(R.dimen.space_vertical)
                )
        )
    }
    else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(weakPasswords) { password ->
                PasswordListRow(
                    password = password,
                    onClick = onWeakPasswordClicked,
                    thresholdGood = thresholdGood,
                    thresholdNeutral = thresholdNeutral,
                    maxSecurityScore = maxSecurityScore
                )
            }
        }
    }
}


/**
 * Composable displays a single password.
 *
 * @param password          Password to display.
 * @param onClick           Callback invoked once the password is clicked.
 * @param thresholdGood     Threshold with which a password is considered 'good'.
 * @param thresholdNeutral  Threshold with which a password is considered 'neutral'.
 * @param maxSecurityScore  Max possible security score.
 */
@Composable
private fun PasswordListRow(
    password: AnalyzedPassword,
    onClick: (AnalyzedPassword) -> Unit,
    thresholdGood: Float,
    thresholdNeutral: Float,
    maxSecurityScore: Int
) {
    var obfuscated: Boolean by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(password)
            }
            .padding(
                start = dimensionResource(R.dimen.space_horizontal),
                top = dimensionResource(R.dimen.space_vertical_between),
                end = dimensionResource(R.dimen.space_horizontal_end_button),
                bottom = dimensionResource(R.dimen.space_vertical_between)
            )
    ) {
        //Logo of the entry:
        Box {
            if (password.entry.logo == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.image_m))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_xs)))
                        .background(LocalPasswordVaultColors.current.backgroundContainer)
                ) {
                    Text(
                        text = if (password.entry.name.isNotEmpty()) { "" + password.entry.name[0] } else { "" },
                        color = LocalPasswordVaultColors.current.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            else {
                val bitmap: ImageBitmap = password.entry.logo.toBitmap().asImageBitmap()
                Icon(
                    painter = BitmapPainter(bitmap),
                    tint = Color.Unspecified,
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(R.dimen.image_m))
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.image_m) - dimensionResource(R.dimen.image_xxs) - dimensionResource(R.dimen.image_xxs) / 2,
                        top = dimensionResource(R.dimen.image_m) - dimensionResource(R.dimen.image_xxs) - dimensionResource(R.dimen.image_xxs) / 2
                    )
                    .align(Alignment.BottomEnd)
                    .size(dimensionResource(R.dimen.image_xxs) * 2)
                    .padding(
                        horizontal = dimensionResource(R.dimen.image_xxs) / 4,
                        vertical = dimensionResource(R.dimen.image_xxs) / 4
                    )
                    .clip(RoundedCornerShape(50))
                    .background(if (password.securityScore > thresholdGood) {
                        LocalPasswordVaultColors.current.backgroundGreen
                    } else if (password.securityScore > thresholdNeutral) {
                        LocalPasswordVaultColors.current.backgroundYellow
                    } else {
                        LocalPasswordVaultColors.current.backgroundRed
                    })
            ) {
                Icon(
                    painter = if (password.securityScore > thresholdGood) {
                        painterResource(R.drawable.ic_ok_filled)
                    } else if (password.securityScore > thresholdNeutral) {
                        painterResource(R.drawable.ic_info_filled)
                    } else {
                        painterResource(R.drawable.ic_alert_filled)
                    },
                    tint = if (password.securityScore > thresholdGood) {
                        LocalPasswordVaultColors.current.green
                    } else if (password.securityScore > thresholdNeutral) {
                        LocalPasswordVaultColors.current.yellow
                    } else {
                        LocalPasswordVaultColors.current.red
                    },
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(R.dimen.image_xxs))
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(R.dimen.space_horizontal_between))
        ) {
            Text(
                text = if (obfuscated) { Utils.obfuscate(password.password) } else { password.password },
                color = LocalPasswordVaultColors.current.text,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = password.entry.name,
                color = LocalPasswordVaultColors.current.textVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    tint = LocalPasswordVaultColors.current.textVariant,
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(R.dimen.image_xxs))
                )
                Text(
                    text = stringResource(R.string.password_results_general_average_score_display)
                        .replace("{arg}", "" + password.securityScore)
                        .replace("{max}", "" + maxSecurityScore),
                    color = LocalPasswordVaultColors.current.textVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.space_horizontal_small))
                )
            }
        }

        IconButton(
            onClick = {
                obfuscated = !obfuscated
            },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                painter = if (obfuscated) { painterResource(R.drawable.ic_show) } else { painterResource(R.drawable.ic_show_off) },
                tint = LocalPasswordVaultColors.current.text,
                contentDescription = ""
            )
        }
    }
}
