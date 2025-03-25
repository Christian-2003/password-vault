package de.passwordvault.view.settings.activity_about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import de.passwordvault.model.storage.settings.Config
import de.passwordvault.ui.theme.PasswordVaultTheme
import de.passwordvault.view.settings.activity_licenses.LicensesActivity
import de.passwordvault.view.settings.activity_webview.WebviewActivity


/**
 * Class implements the activity displaying the settings about the app.
 *
 * @author  Christian-2003
 * @since   3.7.4
 */
class SettingsAboutActivity: ComponentActivity() {

    /**
     * Method is called once the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PasswordVaultTheme {
                val viewModel: SettingsAboutViewModel = viewModel()
                viewModel.init()

                SettingsAboutScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        this@SettingsAboutActivity.finish()
                    },
                    onNavigateToLicenses = {
                        val intent = Intent(this@SettingsAboutActivity, LicensesActivity::class.java)
                        startActivity(intent)
                    },
                    onOpenUrl = { url ->
                        openUrlInBrowserOrApp(url)
                    }
                )
            }
        }
    }


    /**
     * Method opens the specified URL either in a browser, or within the app-internal web view.
     * This depends on the settings of the user within the app.
     *
     * @param url   URL to open.
     */
    private fun openUrlInBrowserOrApp(url: String) {
        if (Config.getInstance().openResourcesInBrowser.get()) {
            val uri = try {
                Uri.parse(url)
            } catch (e: Exception) {
                return
            }

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(uri)
            startActivity(intent)
        }
        else {
            val intent = Intent(this, WebviewActivity::class.java)
            intent.putExtra(WebviewActivity.EXTRA_URL, url)
            startActivity(intent)
        }
    }

}
