package de.passwordvault.view.settings.activity_data;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.Calendar;
import java.util.concurrent.Executor;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.security.authentication.AuthenticationCallback;
import de.passwordvault.model.security.authentication.AuthenticationFailure;
import de.passwordvault.model.security.authentication.Authenticator;
import de.passwordvault.view.settings.activity_create_backup.CreateBackupActivity;
import de.passwordvault.view.settings.activity_restore_backup.RestoreBackupActivity;
import de.passwordvault.view.entries.activity_add_entry.dialog_delete.ConfirmDeleteDialog;
import de.passwordvault.view.settings.activity_security.SettingsSecurityActivity;
import de.passwordvault.view.settings.activity_quality_gates.QualityGatesActivity;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;
import de.passwordvault.view.utils.components.SegmentedProgressBar;


/**
 * Class implements the settings data activity.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class SettingsDataActivity extends PasswordVaultBaseActivity implements DialogCallbackListener, AuthenticationCallback {

    /**
     * Field stores the tag used for authenticating when deleting all app data.
     */
    private static final String TAG_AUTH_DELETE = "delete";


    /**
     * Attribute stores the view model of the activity.
     */
    private SettingsDataViewModel viewModel;

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
     * Method is called on positive callback from a dialog.
     *
     * @param fragment  Dialog which called the method.
     */
    @Override
    public void onPositiveCallback(DialogFragment fragment) {
        if (fragment instanceof ConfirmDeleteDialog) {
            viewModel.deleteAllData();
            updateStorageData(true);
        }
    }

    /**
     * Method is called on negative callback from a dialog.
     *
     * @param fragment  Dialog which called the method.
     */
    @Override
    public void onNegativeCallback(DialogFragment fragment) {

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
        setContentView(R.layout.activity_settings_data);
        viewModel = new ViewModelProvider(this).get(SettingsDataViewModel.class);

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

        //Search:
        findViewById(R.id.button_search_qualitygates).setOnClickListener(view -> startActivity(new Intent(this, QualityGatesActivity.class)));
        findViewById(R.id.button_search_masterpassword).setOnClickListener(view -> startActivity(new Intent(this, SettingsSecurityActivity.class)));
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
            ConfirmDeleteDialog dialog = new ConfirmDeleteDialog();
            Bundle args = new Bundle();
            args.putSerializable(ConfirmDeleteDialog.KEY_CALLBACK_LISTENER, this);
            args.putString(ConfirmDeleteDialog.KEY_MESSAGE, getString(R.string.settings_data_delete_all_info_dialog));
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "");
        }
    }


    private void updateStorageData(boolean force) {
        SegmentedProgressBar progressBar = findViewById(R.id.progress_bar);
        Executor mainExecutor = getMainExecutor();
        App.getExecutor().execute(() -> {
            viewModel.calculateStorageStats(this, force);
            String placeholder = getString(R.string.settings_data_storage_placeholder);
            double usedSpaceMb = viewModel.getTotalUsedDiskSpace();
            double freeSpaceMb = viewModel.getTotalFreeDiskSpace();
            double appSpaceMb = viewModel.getTotalAppSpace();
            double dataSpaceMb = viewModel.getTotalDataSpace();
            double cacheSpaceMb = viewModel.getTotalCacheSpace();
            double totalSpace = usedSpaceMb + freeSpaceMb + appSpaceMb + dataSpaceMb + cacheSpaceMb;
            float usedSpacePercentage = (float)(usedSpaceMb / totalSpace);
            float appSpacePercentage = (float)(appSpaceMb / totalSpace);
            float dataSpacePercentage = (float)(dataSpaceMb / totalSpace);
            float cacheSpacePercentage = (float)(cacheSpaceMb / totalSpace);
            String usedSpaceText = viewModel.formatStorageSpace(placeholder, usedSpaceMb / 1024, "GB");
            String freeSpaceText = viewModel.formatStorageSpace(placeholder, freeSpaceMb / 1024, "GB");
            String appSpaceText = viewModel.formatStorageSpace(placeholder, appSpaceMb, "MB");
            String dataSpaceText = viewModel.formatStorageSpace(placeholder, dataSpaceMb, "MB");
            String cacheSpaceText = viewModel.formatStorageSpace(placeholder, cacheSpaceMb, "MB");
            progressBar.clearSegments();
            progressBar.addSegment(new SegmentedProgressBar.Segment(usedSpacePercentage, getColor(R.color.pv_text_secondary)));
            progressBar.addSegment(new SegmentedProgressBar.Segment(appSpacePercentage, getColor(R.color.pv_primary)));
            progressBar.addSegment(new SegmentedProgressBar.Segment(dataSpacePercentage, getColor(R.color.pv_green)));
            progressBar.addSegment(new SegmentedProgressBar.Segment(cacheSpacePercentage, getColor(R.color.pv_yellow)));
            mainExecutor.execute(() -> {
                ((TextView)findViewById(R.id.used)).setText(usedSpaceText);
                ((TextView)findViewById(R.id.free)).setText(freeSpaceText);
                ((TextView)findViewById(R.id.app)).setText(appSpaceText);
                ((TextView)findViewById(R.id.data)).setText(dataSpaceText);
                ((TextView)findViewById(R.id.cache)).setText(cacheSpaceText);
                progressBar.invalidate();
            });
        });
    }

}
