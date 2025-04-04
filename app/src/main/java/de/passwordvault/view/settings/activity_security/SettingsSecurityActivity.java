package de.passwordvault.view.settings.activity_security;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.materialswitch.MaterialSwitch;
import de.passwordvault.R;
import de.passwordvault.model.security.authentication.AuthenticationCallback;
import de.passwordvault.model.security.authentication.AuthenticationFailure;
import de.passwordvault.model.security.authentication.Authenticator;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.storage.settings.Config;
import de.passwordvault.view.passwords.activity_analysis.PasswordAnalysisActivity;
import de.passwordvault.view.settings.activity_autofill.SettingsAutofillActivity;
import de.passwordvault.view.settings.activity_data.SettingsDataActivity;
import de.passwordvault.view.authentication.dialog_change_password.ChangePasswordDialog;
import de.passwordvault.view.settings.activity_recovery.RecoveryActivity;
import de.passwordvault.view.settings.activity_settings.SettingsViewModel;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements an activity for security-related settings.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class SettingsSecurityActivity extends PasswordVaultActivity<SettingsViewModel> implements AuthenticationCallback, CompoundButton.OnCheckedChangeListener {

    /**
     * Field stores the tag used for authentication when the app login is deactivated.
     */
    private static final String TAG_AUTH_LOGIN_DEACTIVATE = "login_off";

    /**
     * Field stores the tag used for authentication when the app login is activated.
     */
    private static final String TAG_AUTH_LOGIN_ACTIVATE = "login_on";

    /**
     * Field stores the tag used for authentication when biometrics are deactivated.
     */
    private static final String TAG_AUTH_BIOMETRICS_DEACTIVATE = "biometrics_off";

    /**
     * Field stores the tag used for authentication when biometrics are activated.
     */
    private static final String TAG_AUTH_BIOMETRICS_ACTIVATE = "biometrics_on";


    public SettingsSecurityActivity() {
        super(SettingsViewModel.class, R.layout.activity_settings_security);
    }


    /**
     * Method is called on successful authentication.
     *
     * @param tag   Tag that was passed with the authentication request.
     */
    @Override
    public void onAuthenticationSuccess(@Nullable String tag) {
        if (tag == null) {
            return;
        }
        if (tag.equals(TAG_AUTH_LOGIN_DEACTIVATE)) {
            Account.getInstance().removeAccount();
            Account.getInstance().save();
            findViewById(R.id.settings_security_login_app_config_container).setVisibility(View.GONE);
        }
        else if (tag.equals(TAG_AUTH_LOGIN_ACTIVATE)) {
            findViewById(R.id.settings_security_login_app_config_container).setVisibility(View.VISIBLE);
        }
        else if (tag.equals(TAG_AUTH_BIOMETRICS_DEACTIVATE)) {
            Account.getInstance().setBiometrics(false);
            Account.getInstance().save();
        }
        else if (tag.equals(TAG_AUTH_BIOMETRICS_ACTIVATE)) {
            Account.getInstance().setBiometrics(true);
            Account.getInstance().save();
        }
    }

    /**
     * Method is called on unsuccessful authentication.
     *
     * @param tag   Tag that was passed with the authentication request.
     * @param code  Authentication failure code indicating why the authentication failed.
     */
    @Override
    public void onAuthenticationFailure(@Nullable String tag, @NonNull AuthenticationFailure code) {
        if (tag == null) {
            return;
        }
        if (tag.equals(TAG_AUTH_LOGIN_DEACTIVATE)) {
            MaterialSwitch loginSwitch = findViewById(R.id.settings_security_login_app_switch);
            loginSwitch.setOnCheckedChangeListener(null);
            loginSwitch.setChecked(true);
            loginSwitch.setOnCheckedChangeListener(this);
        }
        else if (tag.equals(TAG_AUTH_LOGIN_ACTIVATE)) {
            MaterialSwitch loginSwitch = findViewById(R.id.settings_security_login_app_switch);
            loginSwitch.setOnCheckedChangeListener(null);
            loginSwitch.setChecked(false);
            loginSwitch.setOnCheckedChangeListener(this);
        }
        else if (tag.equals(TAG_AUTH_BIOMETRICS_DEACTIVATE)) {
            MaterialSwitch biometricsSwitch = findViewById(R.id.settings_security_login_biometrics_switch);
            biometricsSwitch.setOnCheckedChangeListener(null);
            biometricsSwitch.setChecked(true);
            biometricsSwitch.setOnCheckedChangeListener(this);
        }
        else if (tag.equals(TAG_AUTH_BIOMETRICS_ACTIVATE)) {
            MaterialSwitch biometricsSwitch = findViewById(R.id.settings_security_login_biometrics_switch);
            biometricsSwitch.setOnCheckedChangeListener(null);
            biometricsSwitch.setChecked(false);
            biometricsSwitch.setOnCheckedChangeListener(this);
        }
    }


    /**
     * Method is called whenever a switch in this activity is toggled.
     *
     * @param button    Switch that was toggled.
     * @param checked   Whether the switch is checked.
     */
    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        if (button.getId() == R.id.settings_security_login_app_switch) {
            if (!checked) {
                //Deactivate login:
                Authenticator authenticator = new Authenticator(this, TAG_AUTH_LOGIN_DEACTIVATE, R.string.settings_security_login_app);
                authenticator.authenticate(this);
            }
            else {
                //Activate login:
                Authenticator authenticator = new Authenticator(this, TAG_AUTH_LOGIN_ACTIVATE, R.string.settings_security_login_app);
                authenticator.authenticate(this, Authenticator.AUTH_PASSWORD, Authenticator.TYPE_REGISTER);
            }
        }
        else if (button.getId() == R.id.settings_security_login_biometrics_switch) {
            Authenticator authenticator;
            if (!checked) {
                //Deactivate biometrics:
                authenticator = new Authenticator(this, TAG_AUTH_BIOMETRICS_DEACTIVATE, R.string.settings_security_login_biometrics);
            }
            else {
                //Activate biometrics:
                authenticator = new Authenticator(this, TAG_AUTH_BIOMETRICS_ACTIVATE, R.string.settings_security_login_biometrics);
            }
            authenticator.authenticate(this, Authenticator.AUTH_BIOMETRICS, Authenticator.TYPE_AUTHENTICATE);
        }
        else if (button.getId() == R.id.settings_security_data_screenshot_switch) {
            Config.getInstance().preventScreenshots.set(checked);
        }
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

        //Login
        MaterialSwitch loginSwitch = findViewById(R.id.settings_security_login_app_switch);
        loginSwitch.setChecked(viewModel.useAppLogin());
        loginSwitch.setOnCheckedChangeListener(this);
        findViewById(R.id.settings_security_login_app_clickable).setOnClickListener(view -> loginSwitch.setChecked(!loginSwitch.isChecked()));
        findViewById(R.id.settings_security_login_app_config_container).setVisibility(viewModel.useAppLogin() ? View.VISIBLE : View.GONE);
        MaterialSwitch biometricsSwitch = findViewById(R.id.settings_security_login_biometrics_switch);
        biometricsSwitch.setChecked(viewModel.useBiometrics());
        biometricsSwitch.setOnCheckedChangeListener(this);
        LinearLayout biometricsContainer = findViewById(R.id.settings_security_login_app_biometrics_container);
        biometricsContainer.setVisibility(viewModel.areBiometricsAvailable() ? View.VISIBLE : View.GONE);
        biometricsContainer.setOnClickListener(view -> biometricsSwitch.setChecked(!biometricsSwitch.isChecked()));
        findViewById(R.id.settings_security_login_password_container).setOnClickListener(view -> changePassword());
        findViewById(R.id.settings_security_login_recovery_container).setOnClickListener(view -> startActivity(new Intent(SettingsSecurityActivity.this, RecoveryActivity.class)));

        //Data:
        MaterialSwitch screenshotSwitch = findViewById(R.id.settings_security_data_screenshot_switch);
        screenshotSwitch.setChecked(Config.getInstance().preventScreenshots.get());
        screenshotSwitch.setOnCheckedChangeListener(this);
        findViewById(R.id.settings_security_data_screenshot_clickable).setOnClickListener(view -> screenshotSwitch.setChecked(!screenshotSwitch.isChecked()));
    }


    /**
     * Method starts the process of changing the master password.
     */
    private void changePassword() {
        ChangePasswordDialog dialog = new ChangePasswordDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

}
