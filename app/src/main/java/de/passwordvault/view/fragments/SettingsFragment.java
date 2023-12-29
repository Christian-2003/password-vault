package de.passwordvault.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import de.passwordvault.R;
import de.passwordvault.model.storage.backup.BackupException;
import de.passwordvault.model.storage.backup.CreateXmlBackup;
import de.passwordvault.model.storage.backup.RestoreXmlBackup;
import de.passwordvault.model.storage.backup.XmlException;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.view.viewmodel.SettingsViewModel;
import de.passwordvault.view.activities.MainActivity;
import de.passwordvault.view.dialogs.UiModeDialogFragment;


/**
 * Class implements the {@linkplain Fragment} that displays all settings within the
 * {@linkplain MainActivity}.
 *
 * @author  Christian-2003
 * @version 3.1.0
 */
public class SettingsFragment extends Fragment implements DialogCallbackListener {

    /**
     * Field stores the request code to pick a location for the XML file for a backup.
     */
    private static final int PICK_XML_FILE_LOCATION = 2;

    /**
     * Field stores the request coe to pick a location for the XML backup file to be restored.
     */
    private static final int PICK_XML_BACKUP_LOCATION = 3;


    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} for this fragment.
     */
    private SettingsViewModel viewModel;

    /**
     * Attribute stores the {@linkplain SharedPreferences} that are used to store all settings.
     */
    private SharedPreferences preferences;

    /**
     * Attribute stores the {@linkplain SharedPreferences.Editor} which is used to edit settings.
     */
    private SharedPreferences.Editor preferencesEditor;

    /**
     * Attribute stores the inflated view of the fragment.
     */
    private View inflated;

    /**
     * Attribute stores the tag that is used for logs.
     */
    private static final String TAG = "Settings";


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
        preferences = getContext().getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();
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
        inflated = inflater.inflate(R.layout.fragment_settings, container, false);

        //Update all settings:
        updateUiMode(preferences.getInt(getString(R.string.preferences_uimode), 0));

        //Add click listeners:
        inflated.findViewById(R.id.settings_ui_mode_clickable).setOnClickListener(view -> {
            UiModeDialogFragment dialog = new UiModeDialogFragment(viewModel.getUiMode(), SettingsFragment.this);
            dialog.setTargetFragment(SettingsFragment.this, 1);
            dialog.show(getActivity().getSupportFragmentManager(), "");
        });
        inflated.findViewById(R.id.settings_xml_data_backup_clickable).setOnClickListener(view -> createXmlBackup());
        inflated.findViewById(R.id.settings_xml_data_backup_info).setOnClickListener(view -> showInfoDialog(R.string.settings_create_data_backup, R.string.settings_xml_backup_info));
        inflated.findViewById(R.id.settings_xml_data_restoration_clickable).setOnClickListener(view -> restoreXmlBackup());
        inflated.findViewById(R.id.settings_xml_data_restoration_info).setOnClickListener(view -> showInfoDialog(R.string.settings_restore_data_backup, R.string.settings_xml_restoration_info));
        inflated.findViewById(R.id.settings_used_software_clickable).setOnClickListener(view -> startActivity(new Intent(getActivity(), OssLicensesMenuActivity.class)));
        inflated.findViewById(R.id.settings_license_clickable).setOnClickListener(view -> showInfoDialog(R.string.settings_license_notice, R.string.app_license));

        return inflated;
    }


    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'positive' button (i.e. the SAVE button).
     *
     * @param obj    Dialog which called the method.
     */
    public void onPositiveCallback(DialogFragment obj) {
        if (obj instanceof UiModeDialogFragment) {
            UiModeDialogFragment dialog = (UiModeDialogFragment)obj;
            updateUiMode(dialog.getUiMode());
        }
    }

    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'negative' button (i.e. the CANCEL button).
     *
     * @param obj    Dialog which called the method.
     */
    public void onNegativeCallback(DialogFragment obj) {
        //Do nothing...
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
        if (requestCode == PICK_XML_FILE_LOCATION && resultCode == Activity.RESULT_OK) {
            //Create backup:
            if (data != null) {
                CreateXmlBackup backup = new CreateXmlBackup(data.getData());
                try {
                    backup.createBackup();
                }
                catch (BackupException e) {
                    Toast.makeText(getContext(), getString(R.string.settings_backup_error), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getContext(), getString(R.string.settings_backup_success), Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == PICK_XML_BACKUP_LOCATION && resultCode == Activity.RESULT_OK) {
            //Restore backup:
            if (data != null) {
                RestoreXmlBackup backup = new RestoreXmlBackup(data.getData());
                try {
                    backup.restoreBackup();
                }
                catch (BackupException | XmlException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getContext(), getString(R.string.settings_restoration_success), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Method updates the UI mode of the application.
     *
     * @param uiMode    UI mode to which the app shall be changed.
     */
    private void updateUiMode(int uiMode) {
        if (viewModel.getUiMode() == uiMode) {
            //UI mode was not changed:
            return;
        }
        viewModel.setUiMode(uiMode);
        preferencesEditor.putInt(getString(R.string.preferences_uimode), uiMode);
        preferencesEditor.apply();
        switch (viewModel.getUiMode()) {
            case 1:
                //Switch to light mode:
                ((TextView)inflated.findViewById(R.id.settings_ui_mode)).setText(getString(R.string.settings_ui_mode_light));
                //TODO: Change theme...
                Log.d(TAG, "Changed UI mode to: LIGHT MODE");
                break;
            case 2:
                //Switch to dark mode:
                ((TextView)inflated.findViewById(R.id.settings_ui_mode)).setText(getString(R.string.settings_ui_mode_dark));
                //TODO: Change theme...
                Log.d(TAG, "Changed UI mode to: DARK MODE");
                break;
            default:
                //Switch to system mode:
                ((TextView)inflated.findViewById(R.id.settings_ui_mode)).setText(getString(R.string.settings_ui_mode_system));
                //TODO: Change theme...
                Log.d(TAG, "Changed UI mode to: SYSTEM DEFAULT MODE");
                break;
        }
    }


    /**
     * Method creates an XML backup for the application.
     */
    private void createXmlBackup() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");
        intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.settings_default_backup_name));
        startActivityForResult(intent, PICK_XML_FILE_LOCATION);
    }

    /**
     * Method restores an XML backup for the application.
     */
    private void restoreXmlBackup() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");
        startActivityForResult(intent, PICK_XML_BACKUP_LOCATION);
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

}
