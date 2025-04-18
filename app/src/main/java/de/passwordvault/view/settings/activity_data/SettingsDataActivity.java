package de.passwordvault.view.settings.activity_data;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.util.Calendar;
import java.util.concurrent.Executor;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.security.authentication.AuthenticationCallback;
import de.passwordvault.model.security.authentication.AuthenticationFailure;
import de.passwordvault.model.security.authentication.Authenticator;
import de.passwordvault.view.general.dialog_delete.DeleteDialog;
import de.passwordvault.view.settings.activity_create_backup.CreateBackupActivity;
import de.passwordvault.view.settings.activity_restore_backup.RestoreBackupActivity;
import de.passwordvault.view.settings.activity_security.SettingsSecurityActivity;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;
import de.passwordvault.view.utils.components.SegmentedProgressBar;


/**
 * Class implements the settings data activity.
 *
 * @author  Christian-2003
 * @version 3.7.3
 */
public class SettingsDataActivity extends PasswordVaultActivity<SettingsDataViewModel> implements AuthenticationCallback, PasswordVaultBottomSheetDialog.Callback {

    /**
     * Field stores the tag used for authenticating when deleting all app data.
     */
    private static final String TAG_AUTH_DELETE = "delete";


    /**
     * Attribute stores the activity result launcher for selecting a directory for the HTML export.
     */
    private final ActivityResultLauncher<Intent> htmlExportLauncher;

    /**
     * Attribute stores the activity result launcher for selecting a file from which to restore an
     * XML backup.
     */
    private final ActivityResultLauncher<Intent> restoreXmlBackupLauncher;


    /**
     * Constructor instantiates a new settings data activity.
     */
    public SettingsDataActivity() {
        super(SettingsDataViewModel.class, R.layout.activity_settings_data);

        //HTML export:
        htmlExportLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                viewModel.exportToHtml(result.getData().getData(), SettingsDataActivity.this);
            }
        });

        restoreXmlBackupLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent intent = new Intent(SettingsDataActivity.this, RestoreBackupActivity.class);
                intent.putExtra(RestoreBackupActivity.KEY_URI, result.getData().getData());
                startActivity(intent);
            }
        });
    }


    /**
     * Method is called whenever a dialog is closed with a callback.
     * @param dialog        Dialog that was closed.
     * @param resultCode    Result code is either {@link #RESULT_SUCCESS} or {@link #RESULT_CANCEL}
     *                      and indicates how the dialog is closed.
     */
    @Override
    public void onCallback(PasswordVaultBottomSheetDialog<? extends ViewModel> dialog, int resultCode) {
        if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS && dialog instanceof DeleteDialog) {
            viewModel.deleteAllData();
            updateStorageData(true);
        }
    }


    /**
     * Method is called on successful authentication.
     *
     * @param tag   Tag that was passed with the authentication request.
     */
    @Override
    public void onAuthenticationSuccess(@Nullable String tag) {
        if (tag == null) {
            return;
        }
        if (tag.equals(TAG_AUTH_DELETE)) {
            viewModel.deleteAllData();
            updateStorageData(true);
        }
    }

    /**
     * Method is called on failed authentication.
     *
     * @param tag   Tag that was passed with the authentication request.
     * @param code  Authentication failure code indicating why the authentication failed.
     */
    @Override
    public void onAuthenticationFailure(@Nullable String tag, @NonNull AuthenticationFailure code) {

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

        //Export:
        findViewById(R.id.settings_data_export_html_container).setOnClickListener(view -> htmlExport());

        //Backup:
        findViewById(R.id.settings_data_backup_create_container).setOnClickListener(view -> startActivity(new Intent(this, CreateBackupActivity.class)));
        findViewById(R.id.settings_data_backup_restore_container).setOnClickListener(view -> restoreXmlBackup());

        //Storage:
        updateStorageData(false);

        //Delete:
        findViewById(R.id.settings_data_delete_all_container).setOnClickListener(view -> deleteAllData());
    }


    /**
     * Method starts the process of creating an HTML export.
     */
    private void htmlExport() {
        String fileName = getString(R.string.settings_data_export_file).replace("{date}", Utils.formatDate(Calendar.getInstance(), "yyyy-MM-dd"));
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        try {
            htmlExportLauncher.launch(intent);
        }
        catch (Exception e) {
            Toast.makeText(this, getString(R.string.settings_data_export_html_error), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method starts the process of restoring an XML backup.
     */
    private void restoreXmlBackup() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); //Require mime-type '*/*' to both restore text-backups (version 3.2.0 onwards) and binary-backups (version 3.1.0)
        try {
            restoreXmlBackupLauncher.launch(intent);
        }
        catch (Exception e) {
            Toast.makeText(this, getString(R.string.settings_data_backup_restore_error), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method starts the process to delete all data.
     */
    private void deleteAllData() {
        if (viewModel.useAppLogin()) {
            //Authenticate through app login:
            Authenticator authenticator = new Authenticator(this, TAG_AUTH_DELETE, R.string.settings_data_delete_all);
            authenticator.authenticate(this);
        }
        else {
            //No app login with which to authenticate:
            DeleteDialog dialog = new DeleteDialog();
            Bundle args = new Bundle();
            args.putString(DeleteDialog.ARG_MESSAGE, getString(R.string.settings_data_delete_all_info_dialog));
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "");
        }
    }


    /**
     * Method updates the storage display within the activity.
     *
     * @param force Whether to force recalculate the storage used by the app.
     */
    private void updateStorageData(boolean force) {
        SegmentedProgressBar progressBar = findViewById(R.id.progress_bar);
        Executor mainExecutor = getMainExecutor();
        App.getExecutor().execute(() -> {
            viewModel.calculateStorageStats(this, force);
            String placeholder = getString(R.string.settings_data_storage_placeholder);
            long appBytes = viewModel.getAppBytes();
            long dataBytes = viewModel.getDataBytes();
            long cacheBytes = viewModel.getCacheBytes();
            long totalBytes = appBytes + dataBytes + cacheBytes;
            float appBytesPercentage = (float)appBytes / totalBytes;
            float dataBytesPercentage = (float)dataBytes / totalBytes;
            float cacheBytesPercentage = (float)cacheBytes / totalBytes;
            String appSpaceText = viewModel.formatStorageSpace(placeholder, appBytes);
            String dataSpaceText = viewModel.formatStorageSpace(placeholder, dataBytes);
            String cacheSpaceText = viewModel.formatStorageSpace(placeholder, cacheBytes);
            progressBar.clearSegments();
            progressBar.addSegment(new SegmentedProgressBar.Segment(appBytesPercentage, getColor(R.color.text_light)));
            progressBar.addSegment(new SegmentedProgressBar.Segment(dataBytesPercentage, getColor(R.color.primary)));
            progressBar.addSegment(new SegmentedProgressBar.Segment(cacheBytesPercentage, getColor(R.color.green)));
            mainExecutor.execute(() -> {
                ((TextView)findViewById(R.id.app)).setText(appSpaceText);
                ((TextView)findViewById(R.id.data)).setText(dataSpaceText);
                ((TextView)findViewById(R.id.cache)).setText(cacheSpaceText);
                progressBar.invalidate();
            });
        });
    }

}
