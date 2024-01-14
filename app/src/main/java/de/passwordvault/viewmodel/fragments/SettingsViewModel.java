package de.passwordvault.viewmodel.fragments;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
import androidx.lifecycle.ViewModel;
import de.passwordvault.R;
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
     * Method creates an XML backup of the application data. The created backup will be encrypted if
     * the passed password is not {@code null}.
     *
     * @param directory             Directory, into which the backup shall be saved.
     * @param filename              Name of the file for the backup (e.g. 'backup.xml').
     * @param password              Password as seed for encryption key generation.
     * @throws NullPointerException The passed URI is {@code null}.
     * @throws BackupException      The backup could not be created.
     */
    public void createXmlBackup(Uri directory, String filename, String password) throws NullPointerException, BackupException {
        if (directory == null) {
            throw new NullPointerException("Null is invalid URI");
        }
        XmlBackupCreator xmlBackupCreator = new XmlBackupCreator(directory, filename, password);
        xmlBackupCreator.createBackup();
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
            Toast.makeText(context, context.getString(R.string.settings_restoration_error), Toast.LENGTH_LONG).show();
            return;
        }
        catch (XmlException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.settings_restoration_xml_error), Toast.LENGTH_LONG).show();
            return;
        }
        catch (EncryptionException e) {
            Toast.makeText(context, context.getString(R.string.settings_restoration_encryption_error), Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(context, context.getString(R.string.settings_restoration_success), Toast.LENGTH_SHORT).show();
    }

}
