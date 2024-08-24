package de.passwordvault.view.settings.activity_customization;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.passwordvault.R;
import de.passwordvault.view.settings.dialog_darkmode.DarkmodeDialog;
import de.passwordvault.view.settings.dialog_swipe.SwipeDialog;
import de.passwordvault.view.settings.dialog_recently_edited.RecentlyEditedDialog;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;
import de.passwordvault.view.activity_main.fragment_settings.SettingsViewModel;


/**
 * Class implements the activity which allows the user to customize the app.
 *
 * @author  Christian-2003
 * @version 3.5.2
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

        //Home page:
        findViewById(R.id.settings_customization_home_recentlyedited_conatainer).setOnClickListener(view -> changeNumberOfMostRecentlyEditedEntries());

        //Details:
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
     * Method shows the dialog to change the number of most recently edited entries on the home
     * fragment.
     */
    private void changeNumberOfMostRecentlyEditedEntries() {
        RecentlyEditedDialog dialog = new RecentlyEditedDialog();
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
