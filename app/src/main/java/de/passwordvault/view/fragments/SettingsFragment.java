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
     * Attribute stores the activity result launcher to activate the autofill-service.
     */
    private final ActivityResultLauncher<Intent> autofillActivityLauncher;


    /**
     * Default constructor instantiates a new SettingsFragment.
     */
    public SettingsFragment() {
        autofillActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            setupAutofill();
        });
    }


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


        view.findViewById(R.id.settings_about_clickable).setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsAboutActivity.class)));
        view.findViewById(R.id.settings_data_clickable).setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsDataActivity.class)));
        view.findViewById(R.id.settings_security_clickable).setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsSecurityActivity.class)));



        view.findViewById(R.id.settings_appearance_darkmode_clickable).setOnClickListener(view -> changeDarkmode());

        setupAutofill();

        return view;
    }


    /**
     * Method sets up the autofill container for the fragment.
     */
    private void setupAutofill() {
        view.findViewById(R.id.settings_autofill_enable_button).setOnClickListener(view -> showInfoDialog(R.string.settings_autofill_enable, R.string.settings_autofill_enable_info_extended));
        LinearLayout autofillEnableContainer = view.findViewById(R.id.settings_autofill_enable_clickable);
        autofillEnableContainer.setVisibility(viewModel.useAutofillService() ? View.GONE : View.VISIBLE);
        autofillEnableContainer.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE, Uri.parse("package: " + App.getContext().getPackageName()));
            try {
                autofillActivityLauncher.launch(intent);
            }
            catch (Exception e) {
                Toast.makeText(getContext(), R.string.settings_autofill_enable_error, Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.settings_autofill_enabled_container).setVisibility(viewModel.useAutofillService() ? View.VISIBLE : View.GONE);

        MaterialSwitch cachingSwitch = view.findViewById(R.id.settings_autofill_caching_switch);
        cachingSwitch.setChecked(Configuration.useAutofillCaching());
        cachingSwitch.setOnCheckedChangeListener((view, checked) -> Configuration.setAutofillCaching(checked));

        view.findViewById(R.id.settings_autofill_authentication_container).setVisibility(viewModel.useAppLogin() ? View.VISIBLE : View.GONE);
        if (viewModel.useAppLogin()) {
            MaterialSwitch authenticationSwitch = view.findViewById(R.id.settings_autofill_authentication_switch);
            authenticationSwitch.setChecked(Configuration.useAutofillAuthentication());
            authenticationSwitch.setOnCheckedChangeListener((view, checked) -> Configuration.setAutofillAuthentication(checked));
        }
    }


    /**
     * Method displays an information dialog.
     *
     * @param titleId   Id of the resource-string for the dialog title.
     * @param messageId Id if the resource-string for the dialog message.
     */
    private void showInfoDialog(int titleId, int messageId) {
        showInfoDialog(titleId, getString(messageId));
    }

    /**
     * Method displays an information dialog. If the passed message is {@code null}, an empty dialog
     * is shown.
     *
     * @param titleId   Id of the resource-string for the dialog title.
     * @param message   Message to be displayed.
     */
    private void showInfoDialog(int titleId, String message) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity()).create();
        dialog.setTitle(getString(titleId));
        if (message != null) {
            dialog.setMessage(message);
        }
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.button_ok), (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.show();
    }


    /**
     * Method shows the dialog to change between dark / light mode.
     */
    private void changeDarkmode() {
        DarkmodeDialog dialog = new DarkmodeDialog();
        dialog.show(requireActivity().getSupportFragmentManager(), "");
    }

}
