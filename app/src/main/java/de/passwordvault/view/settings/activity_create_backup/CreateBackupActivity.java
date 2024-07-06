package de.passwordvault.view.settings.activity_create_backup;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.passwordvault.R;
import de.passwordvault.model.storage.backup.BackupException;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;


/**
 * Class implements the activity through which the user can configure and create backups.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class CreateBackupActivity extends PasswordVaultBaseActivity {

    /**
     * Attribute stores the view model of the activity.
     */
    private CreateBackupViewModel viewModel;

    /**
     * Attribute stores the activity result launcher used to start the directory-chooser to select a
     * directory in which to store the backup.
     *
     */
    private final ActivityResultLauncher<Intent> chooseDirectoryLauncher;


    /**
     * Constructor instantiates a new activity.
     */
    public CreateBackupActivity() {
        chooseDirectoryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                viewModel.setBackupDirectory(result.getData().getData());
                TextInputEditText directoryEditText = findViewById(R.id.input_directory);
                directoryEditText.setText(viewModel.getBackupDirectory().getPath());
            }
        });
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_backup);
        viewModel = new ViewModelProvider(this).get(CreateBackupViewModel.class);

        if (viewModel.getFilename() == null || viewModel.getFilename().isEmpty()) {
            viewModel.setFilename(getString(R.string.settings_data_backup_file).replace("{date}", Utils.formatDate(Calendar.getInstance(), "yyyy-MM-dd")));
        }

        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        findViewById(R.id.button_create).setOnClickListener(view -> createBackup());

        MaterialSwitch includeSettingsSwitch = findViewById(R.id.switch_include_settings);
        includeSettingsSwitch.setChecked(viewModel.includeSettings());
        includeSettingsSwitch.setOnCheckedChangeListener(this::includeSettings);
        findViewById(R.id.include_settings_clickable).setOnClickListener(view -> includeSettingsSwitch.setChecked(!includeSettingsSwitch.isChecked()));

        MaterialSwitch includeQualityGatesSwitch = findViewById(R.id.switch_include_quality_gates);
        includeQualityGatesSwitch.setChecked(viewModel.includeQualityGates());
        includeQualityGatesSwitch.setOnCheckedChangeListener(this::includeQualityGates);
        findViewById(R.id.include_quality_gates_clickable).setOnClickListener(view -> includeQualityGatesSwitch.setChecked(!includeQualityGatesSwitch.isChecked()));

        MaterialSwitch encryptBackupSwitch = findViewById(R.id.switch_encrypt_backup);
        encryptBackupSwitch.setChecked(viewModel.isBackupEncrypted());
        encryptBackupSwitch.setOnCheckedChangeListener(this::encryptBackup);
        findViewById(R.id.encrypt_backup_clickable).setOnClickListener(view -> encryptBackupSwitch.setChecked(!encryptBackupSwitch.isChecked()));

        LinearLayout encryptionContainer = findViewById(R.id.container_encryption);
        encryptionContainer.setVisibility(viewModel.isBackupEncrypted() ? View.VISIBLE : View.GONE);

        TextInputEditText filenameEditText = findViewById(R.id.input_filename);
        filenameEditText.setText(viewModel.getFilename());

        TextInputEditText directoryEditText = findViewById(R.id.input_directory);
        directoryEditText.setText(viewModel.getBackupDirectory() != null ? viewModel.getBackupDirectory().getPath() : "");
        directoryEditText.setOnClickListener(view -> chooseDirectory());
    }


    /**
     * Method creates the backup.
     */
    private void createBackup() {
        //Check data validity:
        boolean errorsDetected = false;
        TextInputLayout directoryLayout = findViewById(R.id.container_directory);
        if (viewModel.getBackupDirectory() == null) {
            directoryLayout.setError(getString(R.string.error_empty_input));
            errorsDetected = true;
        }
        else {
            directoryLayout.setErrorEnabled(false);
        }

        TextInputLayout filenameLayout = findViewById(R.id.container_filename);
        if (viewModel.getFilename() == null || viewModel.getFilename().isEmpty()) {
            filenameLayout.setError(getString(R.string.error_empty_input));
            errorsDetected = true;
        }
        else {
            filenameLayout.setErrorEnabled(false);
        }

        String password = null;
        if (viewModel.isBackupEncrypted()) {
            TextInputLayout passwordLayout = findViewById(R.id.container_password);
            TextInputEditText passwordEditText = findViewById(R.id.input_password);
            password = Objects.requireNonNull(passwordEditText.getText()).toString();
            TextInputLayout confirmPasswordLayout = findViewById(R.id.container_confirm_password);
            TextInputEditText confirmPasswordEditText = findViewById(R.id.input_confirm_password);
            String confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString();
            boolean confirmPasswordEmpty = false;
            if (password.isEmpty()) {
                passwordLayout.setError(getString(R.string.error_empty_input));
                errorsDetected = true;
            }
            else {
                passwordLayout.setErrorEnabled(false);
            }
            if (confirmPassword.isEmpty()) {
                confirmPasswordLayout.setError(getString(R.string.error_empty_input));
                errorsDetected = true;
                confirmPasswordEmpty = true;
            }
            else {
                confirmPasswordLayout.setErrorEnabled(false);
            }
            if (!password.isEmpty() && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
                confirmPasswordLayout.setError(getString(R.string.error_passwords_not_matching));
                errorsDetected = true;
            }
            else if (!confirmPasswordEmpty) {
                confirmPasswordLayout.setErrorEnabled(false);
            }
        }

        if (errorsDetected) {
            return;
        }

        //Update view:
        findViewById(R.id.container_content).setVisibility(View.GONE);
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);

        viewModel.storeSettings();

        //Create backup:
        TextInputEditText filenameEditText = findViewById(R.id.input_filename);
        String filename = Objects.requireNonNull(filenameEditText.getText()).toString();
        viewModel.setFilename(filename);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String finalPassword = password;
        executor.execute(() -> {
            boolean errorsDuringBackupCreation = false;
            try {
                viewModel.createBackup(finalPassword);
            }
            catch (BackupException | EncryptionException e) {
                errorsDuringBackupCreation = true;
            }
            boolean finalErrorsDuringBackupCreation = errorsDuringBackupCreation;
            handler.post(() -> {
                if (finalErrorsDuringBackupCreation) {
                    Toast.makeText(CreateBackupActivity.this, CreateBackupActivity.this.getString(R.string.settings_data_backup_create_error), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CreateBackupActivity.this, CreateBackupActivity.this.getString(R.string.settings_data_backup_create_success), Toast.LENGTH_SHORT).show();
                }
                CreateBackupActivity.this.finish();
            });
        });
    }


    /**
     * Method starts the system's directory-chooser activity to select the directory for the backup.
     */
    private void chooseDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        try {
            chooseDirectoryLauncher.launch(intent);
        }
        catch (Exception e) {
            Toast.makeText(this, getString(R.string.settings_data_backup_create_error), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Method is called whenever the switch to include settings changes state.
     *
     * @param button    Switch whose checked state changed.
     * @param checked   Whether the switch is now checked.
     */
    private void includeSettings(CompoundButton button, boolean checked) {
        viewModel.setIncludeSettings(checked);
    }


    /**
     * Method is called whenever the switch to include quality gates changes state.
     *
     * @param button    Switch whose checked state changed.
     * @param checked   Whether the switch is now checked.
     */
    private void includeQualityGates(CompoundButton button, boolean checked) {
        viewModel.setIncludeQualityGates(checked);
    }


    /**
     * Method is called whenever the switch to toggle backup encryption changes state.
     *
     * @param button    Switch whose checked state changed.
     * @param checked   Whether the switch is now checked.
     */
    private void encryptBackup(CompoundButton button, boolean checked) {
        viewModel.setBackupEncrypted(checked);
        LinearLayout contentContainer = findViewById(R.id.container_content);
        LinearLayout encryptionContainer = findViewById(R.id.container_encryption);
        LayoutTransition layoutTransition = contentContainer.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        encryptionContainer.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

}
