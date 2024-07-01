package de.passwordvault.view.authentication.activity_autofill_authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.service.autofill.FillResponse;
import android.view.View;
import android.view.autofill.AutofillManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.storage.settings.Config;
import de.passwordvault.view.authentication.activity_login.LoginActivity;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;


/**
 * Class implements an activity that can be used to authenticate the user through the autofill service.
 * The class basically works identically to the {@link LoginActivity}. I have decided to reimplement
 * the activity (instead of expanding the login activity) since the login activity contains additional
 * code that is used to convert data when in between version updates. This is not required when
 * authenticating for the autofill service.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class AutofillAuthenticationActivity extends PasswordVaultBaseActivity {

    /**
     * Field stores the key that must be used when passing a list of datasets that shall be displayed
     * to the user after authentication.
     */
    public static final String KEY_DATASETS = "datasets";


    /**
     * Attribute stores the view model of the activity.
     */
    private AutofillAuthenticationViewModel viewModel;

    /**
     * Attribute stores the biometric prompt used for authentication.
     */
    private BiometricPrompt biometricPrompt;

    /**
     * Attribute stores the EditText used to enter the master password.
     */
    private TextInputEditText passwordEditText;

    /**
     * Attribute stores the TextInputLayout encapsulating the EditText to enter the master password.
     */
    private TextInputLayout passwordLayout;


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Config.Methods.applyDarkmode();

        if (!Account.getInstance().hasPassword()) {
            onFailure();
            return;
        }
        setContentView(R.layout.activity_autofill_authentication);
        viewModel = new ViewModelProvider(this).get(AutofillAuthenticationViewModel.class);
        viewModel.processArguments(getIntent().getExtras());

        passwordEditText = findViewById(R.id.autofill_authentication_password);
        passwordEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                authenticate();
            }
            return true;
        });

        findViewById(R.id.autofill_authentication_authenticate_button).setOnClickListener(view -> authenticate());
        findViewById(R.id.autofill_authentication_back_button).setOnClickListener(view -> onFailure());

        if (biometricPrompt == null) {
            biometricPrompt = new BiometricPrompt(AutofillAuthenticationActivity.this, viewModel.getExecutor(), new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    if (errorCode == 13) {
                        viewModel.setBiometricAuthenticationCancelled(true);
                    }
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    onSuccess();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                }
            });
        }

        Button biometricsButton = findViewById(R.id.autofill_authentication_biometrics_button);
        if (viewModel.useBiometrics()) {
            biometricsButton.setOnClickListener(view -> showBiometricAuthenticationDialog());
            if (!viewModel.isBiometricAuthenticationCancelled()) {
                //Initially open biometric authentication dialog:
                showBiometricAuthenticationDialog();
            }
        }
        else {
            biometricsButton.setVisibility(View.GONE);
        }
    }


    /**
     * Method generates a fill response that shows all available data sets to the user after
     * successful authentication.
     *
     * @return  Generated fill response.
     */
    private FillResponse generateFillResponse() {
        FillResponse.Builder responseBuilder = new FillResponse.Builder();
        for (Dataset dataset : viewModel.getDatasets()) {
            responseBuilder.addDataset(dataset);
        }
        return responseBuilder.build();
    }


    /**
     * Method tries to authenticate the user with the entered password. If authentication succeeds,
     * {@link #onSuccess()} is called. Otherwise the visuals of the activity are changed to inform
     * the user about the incorrect password.
     */
    private void authenticate() {
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        if (!viewModel.confirmPassword(password)) {
            if (passwordLayout == null) {
                passwordLayout = findViewById(R.id.autofill_authentication_password_hint);
            }
            passwordLayout.setError(getString(R.string.error_passwords_incorrect));
        }
        else {
            onSuccess();
        }
    }


    /**
     * Method shows the biometric prompt to authenticate with the configured biometrics.
     */
    private void showBiometricAuthenticationDialog() {
        viewModel.setBiometricAuthenticationCancelled(false);
        biometricPrompt.authenticate(viewModel.getBiometricPromptInfo());
    }


    /**
     * Method closes the activity indicating to the app that launched this activity, that
     * authentication was successful.
     */
    private void onSuccess() {
        Intent successIntent = new Intent();
        successIntent.putExtra(AutofillManager.EXTRA_AUTHENTICATION_RESULT, generateFillResponse());
        setResult(Activity.RESULT_OK, successIntent);
        finish();
    }

    /**
     * Method closes the activity indicating to the app that launched this activity, that
     * authentication was not successful.
     */
    private void onFailure() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

}
