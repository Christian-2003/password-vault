package de.passwordvault.view.activity_main.fragment_settings;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.autofill.AutofillManager;
import android.widget.Toast;
import androidx.biometric.BiometricManager;
import androidx.lifecycle.ViewModel;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.storage.backup.BackupException;
import de.passwordvault.model.storage.backup.BackupCreator;
import de.passwordvault.model.storage.encryption.EncryptionException;


/**
 * Class models a {@linkplain ViewModel} for the {@linkplain SettingsFragment} which contains all
 * relevant data and functionalities that are not directly bound to the view of the fragment.
 *
 * @author  Christian-2003
 * @version 3.6.1
 */
public class SettingsViewModel extends ViewModel {

    /**
     * Method tests whether class 3 biometrics are available on the Android device.
     *
     * @return  Whether class 3 biometrics are available.
     */
    public boolean areBiometricsAvailable() {
        BiometricManager biometricManager = BiometricManager.from(App.getContext());
        int result = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        return result == BiometricManager.BIOMETRIC_SUCCESS;
    }


    /**
     * Method creates an XML backup of the application data. The created backup will be encrypted if
     * the passed password is not {@code null}.
     *
     * @param directory             Directory, into which the backup shall be saved.
     * @param filename              Name of the file for the backup (e.g. 'backup.xml').
     * @param password              Password as seed for encryption key generation.
     * @param context               Context needed to display error messages.
     * @throws NullPointerException The passed directory, filename or context is {@code null}.
     */
    public void createXmlBackup(Uri directory, String filename, String password, Context context) throws NullPointerException {
        if (directory == null) {
            throw new NullPointerException("Null is invalid URI");
        }
        if (context == null) {
            throw new NullPointerException("Null is invalid context");
        }
        if (filename == null) {
            throw new NullPointerException("Null is invalid filename");
        }
        BackupCreator xmlBackupCreator = new BackupCreator(directory, filename, password);
        try {
            BackupCreator.BackupConfig config = new BackupCreator.BackupConfig();
            config.setIncludeSettings(true);
            config.setIncludeQualityGates(true);
            xmlBackupCreator.createBackup(config);
        }
        catch (BackupException | EncryptionException e) {
            Log.e("XML", e.getMessage() != null ? e.getMessage() : "No message provided.");
            Toast.makeText(context, context.getString(R.string.settings_data_backup_create_error), Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context, context.getString(R.string.settings_data_backup_create_success), Toast.LENGTH_SHORT).show();
    }


    /**
     * Method returns whether the application uses login.
     *
     * @return  Whether the app uses login.
     */
    public boolean useAppLogin() {
        return Account.getInstance().hasPassword();
    }


    /**
     * Method tests whether the login shall be done using biometrics.
     *
     * @return  Whether the login shall be done using biometrics.
     */
    public boolean useBiometrics() {
        return Account.getInstance().useBiometrics();
    }


    /**
     * Method tests whether the app is used as the autofill manager.
     *
     * @return  Whether Password Vault is used as autofill manager.
     */
    public boolean useAutofillService() {
        AutofillManager manager = (AutofillManager)App.getContext().getSystemService(AutofillManager.class);
        return manager != null && manager.hasEnabledAutofillServices();
    }

}
