package de.passwordvault.view.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.passwordvault.R;
import de.passwordvault.view.dialogs.DarkmodeDialog;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;
import de.passwordvault.viewmodel.fragments.SettingsViewModel;


/**
 * Class implements the activity which allows the user to customize the app.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class SettingsCustomizationActivity extends PasswordVaultBaseActivity {

    /**
     * Attribute stores the view model for the activity.
     */
    private SettingsViewModel viewModel;


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_customization);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Appearance:
        findViewById(R.id.settings_customization_appearance_darkmode_conatainer).setOnClickListener(view -> changeDarkmode());
    }


    /**
     * Method shows the dialog to change between dark / light mode.
     */
    private void changeDarkmode() {
        DarkmodeDialog dialog = new DarkmodeDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

}