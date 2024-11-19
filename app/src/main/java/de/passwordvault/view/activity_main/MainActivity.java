package de.passwordvault.view.activity_main;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import de.passwordvault.R;
import de.passwordvault.model.UpdateManager;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.view.activity_search.SearchActivity;
import de.passwordvault.view.entries.activity_entry.EntryActivity;
import de.passwordvault.view.settings.activity_settings.SettingsActivity;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the MainActivity for this application, displaying a list of all entries.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class MainActivity extends PasswordVaultActivity<MainViewModel> implements UpdateManager.UpdateStatusChangedCallback {

    /**
     * Attribute stores the adapter for the recycler view which displays the abbreviated entries.
     */
    private MainRecyclerViewAdapter adapter;

    /**
     * Attribute stores the launcher used to start the {@link EntryActivity}.
     */
    private final ActivityResultLauncher<Intent> entryLauncher;

    /**
     * Attribute stores the launcher used to launch the {@link SettingsActivity}.
     */
    private final ActivityResultLauncher<Intent> settingsLauncher;

    /**
     * Attribute stores the launcher used to launch the {@link SearchActivity}.
     */
    private final ActivityResultLauncher<Intent> searchLauncher;

    /**
     * Attribute stores the progress bar displaying that entries are being loaded.
     */
    private ProgressBar progressBar;

    /**
     * Attribute stores the recycler view displaying the main page content.
     */
    private RecyclerView recyclerView;


    /**
     * Constructor instantiates a new activity.
     */
    public MainActivity() {
        super(MainViewModel.class, R.layout.activity_main);

        //Show entry:
        entryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (adapter != null) {
                int adapterPosition = viewModel.getOpenedEntryAdapterPosition();
                if (adapterPosition >= MainRecyclerViewAdapter.OFFSET_ENTRIES && adapterPosition <= MainRecyclerViewAdapter.OFFSET_ENTRIES + adapter.getItemCount()) {
                    if (result.getResultCode() == EntryActivity.RESULT_DELETED) {
                        adapter.notifyItemRemoved(adapterPosition);
                    }
                    else if (result.getResultCode() == EntryActivity.RESULT_EDITED) {
                        adapter.notifyDataSetChanged(); //Need to update entire adapter since data is sorted alphabetically
                    }
                }
                else if (adapterPosition == -1 && result.getResultCode() == EntryActivity.RESULT_EDITED) {
                    adapter.notifyDataSetChanged(); //Need to update entire adapter since data is sorted alphabetically
                }
            }
        });

        settingsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (viewModel.isChangedSinceLastCacheGeneration() && adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });

        searchLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == SearchActivity.RESULT_DELETED && adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * Callback is invoked when the update manager determines whether a new update is available or
     * not.
     *
     * @param updateAvailable   Whether an update is available.
     */
    @Override
    public void onUpdateStatusChanged(boolean updateAvailable) {
        if (updateAvailable) {
            viewModel.updateAvailable();
            if (adapter != null) {
                runOnUiThread(() -> {
                    adapter.notifyItemChanged(MainRecyclerViewAdapter.POSITION_UPDATE_INFO);
                });
            }
        }
    }


    /**
     * Method is called whenever the MainActivity is created or recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Add action listener to FAB:
        findViewById(R.id.main_fab).setOnClickListener(view -> onAddEntry());

        //Setup app bar:
        findViewById(R.id.button_settings).setOnClickListener(view -> settingsLauncher.launch(new Intent(this, SettingsActivity.class)));
        findViewById(R.id.search_bar).setOnClickListener(view -> searchLauncher.launch(new Intent(this, SearchActivity.class)));

        //Load entries:
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        viewModel.loadAllEntries(this::onEntriesLoaded, false);

        UpdateManager.getInstance(this, this);
    }


    /**
     * Method is called whenever the MainActivity is destroyed. This is usually done whenever the
     * application is closed.
     */
    @Override
    protected void onStop() {
        super.onStop();
        //Sometimes, when the app crashes (to me, this only happened during testing and development -
        //never production!), all data is, for some strange reason that I do not comprehend or understand,
        //deleted from the EntryManager. Therefore, all data would be lost when the app crashes. Not
        //saving data when the EntryManager is empty prevents this from happening. However, this also
        //prevents the user from manually deleting the last existing entry. As for now, I am willing
        //to accept this drawback.
        if (!EntryManager.getInstance().isEmpty()) {
            try {
                EntryManager.getInstance().save();
            }
            catch (Exception e) {
                Log.d("EntryManager", "Could not save entries from MainActivity: " + e.getMessage());
            }
        }
    }


    /**
     * Method is called whenever an entry is clicked.
     *
     * @param position  Adapter position of the entry clicked.
     */
    private void onEntryClicked(int position) {
        EntryAbbreviated entry = adapter.getEntryForAdapterPosition(position);
        if (entry != null) {
            Intent intent = new Intent(this, EntryActivity.class);
            intent.putExtra(EntryActivity.KEY_ID, entry.getUuid());
            viewModel.setOpenedEntryAdapterPosition(position);
            entryLauncher.launch(intent);
        }
    }


    /**
     * Method is called whenever to add a new entry.
     */
    private void onAddEntry() {
        Intent intent = new Intent(this, EntryActivity.class);
        viewModel.setOpenedEntryAdapterPosition(-1);
        entryLauncher.launch(intent);
    }


    /**
     * Method requests to download the newest version of the app.
     *
     * @param position  Adapter position which invoked the click event.
     */
    private void onUpdateClicked(int position) {
        UpdateManager.getInstance(this).requestDownload(this);
    }


    /**
     * Method is called as callback once all entries are loaded and available to display.
     */
    private void onEntriesLoaded() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new MainRecyclerViewAdapter(this, viewModel);
            adapter.setItemClickListener(this::onEntryClicked);
            adapter.setUpdateClickListener(this::onUpdateClicked);
            recyclerView.setAdapter(adapter);
        });
    }

}
