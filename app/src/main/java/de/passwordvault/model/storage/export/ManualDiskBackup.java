package de.passwordvault.model.storage.export;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.FileWriter;

import de.passwordvault.App;
import de.passwordvault.model.entry.Entry;
import de.passwordvault.model.entry.EntryHandle;


/**
 * Class implements a ManualDiskBackup which can manually backup all entries to external storage.
 *
 * @author  Christian-2003
 * @version 2.2.2
 */
public class ManualDiskBackup {

    /**
     * Constant stores the tag with which log messages are written.
     */
    private static final String TAG = "ManualDiskBackup";


    /**
     * Constructor instantiates a new {@link ManualDiskBackup} instance which can create a manual
     * disk backup of all data through the method {@link #saveToExternalStorage()}.
     */
    public ManualDiskBackup() {

    }


    /**
     * Method saves all entries to the external storage in JSON-format.
     * <b>IMPORTANT: In doing so, all encryption will be lost. The data can then be accessed by
     * everyone!</b>
     *
     * @throws ExternalStorageException The backup could not be created.
     */
    public void saveToExternalStorage() throws ExternalStorageException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //No storage mounted, therefore cannot write to storage:
            throw new ExternalStorageException("No storage mounted");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(App.getContext().getExternalFilesDir(null), false))) {
            Gson gson = new Gson();
            for (Entry entry : EntryHandle.getInstance().getEntries()) {
                writer.write(gson.toJson(entry));
                writer.write(System.lineSeparator());
            }
        }
        catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
                throw new ExternalStorageException(e.getMessage());
            }
            throw new ExternalStorageException("Could not create manual disk backup: Unknown error");
        }
    }

}
