package de.passwordvault.viewmodel.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.service.autofill.AutofillService;
import android.util.Log;
import android.view.autofill.AutofillManager;
import android.widget.Toast;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.backup.BackupException;
import de.passwordvault.model.storage.backup.XmlBackupCreator;
import de.passwordvault.model.storage.backup.XmlBackupRestorer;
import de.passwordvault.model.storage.backup.XmlException;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.model.storage.export.ExportException;
import de.passwordvault.model.storage.export.ExportToHtml;
import de.passwordvault.model.tags.TagManager;
import de.passwordvault.view.fragments.SettingsFragment;


/**
 * Class models a {@linkplain ViewModel} for the {@linkplain SettingsFragment} which contains all
 * relevant data and functionalities that are not directly bound to the view of the fragment.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class SettingsViewModel extends ViewModel {

    /**
     * Field indicates that no action takes place.
     */
    public static final byte ACTION_NONE = -1;

    /**
     * Field indicates that biometrics are used to turn biometrics on.
     */
    public static final byte ACTION_TURN_BIOMETRICS_ON = 0;

    /**
     * Field indicates that biometrics are used to turn biometrics off.
     */
    public static final byte ACTION_TURN_BIOMETRICS_OFF = 1;

    /**
     * Field indicates that biometrics are used to disable login.
     */
    public static final byte ACTION_DISABLE_LOGIN = 2;

    /**
     * Field indicates that biometrics are used to disable login.
     */
    public static final byte ACTION_DELETE_DATA = 3;


    /**
     * Attribute indicates the current biometric action.
     */
    private byte currentAction;

    /**
     * Attribute stores the prompt info for the biometric prompt.
     */
    private final BiometricPrompt.PromptInfo biometricPromptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle(App.getContext().getString(R.string.login_biometrics_title)).setNegativeButtonText(App.getContext().getString(R.string.button_cancel)).build();

    /**
     * Attribute stores the executor that is used for executing the biometric-login dialog.
     */
    private final Executor executor = ContextCompat.getMainExecutor(App.getContext());


    /**
     * Method returns the current biometric action.
     *
     * @return  Current biometric action.
     */
    public byte getCurrentAction() {
        return currentAction;
    }

    /**
     * Method changes the current biometric action to the passed argument.
     *
     * @param currentAction    New biometric action.
     */
    public void setCurrentAction(byte currentAction) {
        this.currentAction = currentAction;
    }

    /**
     * Method returns the {@link #biometricPromptInfo}.
     *
     * @return  Prompt info of the biometric prompt.
     */
    public BiometricPrompt.PromptInfo getBiometricPromptInfo() {
        return biometricPromptInfo;
    }

    /**
     * Method returns the {@link #executor}.
     *
     * @return  Executor.
     */
    public Executor getExecutor() {
        return executor;
    }


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
     * Method tests whether the provided password is correct.
     *
     * @param s Password to be tested.
     * @return  Whether the password matches.
     */
    public boolean confirmPassword(String s) throws NullPointerException {
        return Account.getInstance().isPassword(s);
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
        setCurrentAction(ACTION_NONE);
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
            Log.d("RESTORE", "Invalid password: " + e.getMessage());
            e.printStackTrace();
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
