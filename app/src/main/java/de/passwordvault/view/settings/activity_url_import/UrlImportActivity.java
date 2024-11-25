package de.passwordvault.view.settings.activity_url_import;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultActivity;

public class UrlImportActivity extends PasswordVaultActivity<UrlImportViewModel> {

    public UrlImportActivity() {
        super(UrlImportViewModel.class, R.layout.activity_url_import);
    }


    /**
     * @param savedInstanceState Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        if (data != null) {
            viewModel.setUri(data);
            TextView urlTextView = findViewById(R.id.text_url);
            urlTextView.setText(viewModel.getUri());
            TextView authorTextView = findViewById(R.id.text_author);
            authorTextView.setText(viewModel.getAuthor());
            TextView regexTextView = findViewById(R.id.text_regex);
            regexTextView.setText(viewModel.getRegex());
            TextView descriptionTextView = findViewById(R.id.text_description);
            descriptionTextView.setText(viewModel.getDescription());
        }

        findViewById(R.id.button_import).setOnClickListener(view -> {
            viewModel.importQualityGate();
            finish();
        });

    }

}
