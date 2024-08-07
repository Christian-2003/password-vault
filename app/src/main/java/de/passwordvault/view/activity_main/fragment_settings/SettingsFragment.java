package de.passwordvault.view.activity_main.fragment_settings;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.Serializable;
import de.passwordvault.R;
import de.passwordvault.model.UpdateManager;
import de.passwordvault.view.settings.activity_about.SettingsAboutActivity;
import de.passwordvault.view.settings.activity_autofill.SettingsAutofillActivity;
import de.passwordvault.view.settings.activity_customization.SettingsCustomizationActivity;
import de.passwordvault.view.settings.activity_data.SettingsDataActivity;
import de.passwordvault.view.settings.activity_help.SettingsHelpActivity;
import de.passwordvault.view.settings.activity_security.SettingsSecurityActivity;
import de.passwordvault.view.utils.components.PasswordVaultBaseFragment;
import de.passwordvault.view.activity_main.MainActivity;


/**
 * Class implements the {@linkplain Fragment} that displays all settings within the
 * {@linkplain MainActivity}.
 *
 * @author  Christian-2003
 * @version 3.5.3
 */
public class SettingsFragment extends PasswordVaultBaseFragment implements Serializable {

    /**
     * Method is called whenever the SettingsFragment is created or recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Method is called whenever the {@linkplain View} for the SettingsFragment is created.
     *
     * @param inflater              LayoutInflater for the fragment.
     * @param container             Container which contains the fragment.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Generated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        view.findViewById(R.id.settings_customization_container).setOnClickListener(view1 -> startActivity(new Intent(getActivity(), SettingsCustomizationActivity.class)));
        view.findViewById(R.id.settings_security_container).setOnClickListener(view1 -> startActivity(new Intent(getActivity(), SettingsSecurityActivity.class)));
        view.findViewById(R.id.settings_data_container).setOnClickListener(view1 -> startActivity(new Intent(getActivity(), SettingsDataActivity.class)));
        view.findViewById(R.id.settings_autofill_container).setOnClickListener(view1 -> startActivity(new Intent(getActivity(), SettingsAutofillActivity.class)));

        view.findViewById(R.id.settings_help_container).setOnClickListener(view1 -> startActivity(new Intent(getActivity(), SettingsHelpActivity.class)));
        view.findViewById(R.id.settings_about_container).setOnClickListener(view1 -> startActivity(new Intent(getActivity(), SettingsAboutActivity.class)));

        if (UpdateManager.getInstance(getActivity()).isUpdateAvailable()) {
            view.findViewById(R.id.badge_update).setVisibility(View.VISIBLE);
        }

        return view;
    }

}
