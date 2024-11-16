package de.passwordvault.view.activity_search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.search.SearchResult;
import de.passwordvault.model.search.SearchResultDetail;
import de.passwordvault.model.search.SearchResultEntry;
import de.passwordvault.view.entries.activity_entry.EntryActivity;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the activity through which the user can search their entries.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class SearchActivity extends PasswordVaultActivity<SearchViewModel> {

    /**
     * Field stores the result code returned if some data was deleted.
     */
    public static final int RESULT_DELETED = 315;


    /**
     * Attribute stores the progress bar indicating that the app is currently searching.
     */
    private ProgressBar progressBar;

    /**
     * Attribute stores the edit text used to enter the search query.
     */
    private EditText queryInput;

    /**
     * Attribute stores the recycler view of the activity.
     */
    private RecyclerView recyclerView;

    /**
     * Attribute stores the recycler view adapter of the page.
     */
    private SearchRecyclerViewAdapter adapter;

    /**
     * Attribute stores the input method manager used to manage the keyboard.
     */
    private InputMethodManager inputMethodManager;

    /**
     * Attribute stores the launcher to start the {@link EntryActivity}.
     */
    private final ActivityResultLauncher<Intent> entryLauncher;


    /**
     * Constructor instantiates a new activity.
     */
    public SearchActivity() {
        super(SearchViewModel.class, R.layout.activity_search);

        entryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == EntryActivity.RESULT_DELETED && viewModel.getSearchResults() != null) {
                for (int i = viewModel.getLastOpenedPosition(); i >= viewModel.getFirstOpenedPosition(); i--) {
                    viewModel.getSearchResults().remove(i);
                }
                adapter.notifyItemRangeRemoved(viewModel.getFirstOpenedPosition() + SearchRecyclerViewAdapter.OFFSET_SEARCH_RESULTS, (viewModel.getLastOpenedPosition() - viewModel.getFirstOpenedPosition()) + 1);
                viewModel.setResultCode(RESULT_DELETED);
            }
            viewModel.openEntry(-1);
        });
    }


    /**
     * Method is called to close the activity.
     */
    @Override
    public void finish() {
        setResult(viewModel.getResultCode());
        super.finish();
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);

        queryInput = findViewById(R.id.input_search);
        queryInput.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        queryInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            onSearchClicked();
            return true;
        });
        queryInput.requestFocus();
        inputMethodManager.showSoftInput(queryInput, 0);

        findViewById(R.id.button_search).setOnClickListener(view -> onSearchClicked());

        if (viewModel.isFinished()) {
            onSearchFinished();
        }
    }


    /**
     * Method begins searching the data. The method is invoked if the
     * search button in the app bar is clicked or if the user presses the
     * action button on the keyboard.
     */
    private void onSearchClicked() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        String query = queryInput.getText().toString();
        viewModel.search(this::onSearchFinished, query, true);
        queryInput.clearFocus();
        inputMethodManager.hideSoftInputFromWindow(queryInput.getWindowToken(), 0);
    }


    /**
     * Method is called whenever the view model has finished searching.
     */
    private void onSearchFinished() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new SearchRecyclerViewAdapter(this, viewModel);
            adapter.setSearchResultClicked(this::onSearchResultClicked);
            recyclerView.setAdapter(adapter);
        });
    }


    /**
     * Method is called whenever a search result is clicked.
     *
     * @param position  Adapter position of the search result clicked.
     */
    private void onSearchResultClicked(int position) {
        SearchResult result = adapter.getSearchResultForAdapterPosition(position);
        if (result == null) {
            return;
        }
        String entryUuid = null;
        if (result.getType() == SearchResult.TYPE_ENTRY) {
            entryUuid = ((SearchResultEntry)result).getEntry().getUuid();
        }
        else if (result.getType() == SearchResult.TYPE_DETAIL) {
            entryUuid = ((SearchResultDetail)result).getEntry().getUuid();
        }

        viewModel.openEntry(position - SearchRecyclerViewAdapter.OFFSET_SEARCH_RESULTS);

        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra(EntryActivity.KEY_ID, entryUuid);
        entryLauncher.launch(intent);
    }

}
