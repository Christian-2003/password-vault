package de.passwordvault.view.settings.activity_about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.rest.RestCallback;
import de.passwordvault.model.rest.RestError;
import de.passwordvault.model.rest.legal.LocalizedLegalPage;
import de.passwordvault.view.general.dialog_more.Item;
import de.passwordvault.view.general.dialog_more.ItemButton;
import de.passwordvault.view.general.dialog_more.ItemDivider;
import de.passwordvault.view.general.dialog_more.MoreDialog;
import de.passwordvault.view.general.dialog_more.MoreDialogCallback;
import de.passwordvault.view.settings.activity_licenses.LicensesActivity;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the activity which shows the user information about the application.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class SettingsAboutActivity extends PasswordVaultActivity<SettingsAboutViewModel> implements MoreDialogCallback, RestCallback {

    /**
     * Field stores the tag for the more dialog to show the list of dependencies.
     */
    private static final String TAG_DEPENDENCIES = "dependencies";

    /**
     * Field stores the tag for the more dialog to show the GitHub repository.
     */
    private static final String TAG_REPOSITORY = "repo";

    /**
     * Field stores the tag for the more dialog to show the GitHub issues.
     */
    private static final String TAG_ISSUES = "issues";

    /**
     * Field stores the tag for the more dialog to show the app in system settings.
     */
    private static final String TAG_SETTINGS = "settings";


    /**
     * Attribute stores the adapter for the activity.
     */
    private SettingsAboutRecyclerViewAdapter adapter;

    /**
     * Attribute stores the recycler view.
     */
    private RecyclerView recyclerView;

    /**
     * Attribute stores the progress bar.
     */
    private ProgressBar progressBar;


    /**
     * Constructor instantiates a new activity.
     */
    public SettingsAboutActivity() {
        super(SettingsAboutViewModel.class, R.layout.activity_settings_about);
    }


    /**
     * @param dialog   Dialog in which the action was invoked.
     * @param tag      Tag from the {@link Item} whose action is invoked.
     * @param position Position of the {@link Item} within the dialog.
     */
    @Override
    public void onDialogItemClicked(MoreDialog dialog, String tag, int position) {
        switch (tag) {
            case TAG_DEPENDENCIES: {
                startActivity(new Intent(this, LicensesActivity.class));
                break;
            }
            case TAG_REPOSITORY: {
                openUrl(getString(R.string.settings_about_github_repo_link));
                break;
            }
            case TAG_ISSUES: {
                openUrl(getString(R.string.settings_about_github_issues_link));
                break;
            }
            case TAG_SETTINGS: {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                break;
            }
        }
    }


    /**
     * Method is called once the data has been fetched.
     *
     * @param error Error generated during the call to the REST API.
     */
    @Override
    public void onFetchFinished(@NonNull RestError error) {
        if (viewModel.isFinished()) {
            viewModel.setError(error);
            Log.d("REST", "Error code: " + error.ordinal());
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
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

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        //Menu bar:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());
        findViewById(R.id.button_more).setOnClickListener(view -> onShowMoreDialog());

        //Recycler view:
        adapter = new SettingsAboutRecyclerViewAdapter(this, viewModel);
        adapter.setLegalPageClickListener(this::showLegalPage);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);

        //Fetch data:
        viewModel.fetchData(this);
    }



    private void onShowMoreDialog() {
        Bundle args = new Bundle();
        args.putString(MoreDialog.ARG_TITLE , getString(R.string.settings_about));
        args.putInt(MoreDialog.ARG_ICON, R.drawable.ic_launcher_foreground_noscale);
        ArrayList<Item> items = new ArrayList<>();
        items.add(new ItemButton(getString(R.string.settings_about_usage_dependencies), TAG_DEPENDENCIES, R.drawable.ic_license));
        items.add(new ItemDivider());
        items.add(new ItemButton(getString(R.string.settings_about_github_repo), TAG_REPOSITORY, R.drawable.ic_repository));
        items.add(new ItemButton(getString(R.string.settings_about_github_issues), TAG_ISSUES, R.drawable.ic_bug));
        items.add(new ItemDivider());
        items.add(new ItemButton(getString(R.string.settings_about_software_more), TAG_SETTINGS, R.drawable.ic_settings));
        args.putSerializable(MoreDialog.ARG_ITEMS, items);

        MoreDialog dialog = new MoreDialog();
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }


    /**
     * Method hows the legal page whose position is passed to the user.
     *
     * @param position  Position of the localized legal page to show to the user.
     */
    private void showLegalPage(int position) {
        LocalizedLegalPage legalPage = viewModel.getLegalPages().get(position - SettingsAboutRecyclerViewAdapter.OFFSET_LEGAL_PAGES);
        Uri uri;
        try {
            uri = Uri.parse(legalPage.getUrl());
        }
        catch (Exception e) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
    }

}
