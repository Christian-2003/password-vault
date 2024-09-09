package de.passwordvault.view.passwords.activity_list;

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

import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.view.entries.activity_entry.EntryActivity;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import de.passwordvault.view.utils.recycler_view.OnRecyclerItemClickListener;
import de.passwordvault.view.passwords.PasswordsRecyclerViewAdapter;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;


/**
 * Class implements the fragment which shows a list of all passwords that were analyzed during the
 * password security analysis.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class PasswordAnalysisListActivity extends PasswordVaultActivity<PasswordAnalysisListViewModel> {

    private PasswordAnalysisListRecyclerViewAdapter adapter;

    private TextView appBarTextView;

    private EditText searchBarEditText;

    private ImageButton searchButton;


    public PasswordAnalysisListActivity() {
        super(PasswordAnalysisListViewModel.class, R.layout.activity_password_analysis_list);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        adapter = new PasswordAnalysisListRecyclerViewAdapter(this, viewModel);
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
        /*
        EntryAbbreviated entry = adapter.getEntryForAdapterPosition(position);
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("uuid", entry.getUuid());
        try {
            entryLauncher.launch(intent);
        }
        catch (Exception e) {
            //Ignore...
        }
        */
    }

}
