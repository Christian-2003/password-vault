package de.passwordvault.view.settings.activity_webview

import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient


/**
 * Implements a custom web view client that is used with the app.
 *
 * @author  Christian-2003
 * @since   3.7.4
 */
class MyWebViewClient(

    /**
     * Callback invoked once the page finishes loading. The callback passes the title of the page
     * as argument.
     */
    private val onPageFinished: (String) -> Unit

): WebViewClient() {

    /**
     * Called whenever a webpage finishes loading.
     *
     * @param view  Web view that finishes loading a webpage.
     * @param url   URL that has been loaded.
     */
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (view != null && view.title != null) {
            val title = view.title
            if (title != null) {
                onPageFinished(title)
            }
        }
        else {
            onPageFinished("")
        }
    }


    /**
     * Called when a URL is loaded.
     *
     * @param view      Web view that loads the URL.
     * @param request   Request.
     * @return          Whether the URL loading was overridden successfully.
     */
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (view != null && request != null) {
            val uri: Uri = request.url
            val scheme: String? = uri.scheme
            if (scheme != null && scheme == "mailto") {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                view.context.startActivity(intent)
                return true
            }
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

}
