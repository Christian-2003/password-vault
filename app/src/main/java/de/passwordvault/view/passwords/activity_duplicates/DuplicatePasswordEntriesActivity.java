package de.passwordvault.view.passwords.activity_duplicates;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.view.entries.activity_entry.EntryActivity;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the activity which shows all entries to the user that have an identical password.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class DuplicatePasswordEntriesActivity extends PasswordVaultActivity<DuplicatePasswordEntriesViewModel> {

    /**
     * Field stores the key that needs to be used when passing the list of passwords whose entries
     * shall be displayed.
     */
    public static final String EXTRA_PASSWORDS = "extra_passwords";


    /**
     * Attribute stores the adapter for the activity.
     */
    private DuplicatePasswordEntriesRecyclerViewAdapter adapter;

    /**
     * Attribute stores the activity result launcher used for showing an entry to the user.
     */
    private final ActivityResultLauncher<Intent> entryLauncher;

    /**
     * Attribute stores the text view displaying the title of the activity.
     */
    private TextView appBarTextView;

    /**
     * Attribute stores the edit text used to enter a search query.
     */
    private EditText searchBarEditText;

    /**
     * Attribute stores the button used to enable / disable the search bar.
     */
    private ImageButton searchButton;


    /**
     * Constructor instantiates a new activity.
     */
    public DuplicatePasswordEntriesActivity() {
        super(DuplicatePasswordEntriesViewModel.class, R.layout.activity_duplicate_password_entries);

        entryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null) {
                int index = viewModel.getEntries().indexOf(viewModel.getDisplayedEntry());
                if (result.getResultCode() == EntryActivity.RESULT_EDITED) {
                    EntryAbbreviated editedEntry = EntryManager.getInstance().get(viewModel.getDisplayedEntry().getUuid());
                    viewModel.getEntries().set(index, editedEntry);
                    adapter.notifyItemChanged(index + DuplicatePasswordEntriesRecyclerViewAdapter.OFFSET_ENTRIES);
                    viewModel.setDisplayedEntry(null);
                }
                else if (result.getResultCode() == EntryActivity.RESULT_DELETED) {
                    viewModel.getEntries().remove(index);
                    adapter.notifyItemRemoved(index + DuplicatePasswordEntriesRecyclerViewAdapter.OFFSET_ENTRIES);
                    viewModel.setDisplayedEntry(null);
                }
            }
        });
    }


    /**
     * Method is called whenever the back button is closed while the activity is open.
     */
    @Override
    public void finish() {
        if (viewModel.getSearchQuery() != null) {
            disableSearch();
        }
        else {
            super.finish();
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

        //Process arguments:
        if (viewModel.getPasswords() == null) {
            Bundle args = getIntent().getExtras();
            try {
                ArrayList<Password> passwords = (ArrayList<Password>)args.get(EXTRA_PASSWORDS);
                if (passwords == null) {
                    finish();
                }
                viewModel.setPasswords(passwords);
            }
            catch (ClassCastException e) {
                finish();
            }
        }

        //Setup app bar:
        appBarTextView = findViewById(R.id.text_appbar);
        appBarTextView.setVisibility(viewModel.getSearchQuery() == null ? View.VISIBLE : View.GONE);
        searchBarEditText = findViewById(R.id.input_search);
        searchBarEditText.setVisibility(viewModel.getSearchQuery() == null ? View.GONE : View.VISIBLE);
        searchBarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Search button:
        searchButton = findViewById(R.id.button_search);
        searchButton.setOnClickListener(view -> enableSearch());

        //Recycler view:
        adapter = new DuplicatePasswordEntriesRecyclerViewAdapter(this, viewModel);
        adapter.setItemClickListener(this::onItemClicked);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
    }


    /**
     * Method disables the search bar.
     */
    private void disableSearch() {
        searchBarEditText.setVisibility(View.GONE);
        appBarTextView.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        viewModel.setSearchQuery(null);
        adapter.resetFilter();
        searchBarEditText.clearFocus();
        InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(searchBarEditText.getWindowToken(), 0);
    }

    /**
     * Method enables the search bar.
     */
    private void enableSearch() {
        searchBarEditText.setVisibility(View.VISIBLE);
        appBarTextView.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        viewModel.setSearchQuery("");
        searchBarEditText.setText(viewModel.getSearchQuery());
        searchBarEditText.requestFocus();
        InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(searchBarEditText, 0);
    }


    /**
     * Method is called whenever an item within the recycler view adapter is clicked.
     *
     * @param position  Position of the item clicked.
     */
    private void onItemClicked(int position) {
        EntryAbbreviated entry = adapter.getEntryForAdapterPosition(position);
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("uuid", entry.getUuid());
        try {
            entryLauncher.launch(intent);
        }
        catch (Exception e) {
            //Ignore...
        }
    }

}
