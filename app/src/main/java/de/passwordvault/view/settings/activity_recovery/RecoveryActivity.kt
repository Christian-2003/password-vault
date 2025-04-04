package de.passwordvault.view.settings.activity_recovery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import de.passwordvault.model.security.login.Account
import de.passwordvault.ui.theme.PasswordVaultTheme


class RecoveryActivity: ComponentActivity() {

    /**
     * Method is called once the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PasswordVaultTheme {
                val viewModel: RecoveryViewModel = viewModel()
                viewModel.init(Account.getInstance())

                RecoveryScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        this@RecoveryActivity.finish()
                    }
                )
            }
        }
    }

}
