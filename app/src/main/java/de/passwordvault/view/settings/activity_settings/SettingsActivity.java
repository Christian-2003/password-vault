package de.passwordvault.view.settings.activity_settings;

import android.content.Intent;
import android.os.Bundle;
import java.io.Serializable;
import de.passwordvault.R;
import de.passwordvault.view.settings.activity_about.SettingsAboutActivity;
import de.passwordvault.view.settings.activity_autofill.SettingsAutofillActivity;
import de.passwordvault.view.settings.activity_customization.SettingsCustomizationActivity;
import de.passwordvault.view.settings.activity_data.SettingsDataActivity;
import de.passwordvault.view.settings.activity_help.SettingsHelpActivity;
import de.passwordvault.view.settings.activity_security.SettingsSecurityActivity;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the activity displaying a list of settings for the app.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class SettingsActivity extends PasswordVaultActivity<SettingsViewModel> implements Serializable {

    /**
     * Constructor instantiates a new activity.
     */
    public SettingsActivity() {
        super(SettingsViewModel.class, R.layout.activity_settings);
    }


    /**
     * Method is called whenever the SettingsFragment is created or recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        findViewById(R.id.settings_customization_container).setOnClickListener(view -> startActivity(new Intent(this, SettingsCustomizationActivity.class)));
        findViewById(R.id.settings_security_container).setOnClickListener(view -> startActivity(new Intent(this, SettingsSecurityActivity.class)));
        findViewById(R.id.settings_data_container).setOnClickListener(view -> startActivity(new Intent(this, SettingsDataActivity.class)));
        findViewById(R.id.settings_autofill_container).setOnClickListener(view -> startActivity(new Intent(this, SettingsAutofillActivity.class)));

        findViewById(R.id.settings_help_container).setOnClickListener(view -> startActivity(new Intent(this, SettingsHelpActivity.class)));
        findViewById(R.id.settings_about_container).setOnClickListener(view -> startActivity(new Intent(this, SettingsAboutActivity.class)));
    }

}
