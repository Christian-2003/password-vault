package de.passwordvault.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.io.File;
import java.util.Objects;

import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.storage.Configuration;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.model.storage.file.EncryptedFileWriter;
import de.passwordvault.viewmodel.activities.DataConversionViewModel;
import de.passwordvault.viewmodel.activities.LoginViewModel;


/**
 * Class implements the {@link LoginActivity} which allows the user to login to the activity.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Attribute stores the {@link LoginViewModel} for the {@link LoginActivity}.
     */
    private LoginViewModel viewModel;

    /**
     * Attribute stores the biometric prompt which is used for biometric login.
     */
    private BiometricPrompt biometricPrompt;


    /**
     * Method is called whenever the {@link LoginActivity} is created or recreated.
     *
     * @param savedInstanceState    Previously saved state of the activity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Account.getInstance().hasPassword()) {
            //No login required:
            continueToMainActivity();
            return;
        }
        setContentView(R.layout.activity_login);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        TextInputEditText passwordEditText = findViewById(R.id.login_password);
        passwordEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
            }
            return true;
        });

        findViewById(R.id.login_button_continue).setOnClickListener(view -> login());

        biometricPrompt = new BiometricPrompt(LoginActivity.this, viewModel.getExecutor(), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == 13) {
                    viewModel.setBiometricAuthenticationCancelled(true);
                }
                if (errorCode != 13) {
                    //errorCode == 13 indicates the biometric login was cancelled!
                    Toast.makeText(getApplicationContext(), getString(R.string.login_biometrics_error) + ": " + errString, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                continueToMainActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //Do nothing, since biometric prompt already informs user about error!
            }
        });

        //Hide biometrics button if biometrics are disabled or unavailable:
        Button biometricLoginButton = findViewById(R.id.login_button_biometrics);
        if (viewModel.useBiometrics()) {
            biometricLoginButton.setOnClickListener(view -> showBiometricLoginDialog());
            if (!viewModel.isBiometricAuthenticationCancelled()) {
                showBiometricLoginDialog(); //Initially open dialog.
            }
        }
        else {
            biometricLoginButton.setVisibility(View.GONE);
        }
    }


    /**
     * Method tries to log the user in once the login-button is pressed. If the entered password
     * is incorrect, the view is changed to inform the user about this.
     */
    private void login() {
        TextInputEditText passwordEditText = findViewById(R.id.login_password);
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        if (!viewModel.confirmPassword(password)) {
            TextInputLayout passwordLayout = findViewById(R.id.login_password_hint);
            passwordLayout.setError(getString(R.string.error_passwords_incorrect));
            return;
        }
        continueToMainActivity();
    }


    /**
     * Method opens the {@link MainActivity} or the {@link DataConversionActivity} if data conversion
     * is required.
     */
    private void continueToMainActivity() {
        //Convert data before continuing if necessary:
        if (!Configuration.getConvertedStorage1() && (new File(getFilesDir(), "entries.csv").exists() || new File(getFilesDir(), "details.csv").exists())) {
            Intent intent = new Intent(LoginActivity.this, DataConversionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
            return;
        }

        //Begin loading the data in a separate thread:
        Thread thread = new Thread(() -> {
            try {
                EntryManager.getInstance().load();
            }
            catch (Exception e) {
                //Ignore
            }
        });
        thread.start();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }


    /**
     * Method shows the biometric prompt to authenticate with the configured biometrics.
     */
    private void showBiometricLoginDialog() {
        viewModel.setBiometricAuthenticationCancelled(false);
        biometricPrompt.authenticate(viewModel.getBiometricPromptInfo());
    }

}
