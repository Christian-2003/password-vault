package de.passwordvault.view.settings.activity_about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.Calendar;
import de.passwordvault.BuildConfig;
import de.passwordvault.R;
import de.passwordvault.model.UpdateManager;
import de.passwordvault.view.general.dialog_more.Item;
import de.passwordvault.view.general.dialog_more.ItemButton;
import de.passwordvault.view.general.dialog_more.MoreDialog;
import de.passwordvault.view.general.dialog_more.MoreDialogCallback;
import de.passwordvault.view.settings.activity_licenses.LicensesActivity;
import de.passwordvault.view.settings.activity_localized_asset_viewer.LocalizedAssetViewerActivity;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the activity which shows the user information about the application.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class SettingsAboutActivity extends PasswordVaultActivity<ViewModel> implements MoreDialogCallback {

    /**
     * Field stores the tag for the more dialog to show the terms of service.
     */
    private static final String TAG_TOS = "tos";

    /**
     * Field stores the tag for the more dialog to show the privacy policy.
     */
    private static final String TAG_PRIVACY = "privacy";

    /**
     * Field stores the tag for the more dialog to show the list of dependencies.
     */
    private static final String TAG_DEPENDENCIES = "dependencies";


    /**
     * Constructor instantiates a new activity.
     */
    public SettingsAboutActivity() {
        super(null, R.layout.activity_settings_about);
    }


    /**
     * @param dialog   Dialog in which the action was invoked.
     * @param tag      Tag from the {@link Item} whose action is invoked.
     * @param position Position of the {@link Item} within the dialog.
     */
    @Override
    public void onDialogItemClicked(MoreDialog dialog, String tag, int position) {
        switch (tag) {
            case TAG_TOS: {
                showLegalPage("terms_of_service.html");
                break;
            }
            case TAG_PRIVACY: {
                showLegalPage("privacy_policy.html");
                break;
            }
            case TAG_DEPENDENCIES: {
                startActivity(new Intent(this, LicensesActivity.class));
                break;
            }
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

        findViewById(R.id.button_back).setOnClickListener(view -> finish());
        findViewById(R.id.button_more).setOnClickListener(view -> onShowMoreDialog());

        //Software:
        String version = BuildConfig.VERSION_NAME;
        if (BuildConfig.DEBUG) {
            version += " (Debug Build)";
        }
        ((TextView)findViewById(R.id.settings_about_software_version)).setText(getString(R.string.settings_about_software_version).replace("{arg}", version));
        String copyright = "" + Calendar.getInstance().get(Calendar.YEAR);
        ((TextView)findViewById(R.id.settings_about_software_copyright)).setText(getString(R.string.settings_about_software_copyright).replace("{arg}", copyright));

        //GitHub
        findViewById(R.id.settings_about_github_repo_container).setOnClickListener(view -> openUrl(getString(R.string.settings_about_github_repo_link)));
        findViewById(R.id.settings_about_github_issues_container).setOnClickListener(view -> openUrl(getString(R.string.settings_about_github_issues_link)));
        if (UpdateManager.getInstance(this).isUpdateAvailable()) {
            LinearLayout updateContainer = findViewById(R.id.settings_about_github_update_container);
            updateContainer.setVisibility(View.VISIBLE);
            updateContainer.setOnClickListener(view -> UpdateManager.getInstance(this).requestDownload(this));
        }

        //Software
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


    private void onShowMoreDialog() {
        Bundle args = new Bundle();
        args.putString(MoreDialog.ARG_TITLE , getString(R.string.settings_about));
        args.putInt(MoreDialog.ARG_ICON, R.drawable.ic_launcher_foreground_noscale);
        ArrayList<Item> items = new ArrayList<>();
        items.add(new ItemButton(getString(R.string.settings_about_usage_tos), TAG_TOS, R.drawable.ic_legal));
        items.add(new ItemButton(getString(R.string.settings_about_usage_privacypolicy), TAG_PRIVACY, R.drawable.ic_privacy));
        items.add(new ItemButton(getString(R.string.settings_about_usage_dependencies), TAG_DEPENDENCIES, R.drawable.ic_license));
        args.putSerializable(MoreDialog.ARG_ITEMS, items);

        MoreDialog dialog = new MoreDialog();
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

}
