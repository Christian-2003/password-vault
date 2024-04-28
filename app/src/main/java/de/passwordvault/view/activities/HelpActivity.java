package de.passwordvault.view.activities;

import android.os.Bundle;
import android.webkit.WebView;
import androidx.annotation.Nullable;
import java.util.Locale;
import de.passwordvault.R;
import de.passwordvault.model.storage.LocalizedAssetManager;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;


public class HelpActivity extends PasswordVaultBaseActivity {

    /**
     * Attribute stores the key with which to pass the filename of the page to open.
     */
    public static final String KEY_PAGE = "filename";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Get filename:
        String filename = null;
        Bundle args = getIntent().getExtras();
        if (args != null && args.containsKey(KEY_PAGE)) {
            filename = args.getString(KEY_PAGE, null);
        }
        if (filename == null) {
            finish();
            return;
        }

        try {
            LocalizedAssetManager assetManager = new LocalizedAssetManager("help", Locale.getDefault());

            WebView webView = findViewById(R.id.web_view);
            webView.loadUrl(assetManager.getFileUri(filename));
        }
        catch (Exception e) {
            finish();
            return;
        }
    }

}
