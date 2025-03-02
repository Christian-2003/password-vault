package de.passwordvault.view.passwords.activity_analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import de.passwordvault.R
import de.passwordvault.ui.composables.GenericTextButton
import de.passwordvault.ui.composables.GradientProgressBar
import de.passwordvault.ui.composables.Headline
import de.passwordvault.ui.theme.LocalPasswordVaultColors
import de.passwordvault.view.utils.Utils


@Composable
fun GeneralTab(
    securityScore: Double,
    maxSecurityScore: Int,
    thresholdGood: Float,
    thresholdNeutral: Float,
    numberOfAnalyzedPasswords: Int,
    numberOfWeakPasswords: Int,
    numberOfIdenticalPasswords: Int,
    onAnalyzedPasswordsClicked: () -> Unit,
    onWeakPasswordsClicked: () -> Unit,
    onIdenticalPasswordsClicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Headline(title = stringResource(R.string.password_results_general_title))
        Text(
            text = stringResource(R.string.password_results_general_average_score),
            color = LocalPasswordVaultColors.current.text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.space_horizontal),
                    vertical = dimensionResource(R.dimen.space_vertical)
                )
        )
        Text(
            text = stringResource(R.string.password_results_general_average_score_display)
                .replace("{arg}", "" + Utils.formatNumber(securityScore))
                .replace("{max}", "" + maxSecurityScore),
            fontWeight = FontWeight.Bold,
            color = if (securityScore > thresholdGood) {
                LocalPasswordVaultColors.current.green
            } else if (securityScore > thresholdNeutral) {
                LocalPasswordVaultColors.current.yellow
            } else {
                LocalPasswordVaultColors.current.red
            },
            modifier = Modifier
                .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_l)))
                .background(LocalPasswordVaultColors.current.backgroundContainer)
                .padding(
                    horizontal = dimensionResource(R.dimen.space_horizontal),
                    vertical = dimensionResource(R.dimen.space_vertical)
                )
        )
        GradientProgressBar(
            progress = securityScore.toFloat() / maxSecurityScore,
            colors = listOf(
                LocalPasswordVaultColors.current.red,
                LocalPasswordVaultColors.current.yellow,
                LocalPasswordVaultColors.current.green
            ),
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.space_horizontal),
                vertical = dimensionResource(R.dimen.space_vertical)
            )
        )
        GenericTextButton(
            title = stringResource(R.string.password_results_general_analyzed),
            description = if (numberOfAnalyzedPasswords == 1) {
                stringResource(R.string.password_results_general_analyzed_hint_singular).replace("{arg}","" + numberOfAnalyzedPasswords)
            } else {
                stringResource(R.string.password_results_general_analyzed_hint).replace("{arg}","" + numberOfAnalyzedPasswords)
            },
            onClick = onAnalyzedPasswordsClicked
        )
        HorizontalDivider()
        Headline(title = stringResource(R.string.password_results_general_problems))
        GenericTextButton(
            title = stringResource(R.string.password_results_general_duplicates),
            description = if (numberOfIdenticalPasswords == 1) {
                stringResource(R.string.password_results_general_duplicates_hint_singular).replace("{arg}","" + numberOfIdenticalPasswords)
            } else {
                stringResource(R.string.password_results_general_duplicates_hint).replace("{arg}","" + numberOfIdenticalPasswords)
            },
            onClick = onIdenticalPasswordsClicked
        )
        GenericTextButton(
            title = stringResource(R.string.password_results_general_weak),
            description = if (numberOfWeakPasswords == 1) {
                stringResource(R.string.password_results_general_weak_hint_singular).replace("{arg}","" + numberOfWeakPasswords)
            } else {
                stringResource(R.string.password_results_general_weak_hint).replace("{arg}","" + numberOfWeakPasswords)
            },
            onClick = onWeakPasswordsClicked
        )
    }
}
