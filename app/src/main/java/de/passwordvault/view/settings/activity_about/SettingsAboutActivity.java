package de.passwordvault.view.settings.activity_about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Calendar;
import de.passwordvault.BuildConfig;
import de.passwordvault.R;
import de.passwordvault.model.rest.RestCallback;
import de.passwordvault.model.rest.RestError;
import de.passwordvault.model.rest.legal.LocalizedLegalPage;
import de.passwordvault.view.settings.activity_licenses.LicensesActivity;
import de.passwordvault.view.settings.activity_webview.WebviewActivity;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the activity which shows the user information about the application.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class SettingsAboutActivity extends PasswordVaultActivity<SettingsAboutViewModel> implements RestCallback {

    /**
     * Attribute stores the container showing the privacy policy.
     */
    private LinearLayout privacyContainer;

    /**
     * Attribute stores the container showing the terms of service.
     */
    private LinearLayout tosContainer;


    /**
     * Constructor instantiates a new activity.
     */
    public SettingsAboutActivity() {
        super(SettingsAboutViewModel.class, R.layout.activity_settings_about);
    }



    /**
     * Method is called once the data has been fetched.
     *
     * @param error Error generated during the call to the REST API.
     */
    @Override
    public void onFetchFinished(@Nullable String tag,@NonNull RestError error) {
        if (tag != null) {
            runOnUiThread(() -> {
                if (tag.equals(SettingsAboutViewModel.TAG_PRIVACY)) {
                    //Privacy policy:
                    viewModel.setPrivacyError(error);
                    privacyContainer.setVisibility(viewModel.getPrivacyError() == RestError.SUCCESS ? View.VISIBLE : View.GONE);
                }
                else if (tag.equals(SettingsAboutViewModel.TAG_TOS)) {
                    //Terms of service:
                    viewModel.setTosError(error);
                    tosContainer.setVisibility(viewModel.getTosError() == RestError.SUCCESS ? View.VISIBLE : View.GONE);
                }
            });
        }
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_about);

        //Menu bar:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Software
        String version = BuildConfig.VERSION_NAME;
        if (BuildConfig.DEBUG) {
            version += " (Debug Build)";
        }
        String copyright = "" + Calendar.getInstance().get(Calendar.YEAR);
        ((TextView)findViewById(R.id.settings_about_software_version)).setText(getString(R.string.settings_about_software_version).replace("{arg}", version));
        ((TextView)findViewById(R.id.settings_about_software_copyright)).setText(getString(R.string.settings_about_software_copyright).replace("{arg}", copyright));
        findViewById(R.id.container_more).setOnClickListener(view -> showSettingsPage());

        //GitHub
        findViewById(R.id.container_repo).setOnClickListener(view -> openUrl(getString(R.string.settings_about_github_repo_link)));
        findViewById(R.id.container_issues).setOnClickListener(view -> openUrl(getString(R.string.settings_about_github_issues_link)));

        //Legal
        findViewById(R.id.container_dependencies).setOnClickListener(view -> startActivity(new Intent(this, LicensesActivity.class)));
        privacyContainer = findViewById(R.id.container_privacy);
        privacyContainer.setVisibility(viewModel.getPrivacyError() == RestError.SUCCESS ? View.VISIBLE : View.GONE);
        privacyContainer.setOnClickListener(view -> showLegalPage(viewModel.getPrivacyPage()));
        tosContainer = findViewById(R.id.container_tos);
        tosContainer.setVisibility(viewModel.getTosError() == RestError.SUCCESS ? View.VISIBLE : View.GONE);
        tosContainer.setOnClickListener(view -> showLegalPage(viewModel.getTosPage()));

        //Fetch data:
        viewModel.fetchData(this);
    }



    /**
     * Method hows the legal page whose position is passed to the user.
     *
     * @param page  Legal page to show.
     */
    private void showLegalPage(LocalizedLegalPage page) {
        openUrlInBrowserOrApp(page.getUrl());
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
