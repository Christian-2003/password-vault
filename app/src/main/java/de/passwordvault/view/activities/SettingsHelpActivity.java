package de.passwordvault.view.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;


/**
 * Class implements the help activity displaying all help pages to the user.
 *
 * @author  Christian-2003
 * @version 3.5.3
 */
public class SettingsHelpActivity extends PasswordVaultBaseActivity {

    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_help);

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Help pages:
        findViewById(R.id.settings_help_login_container).setOnClickListener(view -> showHelpPage("configure_login.html"));
        findViewById(R.id.settings_help_autofill_container).setOnClickListener(view -> showHelpPage("configure_autofill.html"));
    }


    /**
     * Method hows the help page with the specified name to the user.
     *
     * @param name  Name of the page (e.g. "help.html") to show to the user.
     */
    private void showHelpPage(String name) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.KEY_PAGE, name);
        startActivity(intent);
    }

}
