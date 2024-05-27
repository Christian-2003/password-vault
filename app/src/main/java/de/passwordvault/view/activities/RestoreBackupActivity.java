package de.passwordvault.view.activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.passwordvault.R;
import de.passwordvault.model.storage.backup.Backup;
import de.passwordvault.model.storage.backup.BackupException;
import de.passwordvault.model.storage.backup.XmlConfiguration;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;
import de.passwordvault.viewmodel.activities.RestoreBackupViewModel;


/**
 * Class implements the activity with which to restore a backup.
 * Pass the URI to the backup file as extra with key {@link #KEY_URI} when starting the activity.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class RestoreBackupActivity extends PasswordVaultBaseActivity {

    /**
     * Field stores the key that needs to be used when passing the URI of the backup file as extra
     * when starting the activity.
     */
    public static final String KEY_URI = "uri";


    /**
     * Attribute stores the view model for the activity.
     */
    private RestoreBackupViewModel viewModel;


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_backup);
        viewModel = new ViewModelProvider(this).get(RestoreBackupViewModel.class);

        findViewById(R.id.button_back).setOnClickListener(view -> finish());
        findViewById(R.id.button_restore).setOnClickListener(view -> restore());

        if (viewModel.getBackup() == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey(KEY_URI)) {
                Uri uri = extras.getParcelable(KEY_URI);
                createNewBackup(uri);
            }
        }
        else {
            updateContent();
        }
    }


    /**
     * Method updates the content on the display.
     */
    private void updateContent() {
        //Metadata:
        TextView filenameTextView = findViewById(R.id.text_filename);
        filenameTextView.setText(viewModel.getFilename());
        TextView createdTextView = findViewById(R.id.text_created);
        createdTextView.setText(Utils.formatDate(viewModel.getCreated(), getString(R.string.date_format)));

        //Settings container:
        LinearLayout generalSettingsContainer = findViewById(R.id.container_settings);
        String version = viewModel.getVersion();
        generalSettingsContainer.setVisibility(version == null || version.equals(XmlConfiguration.VERSION_1.getValue()) ? View.GONE : View.VISIBLE);

        //Override data options:
        RadioButton deleteRadioButton = findViewById(R.id.radio_delete);
        RadioButton replaceRadioButton = findViewById(R.id.radio_replace);
        RadioButton skipRadioButton = findViewById(R.id.radio_skip);
        switch (viewModel.getCurrentDataOption()) {
            case Backup.RestoreConfig.OVERWRITE_EXISTING_DATA:
                replaceRadioButton.setChecked(true);
                break;
            case Backup.RestoreConfig.SKIP_EXISTING_DATA:
                skipRadioButton.setChecked(true);
                break;
            default:
                deleteRadioButton.setChecked(true);
                break;
        }
        deleteRadioButton.setOnCheckedChangeListener((button, checked) -> viewModel.setCurrentDataOption(Backup.RestoreConfig.DELETE_ALL_DATA));
        replaceRadioButton.setOnCheckedChangeListener((button, checked) -> viewModel.setCurrentDataOption(Backup.RestoreConfig.OVERWRITE_EXISTING_DATA));
        skipRadioButton.setOnCheckedChangeListener((button, checked) -> viewModel.setCurrentDataOption(Backup.RestoreConfig.SKIP_EXISTING_DATA));

        //Restore settings:
        LinearLayout settingsContainer = findViewById(R.id.container_settings_settings);
        if (viewModel.areSettingsAvailable()) {
            settingsContainer.setVisibility(View.VISIBLE);
            MaterialSwitch settingsSwitch = findViewById(R.id.switch_restore_settings);
            settingsSwitch.setChecked(viewModel.getRestoreSettings());
            settingsSwitch.setOnCheckedChangeListener(this::settingsSwitchChecked);
        }
        else {
            settingsContainer.setVisibility(View.GONE);
        }

        //Restore quality gates:
        LinearLayout qualityGatesContainer = findViewById(R.id.container_settings_quality_gates);
        if (viewModel.areQualityGatesAvailable()) {
            qualityGatesContainer.setVisibility(View.VISIBLE);
            MaterialSwitch qualityGatesSwitch = findViewById(R.id.switch_restore_quality_gates);
            qualityGatesSwitch.setChecked(viewModel.getRestoreQualityGates());
            qualityGatesSwitch.setOnCheckedChangeListener(this::qualityGatesSwitchChecked);
        }
        else {
            qualityGatesContainer.setVisibility(View.GONE);
        }

        //Encryption:
        LinearLayout encryptionContainer = findViewById(R.id.container_encryption);
        encryptionContainer.setVisibility(viewModel.isBackupEncrypted() ? View.VISIBLE : View.GONE);
    }


    /**
     * Method is called whenever the switch with which to change whether to restore settings is clicked.
     *
     * @param button    Switch that was clicked.
     * @param checked   Whether the switch is checked.
     */
    private void settingsSwitchChecked(CompoundButton button, boolean checked) {
        viewModel.setRestoreSettings(checked);
    }


    /**
     * Method is called whenever the switch with which to change whether to restore quality gates is
     * clicked.
     *
     * @param button    Switch that was clicked.
     * @param checked   Whether the switch is checked.
     */
    private void qualityGatesSwitchChecked(CompoundButton button, boolean checked) {
        viewModel.setRestoreQualityGates(checked);
    }


    /**
     * Method creates a new backup in a separate thread. The UI is changed to inform the user that
     * work is done.
     *
     * @param uri   URI of the backup file to create.
     */
    private void createNewBackup(Uri uri) {
        LinearLayout contentContainer = findViewById(R.id.container_content);
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        contentContainer.setVisibility(View.GONE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                Backup backup = new Backup(uri);
                viewModel.setBackup(backup);
            }
            catch (Exception e) {
                handler.post(() -> {
                    Toast.makeText(RestoreBackupActivity.this, getString(R.string.settings_data_backup_restore_error), Toast.LENGTH_SHORT).show();
                    RestoreBackupActivity.this.finish();
                });
                return;
            }
            handler.post(() -> {
                progressBar.setVisibility(View.GONE);
                contentContainer.setVisibility(View.VISIBLE);
                updateContent();
            });
        });
    }


    /**
     * Method restores the backup asynchronously in a separate thread. If some of the entered data is
     * invalid, the UI is changed to inform the user about incorrect inputs.
     */
    private void restore() {
        if (viewModel.isBackupEncrypted()) {
            TextInputEditText passwordEditText = findViewById(R.id.input_password);
            String password = Objects.requireNonNull(passwordEditText.getText()).toString();
            TextInputLayout passwordContainer = findViewById(R.id.container_password);
            if (password.isEmpty()) {
                passwordContainer.setError(getString(R.string.error_empty_input));
                return;
            }
            else if (!viewModel.isPasswordCorrect(password)) {
                passwordContainer.setError(getString(R.string.error_passwords_incorrect));
                return;
            }
            else {
                passwordContainer.setErrorEnabled(false);
                viewModel.setPassword(password);
            }
        }

        LinearLayout contentContainer = findViewById(R.id.container_content);
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        contentContainer.setVisibility(View.GONE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                viewModel.restore();
            }
            catch (BackupException | EncryptionException e) {
                //Error occurred:
                handler.post(() -> {
                    Toast.makeText(RestoreBackupActivity.this, R.string.settings_data_backup_restore_error, Toast.LENGTH_SHORT).show();
                    RestoreBackupActivity.this.finish();
                });
            }
            //Backup restored successfully:
            handler.post(() -> {
                Toast.makeText(RestoreBackupActivity.this, R.string.settings_data_backup_restore_success, Toast.LENGTH_SHORT).show();
                RestoreBackupActivity.this.finish();
            });
        });
    }

}
