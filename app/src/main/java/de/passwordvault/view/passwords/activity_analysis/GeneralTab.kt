package de.passwordvault.view.passwords.activity_analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import de.passwordvault.R
import de.passwordvault.ui.composables.GenericTextButton
import de.passwordvault.ui.composables.GradientProgressBar
import de.passwordvault.ui.composables.Headline
import de.passwordvault.ui.theme.LocalPasswordVaultColors


/**
 * Composable displays the tab which informs the user about the general results of the password
 * analysis, like the average security score as well as important vulnerabilities.
 *
 * @param securityScore                 Average security score.
 * @param maxSecurityScore              Max possible security score.
 * @param thresholdGood                 Threshold with which a security score is considered 'good'.
 * @param thresholdNeutral              Threshold with which a security score is considered 'neutral'.
 * @param numberOfWeakPasswords         Number of weak passwords.
 * @param numberOfIdenticalPasswords    Number of identical passwords.
 * @param onWeakPasswordsClicked        Callback invoked once the button displaying weak passwords
 *                                      is clicked.
 * @param onIdenticalPasswordsClicked   Callback invoked once the button displaying identical
 *                                      passwords is clicked.
 */
@Composable
fun GeneralTab(
    securityScore: Double,
    maxSecurityScore: Int,
    numberOfWeakPasswords: Int,
    numberOfIdenticalPasswords: Int,
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
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
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
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
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
