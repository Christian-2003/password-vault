package de.passwordvault.view.settings.activity_localized_asset_viewer;

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
import java.util.Locale;
import de.passwordvault.R;
import de.passwordvault.model.storage.LocalizedAssetManager;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;


/**
 * Class implements an activity that can display a localized HTML assets within a {@link WebView}.
 * The filename of the asset that shall be displayed needs to be passed as extra with key
 * {@link #KEY_PAGE}. The folder of the corresponding filename needs to be passed as extra with
 * kex {@link #KEY_FOLDER}.
 * For further information about localized assets, see {@link LocalizedAssetManager}.
 *
 * @author  Christian-2003
 * @version 3.5.6
 */
public class LocalizedAssetViewerActivity extends PasswordVaultBaseActivity {

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
            TextView activityTitleTextView = LocalizedAssetViewerActivity.this.findViewById(R.id.title);
            activityTitleTextView.setText(view.getTitle());
            LocalizedAssetViewerActivity.this.progressBar.setVisibility(View.GONE);
            LocalizedAssetViewerActivity.this.webView.setVisibility(View.VISIBLE);
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
     * Field stores the key with which to pass the filename of the page to open.
     */
    public static final String KEY_PAGE = "filename";

    /**
     * Field stores the key with which to pass the folder name of the page to open.
     */
    public static final String KEY_FOLDER = "folder";


    /**
     * Attribute stores the progress bar displayed while the web page loads.
     */
    private ProgressBar progressBar;

    /**
     * Attribute stores the web view displaying the web page.
     */
    private WebView webView;


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localized_asset_viewer);

        findViewById(R.id.button_back).setOnClickListener(view -> finish());
        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);

        //Get filename:
        String filename = null;
        String folder = null;
        Bundle args = getIntent().getExtras();
        if (args != null) {
            if (args.containsKey(KEY_PAGE)) {
                filename = args.getString(KEY_PAGE, null);
            }
            if (args.containsKey(KEY_FOLDER)) {
                folder = args.getString(KEY_FOLDER, null);
            }
        }
        if (filename == null || folder == null) {
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);

        //Load help page:
        try {
            LocalizedAssetManager assetManager = new LocalizedAssetManager(folder, Locale.getDefault());
            webView.setWebViewClient(new CustomWebViewClient());
            webView.loadUrl(assetManager.getFileUri(filename));
        }
        catch (Exception e) {
            finish();
        }
    }

}
