package de.passwordvault.viewmodel.activities;

import android.net.Uri;
import androidx.lifecycle.ViewModel;
import de.passwordvault.model.storage.backup.BackupException;
import de.passwordvault.model.storage.backup.XmlBackupCreator2;
import de.passwordvault.model.storage.encryption.EncryptionException;


/**
 * Class implements the view model for the activity through which the user can create a backup.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class CreateBackupViewModel extends ViewModel {

    /**
     * Attribute stores the path to the directory in which the backup shall be created.
     */
    private Uri backupPath;

    /**
     * Attribute stores the backup filename.
     */
    private String filename;

    /**
     * Attribute stores the backup config.
     */
    private final XmlBackupCreator2.BackupConfig config;

    /**
     * Attribute stores whether the backup shall be encrypted.
     */
    private boolean backupEncrypted;


    /**
     * Constructor instantiates a new view model.
     */
    public CreateBackupViewModel() {
        backupPath = null;
        filename = null;
        config = new XmlBackupCreator2.BackupConfig();
        backupEncrypted = false;
    }


    /**
     * Method returns the path for the directory.
     *
     * @return  Path for the directory.
     */
    public Uri getBackupPath() {
        return backupPath;
    }

    /**
     * Method changes the path for the directory.
     *
     * @param backupPath            New path for the directory.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setBackupPath(Uri backupPath) throws NullPointerException {
        if (backupPath == null) {
            throw new NullPointerException();
        }
        this.backupPath = backupPath;
    }

    /**
     * Method returns the filename of the backup file.
     *
     * @return  Filename of the backup file.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Method changes the filename of the backup file.
     *
     * @param filename              New filename for the backup file.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setFilename(String filename) throws NullPointerException {
        if (filename == null) {
            throw new NullPointerException();
        }
        this.filename = filename;
    }

    /**
     * Method returns whether the settings are included in the backup.
     *
     * @return  Whether the settings are included.
     */
    public boolean includeSettings() {
        return config.getIncludeSettings();
    }

    /**
     * Method changes whether the settings are included in the backup.
     *
     * @param includeSettings   Whether the settings are included.
     */
    public void setIncludeSettings(boolean includeSettings) {
        config.setIncludeSettings(includeSettings);
    }

    /**
     * Method returns whether the quality gates are included in the backup.
     *
     * @return  Whether the quality gates are included.
     */
    public boolean includeQualityGates() {
        return config.getIncludeQualityGates();
    }

    /**
     * Method changes whether the quality gates are included in the backup.
     *
     * @param includeQualityGates   Whether the quality gates are included.
     */
    public void setIncludeQualityGates(boolean includeQualityGates) {
        config.setIncludeQualityGates(includeQualityGates);
    }

    /**
     * Method returns whether the backup is encrypted.
     *
     * @return  Whether the backup is encrypted.
     */
    public boolean isBackupEncrypted() {
        return backupEncrypted;
    }

    /**
     * Method changes whether the backup is encrypted.
     *
     * @param backupEncrypted   Whether the backup is encrypted.
     */
    public void setBackupEncrypted(boolean backupEncrypted) {
        this.backupEncrypted = backupEncrypted;
    }


    /**
     * Method creates a backup for the passed password. Pass {@code null} if no password is provided.
     *
     * @param password              Password with which to encrypt the backup.
     * @throws BackupException      The backup could not be created.
     * @throws EncryptionException  The data could not be encrypted.
     */
    public void createBackup(String password) throws BackupException, EncryptionException {
        XmlBackupCreator2 backupCreator2 = new XmlBackupCreator2(backupPath, filename, password);
        backupCreator2.createBackup(config);
    }

}
