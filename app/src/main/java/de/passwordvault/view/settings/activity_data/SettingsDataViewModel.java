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
 * @version 3.6.1
 */
public class SettingsDataViewModel extends ViewModel {

    /**
     * Attribute stores the total free disk space.
     */
    private double totalFreeDiskSpace;

    private double totalUsedDiskSpace;

    private double totalAppSpace;

    private double totalDataSpace;

    private double totalCacheSpace;

    private boolean calculatedDiskSpaces;


    /**
     * Constructor instantiates a new view model.
     */
    public SettingsDataViewModel() {
        totalFreeDiskSpace = 0;
        totalUsedDiskSpace = 0;
        totalAppSpace = 0;
        totalDataSpace = 0;
        totalCacheSpace = 0;
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
        long freeDiskBytes = 0;
        long usedDiskBytes = 0;
        long appBytes = 0;
        long dataBytes = 0;
        long cacheBytes = 0;
        for (StorageVolume storageVolume : storageVolumes) {
            UUID storageVolumeUuid = storageVolume.getStorageUuid();
            if (storageVolumeUuid != null) {
                try {
                    freeDiskBytes += storageStatsManager.getFreeBytes(storageVolumeUuid);
                    usedDiskBytes += storageStatsManager.getTotalBytes(storageVolumeUuid);
                    StorageStats stats = storageStatsManager.queryStatsForPackage(storageVolumeUuid, context.getPackageName(), user);
                    appBytes += stats.getAppBytes();
                    dataBytes += stats.getDataBytes();
                    cacheBytes += stats.getCacheBytes();
                }
                catch (IOException | PackageManager.NameNotFoundException e) {
                    continue;
                }
            }
        }
        totalFreeDiskSpace = freeDiskBytes / 1024.0 / 1024;
        totalUsedDiskSpace = (usedDiskBytes - freeDiskBytes) / 1024.0 / 1024;
        totalAppSpace = appBytes / 1024.0 / 1024;
        totalDataSpace = (appBytes - dataBytes) / 1024.0 / 1024;
        totalCacheSpace = cacheBytes / 1024.0 / 1024;
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


    public double getTotalFreeDiskSpace() {
        return totalFreeDiskSpace;
    }

    public double getTotalUsedDiskSpace() {
        return totalUsedDiskSpace;
    }

    public double getTotalAppSpace() {
        return totalAppSpace;
    }

    public double getTotalDataSpace() {
        return totalDataSpace;
    }

    public double getTotalCacheSpace() {
        return totalCacheSpace;
    }



    public String formatStorageSpace(String orig, double space, String unit) {
        String formatted = orig;
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
