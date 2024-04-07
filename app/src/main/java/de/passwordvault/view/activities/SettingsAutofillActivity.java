package de.passwordvault.view.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.materialswitch.MaterialSwitch;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.security.authentication.AuthenticationCallback;
import de.passwordvault.model.security.authentication.AuthenticationFailure;
import de.passwordvault.model.security.authentication.Authenticator;
import de.passwordvault.model.storage.Configuration;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;
import de.passwordvault.viewmodel.fragments.SettingsViewModel;


/**
 * Class implements an activity which allows the user to configure the autofill service.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class SettingsAutofillActivity extends PasswordVaultBaseActivity implements AuthenticationCallback, CompoundButton.OnCheckedChangeListener {

    /**
     * Field stores the tag used for authentication when autofill authentication is deactivated.
     */
    private static final String TAG_AUTH_DEACTIVATE = "auth_deactivate";

    /**
     * Field stores the tag used for authentication when autofill authentication is activated.
     */
    private static final String TAG_AUTH_ACTIVATE = "auth_activate";


    /**
     * Attribute stores the view model for the activity.
     */
    private SettingsViewModel viewModel;

    /**
     * Attribute stores the activity result launcher used to activate the autofill service.
     */
    private final ActivityResultLauncher<Intent> activateAutofillLauncher;


    /**
     * Constructor instantiates a new activity.
     */
    public SettingsAutofillActivity() {
        //Autofill:
        activateAutofillLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            findViewById(R.id.settings_autofill_toggle_container).setVisibility(viewModel.useAutofillService() ? View.GONE : View.VISIBLE);
            findViewById(R.id.settings_autofill_config_container).setVisibility(viewModel.useAutofillService() ? View.VISIBLE : View.GONE);
        });
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
        if (tag.equals(TAG_AUTH_DEACTIVATE)) {
            Configuration.setAutofillAuthentication(false);
        }
        else if (tag.equals(TAG_AUTH_ACTIVATE)) {
            Configuration.setAutofillAuthentication(true);
        }
    }

    /**
     * Method is called on failed authentication.
     *
     * @param tag   Tag that was passed with the authentication request.
     * @param code  Authentication failure code indicating why the authentication failed.
     */
    @Override
    public void onAuthenticationFailure(@Nullable String tag, @NonNull AuthenticationFailure code) {
        if (tag == null) {
            return;
        }
        if (tag.equals(TAG_AUTH_DEACTIVATE)) {
            MaterialSwitch authenticationSwitch = findViewById(R.id.settings_autofill_authentication_toggle_switch);
            authenticationSwitch.setOnCheckedChangeListener(null);
            authenticationSwitch.setChecked(true);
            authenticationSwitch.setOnCheckedChangeListener(this);
        }
        else if (tag.equals(TAG_AUTH_ACTIVATE)) {
            MaterialSwitch authenticationSwitch = findViewById(R.id.settings_autofill_authentication_toggle_switch);
            authenticationSwitch.setOnCheckedChangeListener(null);
            authenticationSwitch.setChecked(false);
            authenticationSwitch.setOnCheckedChangeListener(this);
        }
    }


    /**
     * Method is called whenever a switch's state is changed.
     *
     * @param button    Switch that was toggled.
     * @param checked   Whether the switch is checked.
     */
    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        if (button.getId() == R.id.settings_autofill_authentication_toggle_switch) {
            Authenticator authenticator;
            if (checked) {
                authenticator = new Authenticator(this, TAG_AUTH_ACTIVATE, R.string.settings_autofill_authentication_toggle);
            }
            else {
                authenticator = new Authenticator(this, TAG_AUTH_DEACTIVATE, R.string.settings_autofill_authentication_toggle);
            }
            authenticator.authenticate(this);
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
        setContentView(R.layout.activity_settings_autofill);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Toggle:
        LinearLayout toggleContainer = findViewById(R.id.settings_autofill_toggle_container);
        toggleContainer.setVisibility(viewModel.useAutofillService() ? View.GONE : View.VISIBLE);
        toggleContainer.setOnClickListener(view -> enableAutofill());
        findViewById(R.id.settings_autofill_config_container).setVisibility(viewModel.useAutofillService() ? View.VISIBLE : View.GONE);

        //Caching:
        MaterialSwitch cacheSwitch = findViewById(R.id.settings_autofill_cache_toggle_switch);
        cacheSwitch.setChecked(Configuration.useAutofillCaching());
        cacheSwitch.setOnCheckedChangeListener((view, checked) -> Configuration.setAutofillCaching(checked));

        //Authentication:
        findViewById(R.id.settings_autofill_authentication_container).setVisibility(viewModel.useAppLogin() ? View.VISIBLE : View.GONE);
        MaterialSwitch authenticationSwitch = findViewById(R.id.settings_autofill_authentication_toggle_switch);
        authenticationSwitch.setChecked(Configuration.useAutofillAuthentication());
        authenticationSwitch.setOnCheckedChangeListener(this);
    }


    /**
     * Method starts the process of enabeling the autofill service.
     */
    private void enableAutofill() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE, Uri.parse("package: " + App.getContext().getPackageName()));
        try {
            activateAutofillLauncher.launch(intent);
        }
        catch (Exception e) {
            Toast.makeText(this, R.string.settings_autofill_toggle_error, Toast.LENGTH_SHORT).show();
        }
    }

}
