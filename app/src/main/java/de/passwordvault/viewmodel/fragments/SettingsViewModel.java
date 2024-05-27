package de.passwordvault.viewmodel.fragments;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.autofill.AutofillManager;
import android.widget.Toast;
import androidx.biometric.BiometricManager;
import androidx.lifecycle.ViewModel;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.backup.BackupException;
import de.passwordvault.model.storage.backup.XmlBackupCreator2;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.model.storage.export.ExportException;
import de.passwordvault.model.storage.export.ExportToHtml2;
import de.passwordvault.model.tags.TagManager;
import de.passwordvault.view.fragments.SettingsFragment;


/**
 * Class models a {@linkplain ViewModel} for the {@linkplain SettingsFragment} which contains all
 * relevant data and functionalities that are not directly bound to the view of the fragment.
 *
 * @author  Christian-2003
 * @version 3.5.4
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
        XmlBackupCreator2 xmlBackupCreator = new XmlBackupCreator2(directory, filename, password);
        try {
            XmlBackupCreator2.BackupConfig config = new XmlBackupCreator2.BackupConfig();
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
     * Method IRREVERSIBLY deletes all data from the device!
     */
    public void deleteAllData() {
        TagManager.getInstance().clear();
        TagManager.getInstance().save(true);

        QualityGateManager.getInstance().clearQualityGates();
        QualityGateManager.getInstance().saveAllQualityGates();

        EntryManager.getInstance().clear();
        try {
            EntryManager.getInstance().save(true);
        }
        catch (StorageException e) {
            //Ignore...
        }
    }


    /**
     * Method exports the application data to readable HTML-format.
     *
     * @param uri                   URI of the HTML file to which shall be exported.
     * @param context               Context needed to display error messages.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public void exportToHtml(Uri uri, Context context) throws NullPointerException {
        if (uri == null) {
            throw new NullPointerException("Null is invalid URI");
        }
        ExportToHtml2 htmlExporter = new ExportToHtml2(uri);
        try {
            htmlExporter.export();
        }
        catch (ExportException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.settings_data_export_html_error), Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(context, context.getString(R.string.settings_data_export_html_success), Toast.LENGTH_LONG).show();
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
