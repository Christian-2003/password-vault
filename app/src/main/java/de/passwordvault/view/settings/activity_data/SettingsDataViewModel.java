package de.passwordvault.view.settings.activity_data;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Process;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.widget.Toast;
import androidx.lifecycle.ViewModel;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.export.ExportException;
import de.passwordvault.model.storage.export.ExportToHtml2;
import de.passwordvault.model.tags.TagManager;
import de.passwordvault.view.utils.Utils;


/**
 * Class implements the view model for the {@link SettingsDataActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.3
 */
public class SettingsDataViewModel extends ViewModel {

    /**
     * Attribute stores the total space used by the app on all volumes.
     */
    private long appBytes;

    /**
     * Attribute stores the total space used by the app for it's data on all volumes.
     */
    private long dataBytes;

    /**
     * Attribute stores the total space used by the app for it's caches on all volumes.
     */
    private long cacheBytes;

    /**
     * Attribute indicates whether the storage spaces have been calculated yet.
     */
    private boolean calculatedDiskSpaces;


    /**
     * Constructor instantiates a new view model.
     */
    public SettingsDataViewModel() {
        appBytes = 0;
        dataBytes = 0;
        cacheBytes = 0;
        calculatedDiskSpaces = false;
    }


    /**
     * Method calculates the storage stats. If stats were already calculated, nothing happens unless
     * the parameter force is {@code true}.
     *
     * @param context   Context for which to calculate the storage stats.
     * @param force     Whether to force recalculation.
     */
    public void calculateStorageStats(Context context, boolean force) {
        if (calculatedDiskSpaces && !force) {
            //Already calculated disk spaces:
            return;
        }
        StorageManager storageManager = (StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
        StorageStatsManager storageStatsManager = (StorageStatsManager)context.getSystemService(Context.STORAGE_STATS_SERVICE);
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        UserHandle user = Process.myUserHandle();
        appBytes = 0;
        dataBytes = 0;
        cacheBytes = 0;
        for (StorageVolume storageVolume : storageVolumes) {
            UUID storageVolumeUuid = storageVolume.getStorageUuid();
            if (storageVolumeUuid != null) {
                try {
                    StorageStats stats = storageStatsManager.queryStatsForPackage(storageVolumeUuid, context.getPackageName(), user);
                    long volumeAppBytes = stats.getAppBytes();
                    long volumeDateBytes = stats.getDataBytes();
                    long volumeCacheBytes = stats.getCacheBytes();

                    appBytes += volumeAppBytes < 0 ? -volumeAppBytes : volumeAppBytes;
                    dataBytes += volumeDateBytes < 0 ? -volumeDateBytes : volumeDateBytes;
                    cacheBytes += volumeCacheBytes < 0 ? -volumeCacheBytes : volumeCacheBytes;
                }
                catch (IOException | PackageManager.NameNotFoundException e) {
                    continue;
                }
            }
        }
        dataBytes = dataBytes - cacheBytes;
        if (dataBytes < 0) {
            dataBytes = -dataBytes;
        }
        calculatedDiskSpaces = true;
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
     * Method returns the total space used by the app on all volumes.
     *
     * @return  Total space used by the app.
     */
    public long getAppBytes() {
        return appBytes;
    }

    /**
     * Method returns the space used by the app to store it's data on all volumes.
     *
     * @return  Spaced used to store app data.
     */
    public long getDataBytes() {
        return dataBytes;
    }

    /**
     * Method returns the space used by the app to store it's cache data on all volumes.
     *
     * @return  Space used to store cache data.
     */
    public long getCacheBytes() {
        return cacheBytes;
    }


    /**
     * Method formats the passed placeholder to contain the specified space of the specified unit.
     *
     * @param placeholder   Placeholder in which to replace {@code {unit}} and {@code {space}} with
     *                      the specified unit and space.
     * @param bytes         Space used to format (e.g. {@code 19300}).
     * @return              Formatted string.
     */
    public String formatStorageSpace(String placeholder, long bytes) {
        String formatted = placeholder;
        String unit = "B";
        double space = bytes;
        if (space >= 1024) {
            unit = "KB";
            space /= 1024;
            if (space >= 1024) {
                unit = "MB";
                space /= 1024;
            }
        }
        formatted = formatted.replace("{unit}", unit);
        formatted = formatted.replace("{space}", Utils.formatNumber(space));
        return formatted;
    }


    /**
     * Method returns whether the application uses login.
     *
     * @return  Whether the app uses login.
     */
    public boolean useAppLogin() {
        return Account.getInstance().hasPassword();
    }

}
