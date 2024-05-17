package de.passwordvault.view.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
 * @version 3.5.4
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
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localized_asset_viewer);

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

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

        //Load help page:
        try {
            LocalizedAssetManager assetManager = new LocalizedAssetManager(folder, Locale.getDefault());

            WebView webView = findViewById(R.id.web_view);
            webView.setWebViewClient(new CustomWebViewClient());
            webView.loadUrl(assetManager.getFileUri(filename));
        }
        catch (Exception e) {
            finish();
            return;
        }
    }

}