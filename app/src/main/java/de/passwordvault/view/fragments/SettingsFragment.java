package de.passwordvault.view.fragments;

import android.app.Activity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import java.io.Serializable;
import java.util.Objects;
import de.passwordvault.App;
import de.passwordvault.BuildConfig;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.storage.Configuration;
import de.passwordvault.model.storage.backup.XmlBackupRestorer;
import de.passwordvault.view.activities.PasswordAnalysisActivity;
import de.passwordvault.view.activities.QualityGatesActivity;
import de.passwordvault.view.activities.SettingsAboutActivity;
import de.passwordvault.view.activities.SettingsAutofillActivity;
import de.passwordvault.view.activities.SettingsCustomizationActivity;
import de.passwordvault.view.activities.SettingsDataActivity;
import de.passwordvault.view.activities.SettingsSecurityActivity;
import de.passwordvault.view.dialogs.ChangePasswordDialog;
import de.passwordvault.view.dialogs.ConfigureLoginDialog;
import de.passwordvault.view.dialogs.ConfirmDeleteDialog;
import de.passwordvault.view.dialogs.CreateBackupDialog;
import de.passwordvault.view.dialogs.DarkmodeDialog;
import de.passwordvault.view.dialogs.EnterPasswordDialog;
import de.passwordvault.view.dialogs.RestoreBackupDialog;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.components.PasswordVaultBaseFragment;
import de.passwordvault.viewmodel.fragments.SettingsViewModel;
import de.passwordvault.view.activities.MainActivity;


/**
 * Class implements the {@linkplain Fragment} that displays all settings within the
 * {@linkplain MainActivity}.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class SettingsFragment extends PasswordVaultBaseFragment implements Serializable {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} for this fragment.
     */
    private SettingsViewModel viewModel;

    /**
     * Attribute stores the inflated view of the fragment.
     */
    private View view;


    /**
     * Method is called whenever the SettingsFragment is created or recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
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
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        view.findViewById(R.id.settings_customization_container).setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsCustomizationActivity.class)));
        view.findViewById(R.id.settings_security_container).setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsSecurityActivity.class)));
        view.findViewById(R.id.settings_data_container).setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsDataActivity.class)));
        view.findViewById(R.id.settings_autofill_container).setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsAutofillActivity.class)));
        view.findViewById(R.id.settings_about_container).setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsAboutActivity.class)));

        return view;
    }

}
