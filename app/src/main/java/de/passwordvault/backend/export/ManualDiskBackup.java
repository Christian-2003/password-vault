package de.passwordvault.backend.export;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.FileWriter;
import de.passwordvault.backend.Singleton;
import de.passwordvault.backend.entry.Entry;


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
     * Attribute stores the {@linkplain Context} with which the files are written.
     */
    private final Context context;


    /**
     * Constructor instantiates a new {@link ManualDiskBackup} instance which can create a manual
     * disk backup of all data through the method {@link #saveToExternalStorage()}.
     *
     * @param context               Context from which to create the file for the backup.
     * @throws NullPointerException The passed context is {@code null}.
     */
    public ManualDiskBackup(Context context) throws NullPointerException {
        if (context == null) {
            throw new NullPointerException("Null is invalid context");
        }
        this.context = context;
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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(context.getExternalFilesDir(null), false))) {
            Gson gson = new Gson();
            for (Entry entry : Singleton.ENTRIES.getEntries()) {
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
