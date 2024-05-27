package de.passwordvault.viewmodel.activities;

import androidx.lifecycle.ViewModel;
import java.util.Calendar;
import de.passwordvault.model.storage.backup.Backup;
import de.passwordvault.model.storage.backup.BackupException;
import de.passwordvault.model.storage.encryption.EncryptionException;


/**
 * Class implements the view model for the activity with which to restore a backup.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class RestoreBackupViewModel extends ViewModel {

    /**
     * Attribute stores the backup to restore.
     */
    private Backup backup;

    /**
     * Attribute stores the config with which to restore the backup.
     */
    private final Backup.RestoreConfig config;

    /**
     * Attribute stores the cached value for the date on which the backup was created.
     */
    private Calendar createdCache;


    /**
     * Constructor instantiates a new view model.
     */
    public RestoreBackupViewModel() {
        backup = null;
        config = new Backup.RestoreConfig();
        createdCache = null;
    }


    /**
     * Method returns the backup to be restored. This can be {@code null} if the backup was not
     * set manually.
     *
     * @return  Backup to be restored.
     */
    public Backup getBackup() {
        return backup;
    }

    /**
     * Method changes the backup to restore.
     *
     * @param backup                Backup to be restored.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setBackup(Backup backup) throws NullPointerException {
        if (backup == null) {
            throw new NullPointerException();
        }
        this.backup = backup;
    }

    /**
     * Method returns the filename of the backup.
     *
     * @return  Filename of the backup.
     */
    public String getFilename() {
        if (backup == null) {
            return "";
        }
        return backup.getFilename();
    }

    /**
     * Method returns the created date of the backup.
     *
     * @return  Date on which the backup was created.
     */
    public Calendar getCreated() {
        if (createdCache == null) {
            if (backup == null) {
                return Calendar.getInstance();
            }
            else {
                String created = backup.getMetadata(Backup.CREATED);
                if (created == null) {
                    createdCache = Calendar.getInstance();
                }
                else {
                    Calendar calendar = Calendar.getInstance();
                    try {
                        calendar.setTimeInMillis(Long.parseLong(created));
                    }
                    catch (NumberFormatException e) {
                        //Ignore
                    }
                    createdCache = calendar;
                }
            }
        }
        return createdCache;
    }

    /**
     * Method returns whether settings are available with the backup.
     *
     * @return  Whether settings are available.
     */
    public boolean areSettingsAvailable() {
        if (backup == null) {
            return false;
        }
        return Boolean.parseBoolean(backup.getMetadata(Backup.INCLUDE_SETTINGS));
    }

    /**
     * Method returns whether quality gates are available with the backup.
     *
     * @return  Whether quality gates are available.
     */
    public boolean areQualityGatesAvailable() {
        if (backup == null) {
            return false;
        }
        return Boolean.parseBoolean(backup.getMetadata(Backup.INCLUDE_QUALITY_GATES));
    }

    /**
     * Method returns whether the backup is encrypted.
     *
     * @return  Whether the backup is encrypted.
     */
    public boolean isBackupEncrypted() {
        if (backup == null) {
            return false;
        }
        return backup.isEncrypted();
    }

    /**
     * Method returns the current data option of the config.
     *
     * @return  Current data option.
     */
    public int getCurrentDataOption() {
        return config.getCurrentDataOption();
    }

    /**
     * Method changes the current data option for restoring the backup.
     *
     * @param currentDataOption New current data option.
     */
    public void setCurrentDataOption(int currentDataOption) {
        config.setCurrentDataOption(currentDataOption);
    }

    /**
     * Method returns whether to restore settings with the backup.
     *
     * @return  Whether to restore settings.
     */
    public boolean getRestoreSettings() {
        return config.getRestoreSettings();
    }

    /**
     * Method changes whether to restore settings with the backup.
     *
     * @param restoreSettings   Whether to restore settings.
     */
    public void setRestoreSettings(boolean restoreSettings) {
        config.setRestoreSettings(restoreSettings);
    }

    /**
     * Method returns whether to restore quality gates with the backup.
     *
     * @return  Whether to restore quality gates with the backup.
     */
    public boolean getRestoreQualityGates() {
        return config.getRestoreQualityGates();
    }

    /**
     * Method changes whether to restore quality gates with the backup.
     *
     * @param restoreQualityGates   Whether to restore quality gates with the backup.
     */
    public void setRestoreQualityGates(boolean restoreQualityGates) {
        config.setRestoreQualityGates(restoreQualityGates);
    }

    /**
     * Method changes the password used to generate the key to decrypt the backup (if required).
     *
     * @param password  Password to use as seed for the AES key.
     */
    public void setPassword(String password) {
        config.setEncryptionKeySeed(password);
    }

    /**
     * Method returns the version number of the backup.
     *
     * @return  Version number.
     */
    public String getVersion() {
        if (backup == null) {
            return null;
        }
        return backup.getMetadata(Backup.BACKUP_VERSION);
    }


    /**
     * Method tests whether the passed password is valid.
     *
     * @param password  Password whose validity to test.
     * @return          Whether the passed password is valid.
     */
    public boolean isPasswordCorrect(String password) {
        if (backup == null) {
            return false;
        }
        try {
            return backup.isEncryptionSeedValid(password);
        }
        catch (EncryptionException e) {
            return false;
        }
    }


    /**
     * Method restores the backup according to the passed configuration. This process takes an
     * significant amount of time, so do NOT execute on the main thread.
     *
     * @throws BackupException      The backup could not be restored.
     * @throws EncryptionException  The backup could not be decrypted.
     */
    public void restore() throws BackupException, EncryptionException {
        if (backup != null) {
            backup.restoreBackup(config);
        }
    }

}
