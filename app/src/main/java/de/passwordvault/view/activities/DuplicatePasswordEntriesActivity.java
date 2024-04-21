package de.passwordvault.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.view.utils.adapters.EntriesRecyclerViewAdapter;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;
import de.passwordvault.view.utils.components.SearchBarView;
import de.passwordvault.viewmodel.activities.DuplicatePasswordEntriesViewModel;


/**
 * Class implements the activity which shows all entries to the user that have an identical password.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class DuplicatePasswordEntriesActivity extends PasswordVaultBaseActivity implements OnRecyclerItemClickListener<EntryAbbreviated> {

    /**
     * Field stores the key that needs to be used when passing the list of passwords whose entries
     * shall be displayed.
     */
    public static final String KEY_PASSWORDS = "passwords";

    /**
     * Field stores the tag used for logging.
     */
    private static final String TAG = "DuplicatePasswordEntriesActivity";


    /**
     * Attribute stores the view model for the activity.
     */
    private DuplicatePasswordEntriesViewModel viewModel;

    /**
     * Attribute stores the adapter for the recycler view displaying the entries.
     */
    private EntriesRecyclerViewAdapter adapter;

    /**
     * Attribute stores the activity result launcher used for showing an entry to the user.
     */
    private final ActivityResultLauncher<Intent> entryLauncher;


    /**
     * Constructor instantiates a new activity.
     */
    public DuplicatePasswordEntriesActivity() {
        //Entry:
        entryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null) {
                if (result.getResultCode() == EntryActivity.RESULT_EDITED) {
                    int index = viewModel.getEntries().indexOf(viewModel.getDisplayedEntry());
                    EntryAbbreviated editedEntry = EntryManager.getInstance().get(viewModel.getDisplayedEntry().getUuid());
                    viewModel.getEntries().set(index, editedEntry);
                    adapter.notifyItemChanged(index);
                    viewModel.setDisplayedEntry(null);
                }
                else if (result.getResultCode() == EntryActivity.RESULT_DELETED) {
                    int index = viewModel.getEntries().indexOf(viewModel.getDisplayedEntry());
                    viewModel.getEntries().remove(index);
                    adapter.notifyItemRemoved(index);
                    viewModel.setDisplayedEntry(null);
                }
            }
        });
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_password_entries);
        viewModel = new ViewModelProvider(this).get(DuplicatePasswordEntriesViewModel.class);

        //Handle arguments:
        if (viewModel.getPasswords() == null) {
            Bundle args = getIntent().getExtras();
            try {
                ArrayList<Password> passwords = (ArrayList<Password>)args.get(KEY_PASSWORDS);
                if (passwords == null) {
                    Log.d("DuplicatePasswordEntriesActivity", "Argument is null.");
                    finish();
                }
                viewModel.setPasswords(passwords);
            }
            catch (ClassCastException e) {
                Log.d(TAG, "Could not cast argument: " + e.getMessage());
                finish();
            }
        }

        //Toolbar:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());
        SearchBarView searchBar = findViewById(R.id.search_bar);
        findViewById(R.id.button_search).setOnClickListener(view -> {
            int searchBarVisible = searchBar.getVisibility();
            if (searchBarVisible == View.VISIBLE) {
                searchBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
                searchBar.postDelayed(() -> searchBar.setVisibility(View.GONE), getResources().getInteger(R.integer.default_anim_duration));
            }
            else if (searchBarVisible == View.GONE) {
                searchBar.setVisibility(View.VISIBLE);
                searchBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
            }
        });

        //Recycler view:
        adapter = new EntriesRecyclerViewAdapter(viewModel.getEntries(), this);
        RecyclerView recyclerView = findViewById(R.id.duplicate_password_entries_recycler_view);
        recyclerView.setAdapter(adapter);

        //Search bar:
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                //Do nothing...
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Do nothing...
            }
        });
    }


    /**
     * Method is called when the item which is passed as argument is clicked by the user.
     *
     * @param item      Clicked item.
     * @param position  Index of the clicked item.
     */
    @Override
    public void onItemClick(EntryAbbreviated item, int position) {
        viewModel.setDisplayedEntry(item);
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("uuid", item.getUuid());
        try {
            entryLauncher.launch(intent);
        }
        catch (Exception e) {
            Log.e(TAG, "Could not display entry: " + e.getMessage());
        }
    }

}
