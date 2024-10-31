package de.passwordvault.view.activity_main;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import de.passwordvault.R;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.view.entries.activity_add_entry.AddEntryActivity;
import de.passwordvault.view.entries.activity_entry.EntryActivity;
import de.passwordvault.view.settings.activity_settings.SettingsActivity;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the MainActivity for this application. This MainActivity resembles the starting
 * point for the application and contains multiple fragments with different functionalities.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class MainActivity extends PasswordVaultActivity<MainViewModel> {

    /**
     * Attribute stores the adapter for the recycler view which displays the abbreviated entries.
     */
    private MainRecyclerViewAdapter adapter;

    /**
     * Attribute stores the launcher used to start the {@link EntryActivity}.
     */
    private ActivityResultLauncher<Intent> entryLauncher;

    /**
     * Attribute stores the activity result launcher used to start the activity through which to add
     * a new entry.
     */
    private final ActivityResultLauncher<Intent> addEntryLauncher;


    /**
     * Constructor instantiates a new activity.
     */
    public MainActivity() {
        super(MainViewModel.class, R.layout.activity_main);

        //Show entry:
        entryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            int adapterPosition = viewModel.getOpenedEntryAdapterPosition();
            if (adapterPosition >= MainRecyclerViewAdapter.OFFSET_ENTRIES && adapterPosition <= MainRecyclerViewAdapter.OFFSET_ENTRIES + adapter.getItemCount()) {
                if (result.getResultCode() == EntryActivity.RESULT_DELETED) {
                    adapter.notifyItemRemoved(adapterPosition);
                }
                else if (result.getResultCode() == EntryActivity.RESULT_EDITED) {
                    adapter.notifyItemChanged(adapterPosition);
                }
            }
        });

        //Add entry:
        addEntryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.d("AddEntry", "MainActivity: ResultCode=" + result.getResultCode());
            if (result != null && result.getResultCode() == RESULT_OK) {
                //viewModel.getHomeFragment().update(EntryManager.getInstance());
                //viewModel.getEntriesFragment().updateDataset();
            }
        });
    }


    /**
     * Method starts the activity which allows the user to add a new entry.
     */
    public void addNewEntry() {
        Intent intent = new Intent(this, AddEntryActivity.class);
        try {
            addEntryLauncher.launch(intent);
        }
        catch (Exception e) {
            Toast.makeText(this, "Cannot add new entry", Toast.LENGTH_SHORT).show();
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
        findViewById(R.id.main_fab).setOnClickListener(view -> addNewEntry());

        //Setup app bar:
        findViewById(R.id.button_settings).setOnClickListener(view -> startActivity(new Intent(this, SettingsActivity.class)));

        adapter = new MainRecyclerViewAdapter(this, viewModel);
        adapter.setItemClickListener(this::onEntryClicked);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
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

}
