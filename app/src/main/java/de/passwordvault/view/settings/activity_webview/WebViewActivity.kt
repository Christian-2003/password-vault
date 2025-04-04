package de.passwordvault.view.settings.activity_webview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import de.passwordvault.ui.theme.PasswordVaultTheme


/**
 * Class implements the activity displaying web content.
 *
 * @author  Christian-2003
 * @since   3.7.4
 */
class WebViewActivity: ComponentActivity() {

    /**
     * Method is called once the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var url: String? = null
        val args: Bundle? = intent.extras
        if (args != null && args.containsKey(EXTRA_URL)) {
            url = args.getString(EXTRA_URL, null)
        }
        if (url == null) {
            finish()
            return
        }

        setContent {
            PasswordVaultTheme {
                val viewModel: WebViewViewModel = viewModel()
                viewModel.init(url)

                WebViewScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        this@WebViewActivity.finish()
                    }
                )
            }
        }
    }


    companion object {

        /**
         * Field stores the key for the argument with which to pass the URL to display.
         */
        const val EXTRA_URL: String = "url"

    }

}
