package de.passwordvault.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import java.io.Serializable;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.model.storage.backup.XmlBackupRestorer;
import de.passwordvault.view.dialogs.CreateBackupDialog;
import de.passwordvault.view.dialogs.RestoreBackupDialog;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.viewmodel.fragments.SettingsViewModel;
import de.passwordvault.view.activities.MainActivity;


/**
 * Class implements the {@linkplain Fragment} that displays all settings within the
 * {@linkplain MainActivity}.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class SettingsFragment extends Fragment implements DialogCallbackListener, Serializable {

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
        View inflated = inflater.inflate(R.layout.fragment_settings, container, false);

        inflated.findViewById(R.id.settings_xml_data_backup_clickable).setOnClickListener(view -> selectDirectory(SELECT_DIRECTORY_TO_CREATE_BACKUP));
        inflated.findViewById(R.id.settings_xml_data_backup_info).setOnClickListener(view -> showInfoDialog(R.string.settings_create_data_backup, R.string.settings_xml_backup_info));
        inflated.findViewById(R.id.settings_xml_data_restoration_clickable).setOnClickListener(view -> selectFile(SELECT_FILE_TO_RESTORE_BACKUP, "text/plain"));
        inflated.findViewById(R.id.settings_xml_data_restoration_info).setOnClickListener(view -> showInfoDialog(R.string.settings_restore_data_backup, R.string.settings_xml_restoration_info));
        inflated.findViewById(R.id.settings_used_software_clickable).setOnClickListener(view -> startActivity(new Intent(getActivity(), OssLicensesMenuActivity.class)));
        inflated.findViewById(R.id.settings_license_clickable).setOnClickListener(view -> showInfoDialog(R.string.settings_license_notice, R.string.app_license));
        inflated.findViewById(R.id.settings_open_source_clickable).setOnClickListener(view -> openUrl(getString(R.string.settings_github_link)));
        inflated.findViewById(R.id.settings_bug_report_clickable).setOnClickListener(view -> openUrl(getString(R.string.settings_github_issues_link)));
        inflated.findViewById(R.id.settings_update_clickable).setOnClickListener(view -> openUrl(getString(R.string.settings_github_releases_link)));
        inflated.findViewById(R.id.settings_html_export_clickable).setOnClickListener(view -> createFile(SELECT_FILE_TO_EXPORT_TO_HTML, "text/html", getString(R.string.settings_default_export_name)));
        inflated.findViewById(R.id.settings_html_export_info).setOnClickListener(view -> showInfoDialog(R.string.settings_export_html, R.string.settings_export_html_info));

        return inflated;
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
                    Log.d("BACKUP", e.getMessage());
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
    }


    /**
     * Method is called whenever a negative dialog callback is initiated.
     *
     * @param fragment  Dialog which called the method.
     */
    @Override
    public void onNegativeCallback(DialogFragment fragment) {

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
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
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

}
