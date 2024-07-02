package de.passwordvault.view.settings.activity_about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import de.passwordvault.BuildConfig;
import de.passwordvault.R;
import de.passwordvault.view.settings.activity_localized_asset_viewer.LocalizedAssetViewerActivity;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;


/**
 * Class implements the activity which shows the user information about the application.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class SettingsAboutActivity extends PasswordVaultBaseActivity {

    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_about);

        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Usage
        findViewById(R.id.settings_about_usage_tos_container).setOnClickListener(view -> showLegalPage("terms_of_service.html"));
        findViewById(R.id.settings_about_usage_dependencies_container).setOnClickListener(view -> startActivity(new Intent(this, OssLicensesMenuActivity.class)));
        findViewById(R.id.settings_about_usage_privacypolicy_container).setOnClickListener(view -> showLegalPage("privacy_policy.html"));

        //Software
        String version = BuildConfig.VERSION_NAME;
        if (BuildConfig.DEBUG) {
            version += " (Debug Build)";
        }
        ((TextView)findViewById(R.id.settings_about_software_version)).setText(version);

        findViewById(R.id.settings_about_software_more_container).setOnClickListener(view -> showSettingsPage());
    }


    /**
     * Method hows the legal page with the specified name to the user.
     *
     * @param name  Name of the page (e.g. "privacy_policy.html") to show to the user.
     */
    private void showLegalPage(String name) {
        Intent intent = new Intent(this, LocalizedAssetViewerActivity.class);
        intent.putExtra(LocalizedAssetViewerActivity.KEY_PAGE, name);
        intent.putExtra(LocalizedAssetViewerActivity.KEY_FOLDER, "legal");
        startActivity(intent);
    }


    /**
     * Method shows the app's page in the system settings.
     */
    private void showSettingsPage() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

}
