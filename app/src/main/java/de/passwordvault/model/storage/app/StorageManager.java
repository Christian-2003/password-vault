package de.passwordvault.model.storage.app;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import de.passwordvault.App;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.storage.csv.CsvConfiguration;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.model.storage.file.EncryptedFileReader;
import de.passwordvault.model.storage.file.EncryptedFileWriter;


/**
 * Class implements the storage manager (v2) that is being used since version 3.3.0 to store
 * the managed entries. All data that is stored through this manager is being encrypted.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class StorageManager {

    /**
     * Field stores the name of the file in which the list of abbreviated entries is stored.
     */
    private static final String ABBREVIATED_ENTRIES_FILE = "entries_list.csv";

    /**
     * Field stores the generic name of the file in which extended entries are stored. To get the
     * 'real' name for a file, replace "{id}" within this string with the ID of the entry.
     */
    private static final String EXTENDED_ENTRIES_FILE = "entry_{id}.csv";


    /**
     * Attribute stores the writer that can write to encrypted storage.
     */
    private final EncryptedFileWriter fileWriter;

    /**
     * Attribute stores the reader that can read from encrypted storage.
     */
    private final EncryptedFileReader fileReader;


    /**
     * Constructor instantiates a new storage manager.
     */
    public StorageManager() {
        fileWriter = new EncryptedFileWriter();
        fileReader = new EncryptedFileReader();
    }


    /**
     * Method loads all abbreviated entries from storage and returns them as hashmap.
     *
     * @return                      Hash map of all abbreviated entries.
     * @throws EncryptionException  The data could be decrypted.
     */
    public HashMap<String, EntryAbbreviated> loadAbbreviatedEntries() throws EncryptionException {
        String fileContent = fileReader.read(ABBREVIATED_ENTRIES_FILE);
        String[] lines = fileContent.split("\n");
        HashMap<String, EntryAbbreviated> entries = new HashMap<>();
        for (String s : lines) {
            if (s.isEmpty()) {
                continue;
            }
            EntryAbbreviated entry = new EntryAbbreviated();
            try {
                entry.fromStorable(s);
            }
            catch (StorageException e) {
                //Line is corrupt...
                continue;
            }
            if (existsExtendedEntry(entry)) {
                entries.put(entry.getUuid(), entry);
            }
        }
        return entries;
    }


    /**
     * Method saves the passed collection of abbreviated entries to persistent storage.
     *
     * @param entries               Collection of entries to be saved.
     * @throws NullPointerException The passed collection is {@code null}.
     * @throws EncryptionException  The data could not be encrypted.
     */
    public void saveAbbreviatedEntries(Collection<EntryAbbreviated> entries) throws NullPointerException, EncryptionException {
        if (entries == null) {
            throw new NullPointerException();
        }
        StringBuilder builder = new StringBuilder();
        for (EntryAbbreviated entry : entries) {
            builder.append(entry.toStorable());
            builder.append(CsvConfiguration.ROW_DIVIDER);
        }
        fileWriter.write(ABBREVIATED_ENTRIES_FILE, builder.toString());
    }


    /**
     * Method loads the extended entry of the specified abbreviated version. If no extended version
     * exists, {@code null} is returned.
     *
     * @param abbreviated           Abbreviated entry whose extended version shall be loaded.
     * @return                      Extended entry loaded from persistent storage.
     * @throws NullPointerException The passed UUID is {@code null}.
     * @throws EncryptionException  The entry could not be decrypted.
     * @throws StorageException     The decrypted file could not be converted into an entry.
     */
    public EntryExtended loadExtendedEntry(EntryAbbreviated abbreviated) throws NullPointerException, EncryptionException, StorageException {
        if (abbreviated.getUuid() == null) {
            throw new NullPointerException();
        }
        String fileName = getFileName(abbreviated.getUuid());
        File file = new File(App.getContext().getFilesDir(), fileName);
        if (!file.exists()) {
            return null;
        }
        try {
            String content = fileReader.read(fileName);
            EntryExtended created = new EntryExtended(abbreviated);
            created.fromStorable(content);
            return created;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * Method saves the extended entry to permanent storage. Only the extended parts (i.e. the details)
     * of the extended entry is saved!
     *
     * @param entry                 Extended entry to be saved.
     * @throws NullPointerException The passed entry is {@code null}.
     * @throws EncryptionException  The entry could not be encrypted.
     */
    public void saveExtendedEntry(EntryExtended entry) throws NullPointerException, EncryptionException {
        if (entry == null) {
            throw new NullPointerException();
        }
        fileWriter.write(getFileName(entry.getUuid()), entry.toStorable());
        File file = new File(App.getContext().getFilesDir(), getFileName(entry.getUuid()));
    }


    /**
     * Method deletes the extended entry of the passed UUID from the filesystem.
     *
     * @param uuid                  UUID of the extended entry whose file to delete.
     * @return                      Whether the file was deleted successfully.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    public boolean deleteExtendedEntry(String uuid) throws NullPointerException {
        File file = new File(App.getContext().getFilesDir(), getFileName(uuid));
        return file.delete();
    }


    /**
     * Method tests whether an extended entry exists for the specified abbreviated entry.
     *
     * @param entry                 Abbreviated entry for which to test whether a corresponding
     *                              extended entry exists.
     * @return                      Whether the corresponding extended entry exists.
     * @throws NullPointerException The passed entry is {@code null}.
     */
    public boolean existsExtendedEntry(EntryAbbreviated entry) throws NullPointerException {
        if (entry == null) {
            throw new NullPointerException();
        }
        File file = new File(App.getContext().getFilesDir(), getFileName(entry.getUuid()));
        try {
            return file.exists();
        }
        catch (SecurityException e) {
            return false;
        }
    }


    /**
     * Method returns the name of the file in which the extended entry of the passed UUID is stored.
     *
     * @param uuid                  UUID of the extended entry whose file name should be returned.
     * @return                      Name of the file in which the specified extended entry is stored.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    private String getFileName(String uuid) throws NullPointerException {
        if (uuid == null) {
            throw new NullPointerException();
        }
        return EXTENDED_ENTRIES_FILE.replace("{id}", uuid);
    }

}
