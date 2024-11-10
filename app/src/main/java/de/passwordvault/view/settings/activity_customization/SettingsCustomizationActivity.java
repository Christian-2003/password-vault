package de.passwordvault.view.settings.activity_customization;

import android.os.Bundle;
import androidx.annotation.Nullable;
import de.passwordvault.R;
import de.passwordvault.view.settings.dialog_darkmode.DarkmodeDialog;
import de.passwordvault.view.settings.dialog_swipe.SwipeDialog;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import de.passwordvault.view.settings.activity_settings.SettingsViewModel;


/**
 * Class implements the activity which allows the user to customize the app.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class SettingsCustomizationActivity extends PasswordVaultActivity<SettingsViewModel> {

    /**
     * Constructor instantiates a new activity.
     */
    public SettingsCustomizationActivity() {
        super(SettingsViewModel.class, R.layout.activity_settings_customization);
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

        //Appearance:
        findViewById(R.id.settings_customization_appearance_darkmode_conatainer).setOnClickListener(view -> changeDarkmode());
        findViewById(R.id.settings_customization_details_swipe_conatainer).setOnClickListener(view -> changeSwipeActions());
    }


    /**
     * Method shows the dialog to change between dark / light mode.
     */
    private void changeDarkmode() {
        DarkmodeDialog dialog = new DarkmodeDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

    /**
     * Method shows the dialog to change the swipe actions for swiping details.
     */
    private void changeSwipeActions() {
        SwipeDialog dialog = new SwipeDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

}
