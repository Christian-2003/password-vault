package de.passwordvault.view.authentication.activity_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.splashscreen.SplashScreenViewProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.storage.settings.Config;
import de.passwordvault.view.activity_main.MainActivity;
import de.passwordvault.view.authentication.activity_security_question.SecurityQuestionActivity;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the {@link LoginActivity} which allows the user to login to the activity.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class LoginActivity extends PasswordVaultActivity<LoginViewModel> implements SplashScreen.OnExitAnimationListener {

    /**
     * Attribute stores the biometric prompt which is used for biometric login.
     */
    private BiometricPrompt biometricPrompt;


    /**
     * Constructor instantiates a new activity.
     */
    public LoginActivity() {
        super(LoginViewModel.class, R.layout.activity_login);
    }


    /**
     * Method is called once the splash screen finishes.
     *
     * @param splashScreenViewProvider  Splash screen view provider.
     */
    @Override
    public void onSplashScreenExit(@NonNull SplashScreenViewProvider splashScreenViewProvider) {
        splashScreenViewProvider.remove();
        if (viewModel.useBiometrics() && !viewModel.isBiometricAuthenticationCancelled()) {
            showBiometricLoginDialog(); //Initially open dialog.
        }
    }


    /**
     * Method is called whenever the {@link LoginActivity} is created or recreated.
     *
     * @param savedInstanceState    Previously saved state of the activity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> viewModel.isSplashScreenVisible());
        splashScreen.setOnExitAnimationListener(this);
        viewModel.startSplashScreenTimer(getResources().getInteger(R.integer.anim_duration_splash));

        if (!Account.getInstance().hasPassword()) {
            //No login required:
            continueToMainActivity();
            return;
        }

        TextInputEditText passwordEditText = findViewById(R.id.login_password);
        passwordEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
            }
            return true;
        });

        findViewById(R.id.login_button_continue).setOnClickListener(view -> login());
        if (viewModel.usePasswordRecovery()) {
            findViewById(R.id.button_forgot_password).setOnClickListener(view -> startActivity(new Intent(this, SecurityQuestionActivity.class)));
        }
        else {
            findViewById(R.id.button_forgot_password).setVisibility(View.GONE);
        }

        biometricPrompt = new BiometricPrompt(LoginActivity.this, viewModel.getExecutor(), new BiometricPrompt.AuthenticationCallback() {
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
     * Method opens the {@link MainActivity}.
     */
    private void continueToMainActivity() {
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
