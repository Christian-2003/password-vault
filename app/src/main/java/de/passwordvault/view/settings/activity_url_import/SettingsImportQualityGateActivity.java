package de.passwordvault.view.settings.activity_url_import;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the activity through which the user can import a quality gate through a URL.
 *
 * @author  Christian-2003
 * @version 3.7.1
 */
public class SettingsImportQualityGateActivity extends PasswordVaultActivity<SettingsImportQualityGateViewModel> {

    /**
     * Class implements a text watcher for the {@link #urlEditText}.
     */
    private class UrlEditTextTextWatcher implements TextWatcher {

        /**
         * Method is called before the text is changed.
         *
         * @param s     Text that is changed.
         * @param start Index at which the change begins.
         * @param count Number of changed characters.
         * @param after New length for the changed text.
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        /**
         * Method is called while the text is changed.
         *
         * @param s         Text that is being changed.
         * @param start     Index at which the change begins.
         * @param before    Length of the text before the text is changed.
         * @param count     Number of characters being replaced.
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                viewModel.setUri(s.toString());
            }
            catch (IllegalArgumentException e) {
                //Invalid UR...
                mainContainer.setVisibility(View.GONE);
                if (s.toString().isEmpty()) {
                    urlContainer.setError(getString(R.string.error_empty_input));
                }
                else {
                    urlContainer.setError(getString(R.string.error_quality_gate_url_invalid));
                }
                return;
            }
            authorTextView.setText(viewModel.getAuthor());
            regexTextView.setText(viewModel.getRegex());
            descriptionTextView.setText(viewModel.getDescription());
            mainContainer.setVisibility(View.VISIBLE);
            urlContainer.setErrorEnabled(false);
        }

        /**
         * Method is called after the text was changed.
         *
         * @param s Text that was changed.
         */
        @Override
        public void afterTextChanged(Editable s) {

        }

    }


    /**
     * Attribute stores the text input layout through which to enter the URL.
     */
    private TextInputLayout urlContainer;

    /**
     * Attribute stores the edit text through which to enter the URL.
     */
    private TextInputEditText urlEditText;

    /**
     * Attribute stores the text view displaying the description of the quality gate to import.
     */
    private TextView descriptionTextView;

    /**
     * Attribute stores the text view displaying the regex of the quality gate.
     */
    private TextView regexTextView;

    /**
     * Attribute stores the text view displaying the author of the quality gate.
     */
    private TextView authorTextView;

    /**
     * Attribute stores the linear layout containing all views to display once a valid URL is entered.
     */
    private LinearLayout mainContainer;


    /**
     * Constructor instantiates a new activity.
     */
    public SettingsImportQualityGateActivity() {
        super(SettingsImportQualityGateViewModel.class, R.layout.activity_settings_import_quality_gate);
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        urlContainer = findViewById(R.id.container_url);
        urlEditText = findViewById(R.id.input_url);
        descriptionTextView = findViewById(R.id.text_description);
        regexTextView = findViewById(R.id.text_regex);
        authorTextView = findViewById(R.id.text_author);
        mainContainer = findViewById(R.id.container_content);

        urlEditText.addTextChangedListener(new UrlEditTextTextWatcher());

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        if (data != null) {
            urlEditText.setText(data.toString());
        }

        findViewById(R.id.button_import).setOnClickListener(view -> {
            if (!viewModel.importQualityGate()) {
                Toast.makeText(this, R.string.error_quality_gate_import, Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
            }
            else {
                setResult(RESULT_OK);
            }
            finish();
        });

        findViewById(R.id.button_back).setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

}
