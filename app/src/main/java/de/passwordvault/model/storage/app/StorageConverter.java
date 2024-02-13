package de.passwordvault.model.storage.app;

import java.io.File;
import java.util.ArrayList;
import de.passwordvault.App;
import de.passwordvault.model.detail.DetailBackupDTO;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.storage.csv.CsvConfiguration;
import de.passwordvault.model.storage.file.EncryptedFileReader;


/**
 * Class models a converter which can convert the stored data from before version 3.3.0 to the new
 * data format. After conversion, no previous data will exist, all data will be saved to the device
 * storage and the data will be loaded in the app as if the app started normally.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class StorageConverter {

    /**
     * Field stores the file name in which the entries are stored.
     */
    private static final String FILE_ENTRIES = "entries.csv";

    /**
     * Field stores the file name in which the details were stored.
     */
    private static final String FILE_DETAILS = "details.csv";


    /**
     * Attribute stores the list of entries that have been loaded from storage.
     */
    private final ArrayList<EntryAbbreviated> entries;

    /**
     * Attribute stores the list of details that have been loaded from storage.
     */
    private final ArrayList<DetailBackupDTO> details;

    /**
     * Attribute stores the file reader used for reading the unconverted files.
     */
    private final EncryptedFileReader reader;


    /**
     * Constructor instantiates a new converter.
     */
    public StorageConverter() {
        entries = new ArrayList<>();
        details = new ArrayList<>();
        reader = new EncryptedFileReader();
    }


    /**
     * Method converts the data. When the method finishes (without throwing an exception), no previous
     * data is left on the device's storage, the converted data is saved to the storage and the
     * converted data is loaded to the application as if the app started normally.
     *
     * @throws ConverterException   The data could not be converted.
     */
    public void convert() throws ConverterException {
        //Prepare for conversion:
        EntryManager.getInstance().clear();

        //Load data that has not been converted:
        readEntries();
        readDetails();
        assignDetailsToEntries();

        //Save converted data:
        try {
            EntryManager.getInstance().save(true);
        }
        catch (Exception e) {
            throw new ConverterException("Could not save converted data: " + e.getMessage());
        }

        //Clear EntryManager and reload:
        EntryManager.getInstance().clear();
        try {
            EntryManager.getInstance().load();
        }
        catch (Exception e) {
            throw new ConverterException("Could not load converted data: " + e.getMessage());
        }

        //Delete details.csv-file if everything worked:
        File file = new File(App.getContext().getFilesDir(), FILE_DETAILS);
        if (file.exists() && !file.delete()) {
            throw new ConverterException("Could not delete unconverted files");
        }
    }


    /**
     * Method reads all entries from storage.
     *
     * @throws ConverterException   Something went wrong.
     */
    private void readEntries() throws ConverterException {
        String fileContent;
        try {
            fileContent = reader.read(FILE_ENTRIES);
        }
        catch (Exception e) {
            throw new ConverterException("Cannot read file: " + e.getMessage());
        }
        if (fileContent == null) {
            throw new ConverterException("Cannot read file");
        }
        String[] lines = fileContent.split("" + CsvConfiguration.ROW_DIVIDER);
        int corruptedEntries = 0;
        for (String s : lines) {
            EntryAbbreviated entry = new EntryAbbreviated();
            try {
                entry.fromStorable(s);
            }
            catch (Exception e) {
                //Entry corrupt
                corruptedEntries++;
                continue;
            }
            entries.add(entry);
        }
        if (corruptedEntries >= lines.length * 0.5 && lines.length < 4) {
            //More than half of the entries were corrupted:
            throw new ConverterException("A large portion of accounts (" + corruptedEntries + "/" + lines.length + ") was corrupted");
        }
    }

    /**
     * Method reads all details from storage.
     *
     * @throws ConverterException   Something went wrong.
     */
    private void readDetails() throws ConverterException {
        String fileContent;
        try {
            fileContent = reader.read(FILE_DETAILS);
        }
        catch (Exception e) {
            throw new ConverterException("Cannot read file: " + e.getMessage());
        }
        if (fileContent == null) {
            throw new ConverterException("Cannot read file");
        }
        String[] lines = fileContent.split("" + CsvConfiguration.ROW_DIVIDER);
        int corruptedEntries = 0;
        for (String s : lines) {
            DetailBackupDTO detail;
            try {
                detail = new DetailBackupDTO(s);
            }
            catch (Exception e) {
                //Entry corrupt
                corruptedEntries++;
                continue;
            }
            details.add(detail);
        }
        if (corruptedEntries >= lines.length * 0.5 && lines.length < 4) {
            //More than half of the entries were corrupted:
            throw new ConverterException("A large portion of account information (" + corruptedEntries + "/" + lines.length + ") was corrupted");
        }
    }


    /**
     * Method assigns the details to all entries and stores them in the EntryManager.
     */
    private void assignDetailsToEntries() {
        for (EntryAbbreviated abbreviated : entries) {
            EntryExtended extended = new EntryExtended(abbreviated);
            for (DetailBackupDTO detail : details) {
                if (detail.getEntryUuid().equals(extended.getUuid())) {
                    extended.add(detail.toDetail());
                }
            }
            EntryManager.getInstance().add(extended);
        }
    }

}
