package de.passwordvault.view.settings.activity_webview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


/**
 * Implements the view model for the screen displaying web pages within the app.
 *
 * @author  Christian-2003
 * @since   3.7.4
 */
class WebViewViewModel: ViewModel() {

    /**
     * Initial URL that is being displayed with the web view.
     */
    lateinit var url: String

    /**
     * Title of the web page displayed. While loading a page, this is null.
     */
    var title: String? by mutableStateOf(null)


    /**
     * Initializes the view model.
     *
     * @param url   Initial URL for the web view.
     */
    fun init(url: String) {
        this.url = url
        title = null
    }


    /**
     * Method is called whenever the web view finishes loading a page.
     */
    fun onPageFinished(title: String) {
        this.title = title
    }

}
