package de.passwordvault.view.settings.activity_help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.rest.RestCallback;
import de.passwordvault.model.rest.RestError;
import de.passwordvault.model.rest.help.LocalizedHelpPage;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the help activity displaying all help pages to the user.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class SettingsHelpActivity extends PasswordVaultActivity<SettingsHelpViewModel> implements RestCallback {

    /**
     * Attribute stores the recycler view adapter for the activity.
     */
    private SettingsHelpRecyclerViewAdapter adapter;

    /**
     * Attribute stores the recycler view of the activity.
     */
    private RecyclerView recyclerView;

    /**
     * Attribute stores the progress bar indicating that data is being fetched.
     */
    private ProgressBar progressBar;


    /**
     * Constructor instantiates a new activity.
     */
    public SettingsHelpActivity() {
        super(SettingsHelpViewModel.class, R.layout.activity_settings_help);
    }


    /**
     * Method is called once the data has been fetched.
     *
     * @param tag   Tag used with the REST client.
     * @param error Error generated during the call to the REST API.
     */
    @Override
    public void onFetchFinished(@Nullable String tag, @NonNull RestError error) {
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

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Recycler view:
        adapter = new SettingsHelpRecyclerViewAdapter(this, viewModel);
        adapter.setHelpPageClickListener(this::showHelpPage);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);

        //Fetch data:
        viewModel.fetchData(this);
    }


    /**
     * Method hows the help page whose position is passed to the user.
     *
     * @param position  Position of the localized help page to show to the user.
     */
    private void showHelpPage(int position) {
        LocalizedHelpPage helpPage = viewModel.getHelpPages().get(position - SettingsHelpRecyclerViewAdapter.OFFSET_HELP_PAGES);
        Uri uri;
        try {
            uri = Uri.parse(helpPage.getUrl());
        }
        catch (Exception e) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
    }

}
