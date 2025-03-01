package de.passwordvault.view.passwords.activity_analysis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import de.passwordvault.model.analysis.QualityGateManager
import de.passwordvault.model.entry.EntryManager
import de.passwordvault.ui.theme.PasswordVaultTheme


class PasswordAnalysisActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PasswordVaultTheme {
                val viewModel: PasswordAnalysisViewModel = viewModel()
                viewModel.init(EntryManager.getInstance(), QualityGateManager.getInstance())

                PasswordAnalysisScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        this@PasswordAnalysisActivity.finish()
                    }
                )
            }
        }
    }

}
