package de.passwordvault.view.fragments;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import java.io.Serializable;
import java.util.Objects;
import de.passwordvault.BuildConfig;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.storage.backup.XmlBackupRestorer;
import de.passwordvault.view.activities.PasswordAnalysisActivity;
import de.passwordvault.view.activities.QualityGatesActivity;
import de.passwordvault.view.dialogs.ChangePasswordDialog;
import de.passwordvault.view.dialogs.ConfigureLoginDialog;
import de.passwordvault.view.dialogs.ConfirmDeleteDialog;
import de.passwordvault.view.dialogs.CreateBackupDialog;
import de.passwordvault.view.dialogs.EnterPasswordDialog;
import de.passwordvault.view.dialogs.RestoreBackupDialog;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.viewmodel.fragments.SettingsViewModel;
import de.passwordvault.view.activities.MainActivity;


/**
 * Class implements the {@linkplain Fragment} that displays all settings within the
 * {@linkplain MainActivity}.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class SettingsFragment extends Fragment implements DialogCallbackListener, Serializable, CompoundButton.OnCheckedChangeListener {

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
     * Default constructor instantiates a new SettingsFragment.
     */
    public SettingsFragment() {
        // Required empty public constructor
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
        if (BuildConfig.DEBUG) {
            ((TextView)view.findViewById(R.id.settings_version)).setText(BuildConfig.VERSION_NAME + " (Debug Build)");
        }
        else {
            ((TextView)view.findViewById(R.id.settings_version)).setText(BuildConfig.VERSION_NAME);
        }

        view.findViewById(R.id.settings_security_password_clickable).setOnClickListener(view -> changePassword());
        view.findViewById(R.id.settings_security_backup_clickable).setOnClickListener(view -> selectDirectory(SELECT_DIRECTORY_TO_CREATE_BACKUP));
        view.findViewById(R.id.settings_security_backup_button).setOnClickListener(view -> showInfoDialog(R.string.settings_security_backup, R.string.settings_security_backup_info_extended));
        //Require mime-type '*/*' to both restore text-backups (version 3.2.0 onwards) and binary-backups (version 3.1.0):
        view.findViewById(R.id.settings_security_restore).setOnClickListener(view -> selectFile(SELECT_FILE_TO_RESTORE_BACKUP, "*/*"));
        view.findViewById(R.id.settings_security_restore_button).setOnClickListener(view -> showInfoDialog(R.string.settings_security_restore, R.string.settings_security_restore_info_extended));
        view.findViewById(R.id.settings_security_quality_gates).setOnClickListener(view -> startActivity(new Intent(getActivity(), QualityGatesActivity.class)));
        view.findViewById(R.id.settings_security_password_analysis_clickable).setOnClickListener(view -> startActivity(new Intent(getActivity(), PasswordAnalysisActivity.class)));
        view.findViewById(R.id.settings_used_software_clickable).setOnClickListener(view -> startActivity(new Intent(getActivity(), OssLicensesMenuActivity.class)));
        view.findViewById(R.id.settings_license_clickable).setOnClickListener(view -> showInfoDialog(R.string.settings_about_license, R.string.app_license));
        view.findViewById(R.id.settings_open_source_clickable).setOnClickListener(view -> openUrl(getString(R.string.settings_about_github_link)));
        view.findViewById(R.id.settings_bug_report_clickable).setOnClickListener(view -> openUrl(getString(R.string.settings_about_bug_link)));
        view.findViewById(R.id.settings_update_clickable).setOnClickListener(view -> openUrl(getString(R.string.settings_about_update_link)));
        view.findViewById(R.id.settings_html_export_clickable).setOnClickListener(view -> createFile(SELECT_FILE_TO_EXPORT_TO_HTML, "text/html", getString(R.string.settings_export_file)));
        view.findViewById(R.id.settings_html_export_info).setOnClickListener(view -> showInfoDialog(R.string.settings_export_html, R.string.settings_export_html_info_extended));
        view.findViewById(R.id.settings_data_delete_clickable).setOnClickListener(view -> deleteData());

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
     * Method is called whenever an activity has finished after being called through
     * {@link #startActivityForResult(Intent, int)}
     *
     * @param requestCode   The integer request code originally supplied to
     *                      startActivityForResult(), allowing you to identify who this
     *                      result came from.
     * @param resultCode    The integer result code returned by the child activity
     *                      through its setResult().
     * @param data          An Intent, which can return result data to the caller
     *                      (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case SELECT_DIRECTORY_TO_CREATE_BACKUP:
                //Create backup:
                CreateBackupDialog backupDialog = new CreateBackupDialog();
                Bundle backupArgs = new Bundle();
                backupArgs.putString(CreateBackupDialog.KEY_DIRECTORY, Objects.requireNonNull(data.getData()).toString());
                backupArgs.putSerializable(CreateBackupDialog.KEY_CALLBACK_LISTENER, this);
                backupDialog.setArguments(backupArgs);
                backupDialog.show(requireActivity().getSupportFragmentManager(), "");
                break;
            case SELECT_FILE_TO_RESTORE_BACKUP:
                //Restore backup:
                try {
                    if (!XmlBackupRestorer.isBackupEncrypted(data.getData())) {
                        //Backup not encrypted:
                        viewModel.restoreXmlBackup(data.getData(), null, getContext());
                        return;
                    }
                    //Backup encrypted:
                    RestoreBackupDialog restoreDialog = new RestoreBackupDialog();
                    Bundle restoreArgs = new Bundle();
                    restoreArgs.putString(RestoreBackupDialog.KEY_FILE, Objects.requireNonNull(data.getData()).toString());
                    restoreArgs.putSerializable(RestoreBackupDialog.KEY_CALLBACK_LISTENER, this);
                    restoreDialog.setArguments(restoreArgs);
                    restoreDialog.show(requireActivity().getSupportFragmentManager(), "");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SELECT_FILE_TO_EXPORT_TO_HTML:
                //Export to HTML:
                viewModel.exportToHtml(data.getData(), getContext());
                break;
        }
    }


    /**
     * Method is called whenever a positive dialog callback is initiated.
     *
     * @param fragment  Dialog which called the method.
     */
    @Override
    public void onPositiveCallback(DialogFragment fragment) {
        if (fragment instanceof CreateBackupDialog) {
            CreateBackupDialog dialog = (CreateBackupDialog)fragment;
            viewModel.createXmlBackup(dialog.getDirectory(), dialog.getFilename(), dialog.getPassword(), getContext());
        }
        else if (fragment instanceof RestoreBackupDialog) {
            RestoreBackupDialog dialog = (RestoreBackupDialog)fragment;
            viewModel.restoreXmlBackup(dialog.getFile(), dialog.getPassword(), getContext());
        }
        else if (fragment instanceof ConfigureLoginDialog) {
            Account.getInstance().save();
            view.findViewById(R.id.settings_security_password_container).setVisibility(View.VISIBLE);
            if (viewModel.areBiometricsAvailable()) {
                view.findViewById(R.id.settings_security_biometrics_container).setVisibility(View.VISIBLE);
            }
        }
        else if (fragment instanceof EnterPasswordDialog) {
            if (viewModel.getCurrentAction() == SettingsViewModel.ACTION_DELETE_DATA) {
                viewModel.deleteAllData();
            }
            else {
                deactivateLogin();
            }
        }
        else if (fragment instanceof ConfirmDeleteDialog) {
            viewModel.deleteAllData();
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
     * Method opens the device's default file explorer to select a directory.
     *
     * @param requestCode   Request code to be used when choosing a directory.
     */
    private void selectDirectory(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, SELECT_DIRECTORY_TO_CREATE_BACKUP);
    }


    /**
     * Method opens the device's default file explorer to select a file.
     *
     * @param requestCode   Request code to be used when choosing a file.
     * @param mimeType      MimeType of the file to be selected.
     */
    private void selectFile(int requestCode, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        startActivityForResult(intent, requestCode);
    }

    /**
     * Method opens the device's default file explorer to create a file.
     *
     * @param requestCode   Request code to be used when creating a file.
     * @param mimeType      MimeType of the file to be created.
     */
    private void createFile(int requestCode, String mimeType, String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        if (fileName != null) {
            intent.putExtra(Intent.EXTRA_TITLE, fileName);
        }
        startActivityForResult(intent, requestCode);
    }


    /**
     * Method displays an information dialog.
     *
     * @param titleId   Id of the resource-string for the dialog title.
     * @param messageId Id if the resource-string for the dialog message.
     */
    private void showInfoDialog(int titleId, int messageId) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity()).create();
        dialog.setTitle(getString(titleId));
        dialog.setMessage(getString(messageId));
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.button_ok), (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.show();
    }


    /**
     * Method opens the specified URL in the browser.
     *
     * @param url   URL to be opened.
     */
    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }


    private void deleteData() {
        viewModel.setCurrentAction(SettingsViewModel.ACTION_DELETE_DATA);
        if (viewModel.useAppLogin()) {
            //Authenticate through app login:
            if (viewModel.useBiometrics()) {
                showBiometricAuthenticationDialog(SettingsViewModel.ACTION_DELETE_DATA);
            }
            else {
                EnterPasswordDialog dialog = new EnterPasswordDialog();
                Bundle args = new Bundle();
                args.putSerializable(EnterPasswordDialog.KEY_CALLBACK_LISTENER, this);
                args.putString(EnterPasswordDialog.KEY_TITLE, getString(R.string.settings_data_delete));
                args.putString(EnterPasswordDialog.KEY_INFO, getString(R.string.settings_data_delete_dialog_info_extended));
                dialog.setArguments(args);
                dialog.show(requireActivity().getSupportFragmentManager(), "");
            }
        }
        else {
            //No app login:
            ConfirmDeleteDialog dialog = new ConfirmDeleteDialog();
            Bundle args = new Bundle();
            args.putSerializable(ConfirmDeleteDialog.KEY_CALLBACK_LISTENER, this);
            args.putString(ConfirmDeleteDialog.KEY_OBJECT, getString(R.string.settings_data_delete_dialog_info));
            dialog.setArguments(args);
            dialog.show(requireActivity().getSupportFragmentManager(), "");
        }
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
    }

}
