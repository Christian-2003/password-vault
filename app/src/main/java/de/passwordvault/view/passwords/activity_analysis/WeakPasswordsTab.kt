package de.passwordvault.view.passwords.activity_analysis

import android.graphics.drawable.shapes.RoundRectShape
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import de.passwordvault.model.analysis.passwords.Password
import de.passwordvault.R
import de.passwordvault.ui.theme.LocalPasswordVaultColors
import de.passwordvault.view.utils.Utils


@Composable
fun WeakPasswordsTab(
    weakPasswords: List<Password>,
    onWeakPasswordClicked: (Password) -> Unit,
    thresholdGood: Float,
    thresholdNeutral: Float,
    maxSecurityScore: Int
) {
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


@Composable
private fun PasswordListRow(
    password: Password,
    onClick: (Password) -> Unit,
    thresholdGood: Float,
    thresholdNeutral: Float,
    maxSecurityScore: Int
) {
    var obfuscated: Boolean by remember { mutableStateOf(true) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
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
        if (password.securityScore > thresholdGood) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.image_m))
                    .clip(RoundedCornerShape(50))
                    .background(LocalPasswordVaultColors.current.backgroundGreen)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_ok_filled),
                    tint = LocalPasswordVaultColors.current.green,
                    contentDescription = ""
                )
            }
        }
        else if (password.securityScore > thresholdNeutral) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.image_m))
                    .clip(RoundedCornerShape(50))
                    .background(LocalPasswordVaultColors.current.backgroundYellow)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info_filled),
                    tint = LocalPasswordVaultColors.current.yellow,
                    contentDescription = ""
                )
            }
        }
        else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.image_m))
                    .clip(RoundedCornerShape(50))
                    .background(LocalPasswordVaultColors.current.backgroundRed)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_alert_filled),
                    tint = LocalPasswordVaultColors.current.red,
                    contentDescription = ""
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(R.dimen.space_horizontal_between))
        ) {
            Text(
                text = if (obfuscated) { Utils.obfuscate(password.cleartextPassword) } else { password.cleartextPassword },
                color = LocalPasswordVaultColors.current.text,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = password.name,
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
            }
        ) {
            Icon(
                painter = if (obfuscated) { painterResource(R.drawable.ic_show) } else { painterResource(R.drawable.ic_show_off) },
                tint = LocalPasswordVaultColors.current.text,
                contentDescription = ""
            )
        }
    }
}
