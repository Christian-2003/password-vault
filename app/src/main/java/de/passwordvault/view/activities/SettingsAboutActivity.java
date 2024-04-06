package de.passwordvault.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import de.passwordvault.BuildConfig;
import de.passwordvault.R;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;


/**
 * Class implements the activity which shows the user information about the application.
 *
 * @author  Christian-2003
 * @version 3.5.1
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
        findViewById(R.id.settings_about_usage_license_container).setOnClickListener(view -> showInfoDialog(R.string.settings_about_usage_license, Utils.readRawResource(R.raw.license)));
        findViewById(R.id.settings_about_usage_dependencies_container).setOnClickListener(view -> startActivity(new Intent(this, OssLicensesMenuActivity.class)));

        //GitHub
        findViewById(R.id.settings_about_github_repository_container).setOnClickListener(view -> openUrl(getString(R.string.settings_about_github_link)));
        findViewById(R.id.settings_about_github_issues_container).setOnClickListener(view -> openUrl(getString(R.string.settings_about_bug_link)));

        //Software
        String version = BuildConfig.VERSION_NAME;
        if (BuildConfig.DEBUG) {
            version += " (Debug Build)";
        }
        ((TextView)findViewById(R.id.settings_about_software_version)).setText(version);
        findViewById(R.id.settings_about_software_update_container).setOnClickListener(view -> openUrl(getString(R.string.settings_about_update_link)));
    }

}
