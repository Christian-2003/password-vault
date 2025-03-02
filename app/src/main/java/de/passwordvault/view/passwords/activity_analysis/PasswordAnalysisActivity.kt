package de.passwordvault.view.passwords.activity_analysis

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import de.passwordvault.model.analysis.QualityGateManager
import de.passwordvault.model.entry.EntryManager
import de.passwordvault.ui.theme.PasswordVaultTheme
import de.passwordvault.view.entries.activity_entry.EntryActivity


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
                    },
                    onNavigateToEntry = { uuid ->
                        val intent = Intent(this@PasswordAnalysisActivity, EntryActivity::class.java)
                        intent.putExtra(EntryActivity.KEY_ID, uuid)
                        startActivity(intent)
                    }
                )
            }
        }
    }

}
