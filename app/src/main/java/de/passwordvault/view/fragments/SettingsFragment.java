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
public class SettingsFragment extends PasswordVaultBaseFragment implements DialogCallbackListener, Serializable, CompoundButton.OnCheckedChangeListener {

    /**
     * Field stores the request code for when the user selects the directory into which an XML backup
     * shall be created.
     */
    private static final int SELECT_DIRECTORY_TO_CREATE_BACKUP = 2;

    /**
     * Field stores the request code for when the user selects a file from which an XML backup shall
     * be restored.
     */
    private static final int SELECT_FILE_TO_RESTORE_BACKUP = 3;

    /**
     * Field stores the request code for when the user selects a file into which an HTML export shall
     * be saved.
     */
    private static final int SELECT_FILE_TO_EXPORT_TO_HTML = 4;


    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} for this fragment.
     */
    private SettingsViewModel viewModel;

    /**
     * Attribute stores the biometric prompt which is used for biometric authentication.
     */
    private BiometricPrompt biometricPrompt;

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

        MaterialSwitch loginSwitch = view.findViewById(R.id.settings_security_login_switch);
        loginSwitch.setChecked(viewModel.useAppLogin());
        loginSwitch.setOnCheckedChangeListener(this);
        if (!viewModel.useAppLogin()) {
            view.findViewById(R.id.settings_security_biometrics_container).setVisibility(View.GONE);
            view.findViewById(R.id.settings_security_password_container).setVisibility(View.GONE);
        }
        else {
            view.findViewById(R.id.settings_security_biometrics_container).setVisibility(View.VISIBLE);
            view.findViewById(R.id.settings_security_password_container).setVisibility(View.VISIBLE);
        }
        if (viewModel.areBiometricsAvailable()) {
            MaterialSwitch biometricsSwitch = view.findViewById(R.id.settings_security_biometrics_switch);
            biometricsSwitch.setChecked(viewModel.useBiometrics());
            biometricsSwitch.setOnCheckedChangeListener(this);
        }
        else {
            view.findViewById(R.id.settings_security_biometrics_container).setVisibility(View.GONE);
        }

        view.findViewById(R.id.settings_about_clickable).setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsAboutActivity.class)));
        view.findViewById(R.id.settings_data_clickable).setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsDataActivity.class)));


        view.findViewById(R.id.settings_appearance_darkmode_clickable).setOnClickListener(view -> changeDarkmode());
        view.findViewById(R.id.settings_security_password_clickable).setOnClickListener(view -> changePassword());
        view.findViewById(R.id.settings_security_quality_gates).setOnClickListener(view -> startActivity(new Intent(getActivity(), QualityGatesActivity.class)));
        view.findViewById(R.id.settings_security_password_analysis_clickable).setOnClickListener(view -> startActivity(new Intent(getActivity(), PasswordAnalysisActivity.class)));

        setupAutofill();

        biometricPrompt = new BiometricPrompt(requireActivity(), viewModel.getExecutor(), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                switch (viewModel.getCurrentAction()) {
                    case SettingsViewModel.ACTION_TURN_BIOMETRICS_ON:
                        MaterialSwitch toggleBiometricsOnSwitch = view.findViewById(R.id.settings_security_biometrics_switch);
                        toggleBiometricsOnSwitch.setOnCheckedChangeListener(null);
                        toggleBiometricsOnSwitch.setChecked(false);
                        toggleBiometricsOnSwitch.setOnCheckedChangeListener(SettingsFragment.this);
                        Account.getInstance().setBiometrics(false);
                        Account.getInstance().save();
                        break;
                    case SettingsViewModel.ACTION_TURN_BIOMETRICS_OFF:
                        MaterialSwitch toggleBiometricsOffSwitch = view.findViewById(R.id.settings_security_biometrics_switch);
                        toggleBiometricsOffSwitch.setOnCheckedChangeListener(null);
                        toggleBiometricsOffSwitch.setChecked(true);
                        toggleBiometricsOffSwitch.setOnCheckedChangeListener(SettingsFragment.this);
                        Account.getInstance().setBiometrics(true);
                        Account.getInstance().save();
                        break;
                    case SettingsViewModel.ACTION_DISABLE_LOGIN:
                        MaterialSwitch disableLoginSwitch = view.findViewById(R.id.settings_security_login_switch);
                        disableLoginSwitch.setOnCheckedChangeListener(null);
                        disableLoginSwitch.setChecked(true);
                        disableLoginSwitch.setOnCheckedChangeListener(SettingsFragment.this);
                        break;
                    case SettingsViewModel.ACTION_DELETE_DATA:
                        viewModel.setCurrentAction(SettingsViewModel.ACTION_NONE);
                        break;
                }

                viewModel.setCurrentAction(SettingsViewModel.ACTION_NONE);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                switch (viewModel.getCurrentAction()) {
                    case SettingsViewModel.ACTION_TURN_BIOMETRICS_ON:
                        MaterialSwitch toggleBiometricsOnSwitch = view.findViewById(R.id.settings_security_biometrics_switch);
                        if (!viewModel.areBiometricsAvailable()) {
                            toggleBiometricsOnSwitch.setOnCheckedChangeListener(null);
                            toggleBiometricsOnSwitch.setChecked(false);
                            toggleBiometricsOnSwitch.setOnCheckedChangeListener(SettingsFragment.this);
                            return;
                        }
                        Account.getInstance().setBiometrics(true);
                        Account.getInstance().save();
                        break;
                    case SettingsViewModel.ACTION_TURN_BIOMETRICS_OFF:
                        MaterialSwitch toggleBiometricsOffSwitch = view.findViewById(R.id.settings_security_biometrics_switch);
                        if (!viewModel.areBiometricsAvailable()) {
                            toggleBiometricsOffSwitch.setOnCheckedChangeListener(null);
                            toggleBiometricsOffSwitch.setChecked(true);
                            toggleBiometricsOffSwitch.setOnCheckedChangeListener(SettingsFragment.this);
                            return;
                        }
                        Account.getInstance().setBiometrics(false);
                        Account.getInstance().save();
                        break;
                    case SettingsViewModel.ACTION_DISABLE_LOGIN:
                        deactivateLogin();
                        break;
                    case SettingsViewModel.ACTION_DELETE_DATA:
                        viewModel.deleteAllData();
                        break;
                }

                viewModel.setCurrentAction(SettingsViewModel.ACTION_NONE);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //Do nothing, since biometric prompt already informs user about error!
            }
        });

        return view;
    }


    /**
     * Method is called whenever a positive dialog callback is initiated.
     *
     * @param fragment  Dialog which called the method.
     */
    @Override
    public void onPositiveCallback(DialogFragment fragment) {
        if (fragment instanceof ConfigureLoginDialog) {
            Account.getInstance().save();
            view.findViewById(R.id.settings_security_password_container).setVisibility(View.VISIBLE);
            if (viewModel.areBiometricsAvailable()) {
                view.findViewById(R.id.settings_security_biometrics_container).setVisibility(View.VISIBLE);
            }
            view.findViewById(R.id.settings_autofill_authentication_container).setVisibility(View.VISIBLE);
        }
        else if (fragment instanceof EnterPasswordDialog) {
            if (viewModel.getCurrentAction() == SettingsViewModel.ACTION_DELETE_DATA) {
                viewModel.deleteAllData();
            }
            else {
                deactivateLogin();
            }
        }
    }


    /**
     * Method is called whenever a negative dialog callback is initiated.
     *
     * @param fragment  Dialog which called the method.
     */
    @Override
    public void onNegativeCallback(DialogFragment fragment) {
        if (fragment instanceof ConfigureLoginDialog) {
            MaterialSwitch materialSwitch = view.findViewById(R.id.settings_security_login_switch);
            materialSwitch.setOnCheckedChangeListener(null);
            materialSwitch.setChecked(false);
            materialSwitch.setOnCheckedChangeListener(this);
        }
        else if (fragment instanceof EnterPasswordDialog) {
            if (viewModel.getCurrentAction() == SettingsViewModel.ACTION_DELETE_DATA) {
                viewModel.setCurrentAction(SettingsViewModel.ACTION_NONE);
            }
            else {
                MaterialSwitch materialSwitch = view.findViewById(R.id.settings_security_login_switch);
                materialSwitch.setOnCheckedChangeListener(null);
                materialSwitch.setChecked(true);
                materialSwitch.setOnCheckedChangeListener(this);
                viewModel.setCurrentAction(SettingsViewModel.ACTION_NONE);
            }
        }
        else if (fragment instanceof ConfirmDeleteDialog) {
            viewModel.setCurrentAction(SettingsViewModel.ACTION_NONE);
        }
    }


    /**
     * Method is called whenever the state of the switch is changed.
     *
     * @param compoundButton    Switch whose state was changed.
     * @param isChecked         Whether the switch is checked.
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton.getId() == R.id.settings_security_login_switch) {
            if (!isChecked) {
                //Inverted logic -> When method is called, the switch just has been checked.
                if (Account.getInstance().useBiometrics()) {
                    showBiometricAuthenticationDialog(SettingsViewModel.ACTION_DISABLE_LOGIN);
                }
                else {
                    EnterPasswordDialog dialog = new EnterPasswordDialog();
                    Bundle args = new Bundle();
                    args.putSerializable(EnterPasswordDialog.KEY_CALLBACK_LISTENER, this);
                    args.putString(EnterPasswordDialog.KEY_TITLE, getString(R.string.settings_security_disable_login_title));
                    args.putString(EnterPasswordDialog.KEY_INFO, getString(R.string.settings_security_disable_login_info));
                    dialog.setArguments(args);
                    dialog.show(requireActivity().getSupportFragmentManager(), "");
                }
                return;
            }
            ConfigureLoginDialog dialog = new ConfigureLoginDialog();
            Bundle args = new Bundle();
            args.putSerializable(ConfigureLoginDialog.KEY_CALLBACK_LISTENER, this);
            dialog.setArguments(args);
            dialog.show(requireActivity().getSupportFragmentManager(), "");
        }
        else if (compoundButton.getId() == R.id.settings_security_biometrics_switch) {
            byte action;
            if (isChecked) {
                action = SettingsViewModel.ACTION_TURN_BIOMETRICS_ON;
            }
            else {
                action = SettingsViewModel.ACTION_TURN_BIOMETRICS_OFF;
            }
            showBiometricAuthenticationDialog(action);
        }
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
     * Method opens a dialog to change the application login-password. The dialog handles everything
     * else. No callback or arguments are needed.
     */
    private void changePassword() {
        ChangePasswordDialog dialog = new ChangePasswordDialog();
        dialog.show(requireActivity().getSupportFragmentManager(), "");
    }


    /**
     * Method shows the biometric prompt to authenticate with the configured biometrics.
     *
     * @param biometricAction   Action for which the biometric prompt shall be opened.
     */
    private void showBiometricAuthenticationDialog(byte biometricAction) {
        viewModel.setCurrentAction(biometricAction);
        biometricPrompt.authenticate(viewModel.getBiometricPromptInfo());
    }


    /**
     * Method deactivates the login.
     */
    private void deactivateLogin() {
        Account.getInstance().removeAccount();
        Account.getInstance().save();
        this.view.findViewById(R.id.settings_security_password_container).setVisibility(View.GONE);
        this.view.findViewById(R.id.settings_security_biometrics_container).setVisibility(View.GONE);
        view.findViewById(R.id.settings_autofill_authentication_container).setVisibility(View.GONE);
    }


    /**
     * Method shows the dialog to change between dark / light mode.
     */
    private void changeDarkmode() {
        DarkmodeDialog dialog = new DarkmodeDialog();
        dialog.show(requireActivity().getSupportFragmentManager(), "");
    }

}
