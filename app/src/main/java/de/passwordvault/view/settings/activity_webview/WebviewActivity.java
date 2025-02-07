package de.passwordvault.view.settings.activity_webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements an activity that can display a web page within the app withou opening an external
 * browser. Pass the URL to display as argument with key {@link #EXTRA_URL}.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class WebviewActivity extends PasswordVaultActivity<ViewModel> {

    /**
     * Class implements a custom web view client for the help activity.
     */
    private class CustomWebViewClient extends WebViewClient {

        /**
         * Method is called whenever the web page finishes loading.
         *
         * @param view  WebView that finished loading.
         * @param url   URL that was loaded.
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            TextView activityTitleTextView = WebviewActivity.this.findViewById(R.id.title);
            activityTitleTextView.setText(view.getTitle());
            WebviewActivity.this.progressBar.setVisibility(View.GONE);
            WebviewActivity.this.webView.setVisibility(View.VISIBLE);
        }


        /**
         * Method is called whenever a URL from within the web view is loaded to determine, whether
         * the WebView can load the URL or whether the app should start an intent to handle the URL.
         *
         * @param view      Web view from which the URL was loaded.
         * @param request   Web request for which to determine, whether the web view or an intent
         *                  shall be used.
         * @return          Whether the web view can load the URL (= {@code true}) or whether an
         *                  intent is required (= {@code false}).
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri url = request.getUrl();
            String urlScheme = url.getScheme();
            if (urlScheme != null && urlScheme.equals("mailto")) {
                //Mailto:
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, url));
                return true;
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

    }


    /**
     * Field stores the key for the argument with which to pass the URL to display.
     */
    public static final String EXTRA_URL = "url";


    /**
     * Attribute stores the progress bar displayed while the web page loads.
     */
    private ProgressBar progressBar;

    /**
     * Attribute stores the web view displaying the web page.
     */
    private WebView webView;


    public WebviewActivity() {
        super(null, R.layout.activity_webview);
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.button_back).setOnClickListener(view -> finish());
        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);

        //Get filename:
        String url = null;
        Bundle args = getIntent().getExtras();
        if (args != null && args.containsKey(EXTRA_URL)) {
            url = args.getString(EXTRA_URL, null);
        }
        if (url == null) {
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);

        //Load help page:
        try {
            webView.setWebViewClient(new CustomWebViewClient());
            webView.loadUrl(url);
        }
        catch (Exception e) {
            finish();
        }
    }

}
