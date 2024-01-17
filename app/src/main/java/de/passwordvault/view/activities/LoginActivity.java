package de.passwordvault.view.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.viewmodel.activities.LoginViewModel;


/**
 * Class implements the {@link LoginActivity} which allows the user to login to the activity.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Attribute stores the {@link LoginViewModel} for the {@link LoginActivity}.
     */
    private LoginViewModel viewModel;


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

        findViewById(R.id.login_button_continue).setOnClickListener(view -> login());
        findViewById(R.id.login_button_biometrics).setOnClickListener(view -> continueToMainActivity());
    }


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


    private void continueToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        onDestroy();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
