package de.passwordvault.view.settings.activity_quality_gates

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import de.passwordvault.model.analysis.QualityGateManager
import de.passwordvault.ui.theme.PasswordVaultTheme


class QualityGatesActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PasswordVaultTheme {
                val viewModel: QualityGatesViewModel = viewModel()
                viewModel.init(
                    qualityGateManager = QualityGateManager.getInstance()
                )

                QualityGatesScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        this@QualityGatesActivity.finish()
                    }
                )
            }
        }
    }

}
