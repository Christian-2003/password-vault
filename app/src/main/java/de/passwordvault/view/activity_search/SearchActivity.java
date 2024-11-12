package de.passwordvault.view.activity_search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.view.entries.dialog_edit_tag.EditTagDialog;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the activity through which the user can search their entries.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class SearchActivity extends PasswordVaultActivity<SearchViewModel> {

    private ProgressBar progressBar;

    private RecyclerView recyclerView;

    private SearchRecyclerViewAdapter adapter;


    /**
     * Constructor instantiates a new activity.
     */
    public SearchActivity() {
        super(SearchViewModel.class, R.layout.activity_search);
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);
        EditText queryEditText = findViewById(R.id.input_search);

        findViewById(R.id.button_search).setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            String query = queryEditText.getText().toString();
            viewModel.search(this::onSearchFinished, query, true);
        });
    }



    private void onSearchFinished() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new SearchRecyclerViewAdapter(this, viewModel);
            recyclerView.setAdapter(adapter);
        });
    }

}
