package de.passwordvault.viewmodel.fragments;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.biometric.BiometricManager;
import androidx.lifecycle.ViewModel;

import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.storage.backup.BackupException;
import de.passwordvault.model.storage.backup.XmlBackupCreator;
import de.passwordvault.model.storage.backup.XmlBackupRestorer;
import de.passwordvault.model.storage.backup.XmlException;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.model.storage.export.ExportException;
import de.passwordvault.model.storage.export.ExportToHtml;
import de.passwordvault.view.fragments.SettingsFragment;


/**
 * Class models a {@linkplain ViewModel} for the {@linkplain SettingsFragment} which contains all
 * relevant data and functionalities that are not directly bound to the view of the fragment.
 *
 * @author  Christian-2003
 * @version 3.2.0
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
        XmlBackupCreator xmlBackupCreator = new XmlBackupCreator(directory, filename, password);
        try {
            xmlBackupCreator.createBackup();
        }
        catch (BackupException e) {
            Toast.makeText(context, context.getString(R.string.settings_security_backup_error), Toast.LENGTH_LONG).show();
        }
        Toast.makeText(context, context.getString(R.string.settings_security_backup_success), Toast.LENGTH_SHORT).show();
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
        ExportToHtml htmlExporter = new ExportToHtml(uri);
        try {
            htmlExporter.export();
        }
        catch (ExportException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.settings_export_error), Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(context, context.getString(R.string.settings_export_success), Toast.LENGTH_LONG).show();
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
     * Method restores an XML backup at the specified URI.
     *
     * @param uri                   URI of the file containing the XML backup.
     * @param password              Password as seed for key generation. Pass {@code null} if the
     *                              backup is not encrypted.
     * @param context               Context needed to display error messages to the user.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public void restoreXmlBackup(Uri uri, String password, Context context) throws NullPointerException {
        if (uri == null) {
            throw new NullPointerException("Null is invalid URI");
        }
        XmlBackupRestorer xmlBackupRestorer = new XmlBackupRestorer(uri, password);
        try {
            xmlBackupRestorer.restoreBackup();
        }
        catch (BackupException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.settings_security_restore_error), Toast.LENGTH_LONG).show();
            return;
        }
        catch (XmlException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.settings_security_restore_error_xml), Toast.LENGTH_LONG).show();
            return;
        }
        catch (EncryptionException e) {
            Toast.makeText(context, context.getString(R.string.settings_security_restore_error_encryption), Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(context, context.getString(R.string.settings_security_restore_success), Toast.LENGTH_SHORT).show();
    }


    /**
     * Method tests whether the login shall be done using biometrics.
     *
     * @return  Whether the login shall be done using biometrics.
     */
    public boolean useBiometrics() {
        return Account.getInstance().useBiometrics();
    }

}
