package de.passwordvault.view.settings.activity_licenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import de.passwordvault.ui.theme.PasswordVaultTheme


/**
 * Class implements the activity displaying a list of software used by the app.
 *
 * @author  Christian-2003
 * @since   3.7.4
 */
class LicensesActivity: ComponentActivity() {

    /**
     * Method is called once the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PasswordVaultTheme {
                val viewModel: LicensesViewModel = viewModel()
                viewModel.init()

                LicensesScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        this@LicensesActivity.finish()
                    }
                )
            }
        }
    }

}
