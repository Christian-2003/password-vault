package de.passwordvault.view.entries.activity_search;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;

public class SearchActivity extends PasswordVaultBaseActivity {

    private SearchViewModel viewModel;

    private EditText searchQueryEditText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Search query:
        searchQueryEditText = findViewById(R.id.edit_text_search_query);
    }

}
