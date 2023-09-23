package de.passwordvault.backend.entry;

import android.content.Context;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class models a handle which can manage entries. The class provides two types of entries:
 * <ul>
 *     <li>{@linkplain AbbreviatedEntry} instances contain only the most basic information about an
 *     entry. These are loaded into working memory whenever an instance of this class is created.</li>
 *     <li>{@linkplain Entry} instances contain all details about an entry. These are loaded into
 *     working memory on demand in order to speed up the startup time for the application.</li>
 * </ul>
 * These behaviours are implemented automatically and the user of this class does not need to do
 * anything.
 *
 * @author  Christian-2003
 * @version 1.0.0
 */
public class EntryHandle implements Serializable {

    /**
     * Constant stores a suffix for the files which store all relevant data.
     */
    private static final transient String FILE_NAME_SUFFIX = ".json";

    /**
     * Constant stores a prefix for the files which store all relevant data.s
     */
    private static final transient String FILE_NAME_PREFIX = "entry";

    /**
     * Constant stores the filename for the file which shall contain the abbreviated entries.
     */
    private static final transient String FILE_NAME_ABBREVIATES = "abbreviates";


    /**
     * Attribute stores the context if the EntryHandle. This is needed for file handling.
     */
    private final Context context;

    /**
     * Attribute stores {@linkplain Gson} instance to convert instances into JSON and vice versa.
     */
    private final Gson gson;

    /**
     * Attribute stores all {@linkplain AbbreviatedEntry} instances.
     */
    private final ArrayList<AbbreviatedEntry> abbreviatedEntries;

    /**
     * Attribute stores all {@linkplain Entry} instances that were previously loaded into memory.
     * The key of this HashMap will be the entries UUID.
     * A value of {@code null} indicates that the corresponding file will be removed when everything
     * is saved.
     */
    private final HashMap<String, Entry> entries;



    /**
     * Constructor constructs a new EntryHandle.
     */
    public EntryHandle(Context context) {
        this.context = context;
        abbreviatedEntries = new ArrayList<>();
        entries = new HashMap<>();
        gson = new Gson();
        loadAbbreviatedEntries();
    }


    public ArrayList<AbbreviatedEntry> getAbbreviatedEntries() {
        return abbreviatedEntries;
    }


    /**
     * Method returns all {@linkplain AbbreviatedEntry} instances that are visible (i.e. the method
     * {@linkplain AbbreviatedEntry#isVisible()} returns {@code true} as an {@linkplain ArrayList}.
     *
     * @return  List of visible AbbreviatedEntries.
     */
    public ArrayList<AbbreviatedEntry> getVisibleAbbreviatedEntries() {
        ArrayList<AbbreviatedEntry> visibleEntries = new ArrayList<>();
        for (AbbreviatedEntry entry : abbreviatedEntries) {
            if (entry.isVisible()) {
                visibleEntries.add(entry);
            }
        }
        return visibleEntries;
    }


    /**
     * Method returns all {@linkplain AbbreviatedEntry} instances that are invisible (i.e. the method
     * {@linkplain AbbreviatedEntry#isVisible()} returns {@code false} as an {@linkplain ArrayList}.
     *
     * @return  List of invisible AbbreviatedEntries.
     */
    public ArrayList<AbbreviatedEntry> getInvisibleAbbreviatedEntries() {
        ArrayList<AbbreviatedEntry> invisibleEntries = new ArrayList<>();
        for (AbbreviatedEntry entry : abbreviatedEntries) {
            if (!entry.isVisible()) {
                invisibleEntries.add(entry);
            }
        }
        return invisibleEntries;
    }


    /**
     * Method adds the specified entry to the handled entries. If an entry with the same UUID already
     * exists, nothing happens and {@code false} is returned. If the passed entry was successfully
     * added, {@code true} is returned.
     *
     * @param entry Entry to be added to the handled entries.
     * @return      Whether the entry was added successfully.
     */
    public boolean add(Entry entry) {
        if (containsUuid(entry.getUuid()) != -1) {
            //Entry does already exist:
            return false;
        }
        abbreviatedEntries.add(entry);
        entries.put(entry.getUuid(), entry);
        return true;
    }

    /**
     * Method removes the entry with the specified UUID permanently. This can only be undone when
     * discarding all changes and not calling {@link #save()}.
     *
     * @param uuid  UUID of the entry to remove.
     * @return      Whether any entry was removed.
     */
    public boolean remove(String uuid) {
        int index = containsUuid(uuid);
        if (index == -1) {
            //Entry does not exist:
            return false;
        }
        abbreviatedEntries.remove(index);
        entries.put(uuid, null); //Mark for removal.
        return true;
    }

    /**
     * Method replaces the entry (that has the same UUID as the passed entry) with the passed entry.
     * If no entry with the passed UUID exists, it is added instead. If the passed entry was
     * successfully added to the handled entries {@code true} is returned. Otherwise {@code false}
     * will be returned.
     *
     * @param entry Entry which shall replace an entry of the same UUID.
     * @return      Whether the entry was successfully added / replaced.
     */
    public boolean set(Entry entry) {
        int index = containsUuid(entry.getUuid());
        if (index == -1) {
            //Entry does not already exist:
            return add(entry);
        }
        abbreviatedEntries.set(index, entry);
        entries.put(entry.getUuid(), entry);
        return true;
    }

    /**
     * Method returns the {@linkplain AbbreviatedEntry} of the specified UUID. If no entry of the
     * passed UUID exists, {@code null} is returned.
     *
     * @param uuid  UUID whose AbbreviatedEntry shall be returned.
     * @return      AbbreviatedEntry of the specified UUID.
     */
    public AbbreviatedEntry getAbbreviatedEntry(String uuid) {
        int index = containsUuid(uuid);
        if (index == -1) {
            return null;
        }
        return abbreviatedEntries.get(index);
    }

    /**
     * Method returns the entry of the specified UUID. If necessary, the entry is loaded into memory
     * from persistent memory beforehand. If no entry with the specified UUID exists, {@code null}
     * is returned.
     *
     * @param uuid  UUID whose entry shall be returned.
     * @return      Entry with the specified UUID.
     */
    public Entry getEntry(String uuid) {
        if (containsUuid(uuid) == -1) {
            //Entry does not exist:
            return null;
        }
        if (entries.containsKey(uuid)) {
            return entries.get(uuid);
        }
        //Load from persistent memory:
        String json = readFromFile(generatePersistentFileName(uuid));
        if (json == null) {
            return null;
        }
        Entry entry = gson.fromJson(json, Entry.class);
        entries.put(entry.getUuid(), entry);
        return entry;
    }


    /**
     * Method saves all the entries that are currently being managed by this handle to the persistent
     * memory.
     *
     * @return  Whether everything was saved successfully.
     */
    public boolean save() {
        boolean savedEverything = true;
        //Save abbreviated entries:
        String json = gson.toJson(abbreviatedEntries, new TypeToken<ArrayList<AbbreviatedEntry>>(){}.getType());
        boolean result = writeToFile(generatePersistentFileName(FILE_NAME_ABBREVIATES), json);
        if (!result) {
            savedEverything = false;
        }
        //Save extended entries:
        for (String uuid : entries.keySet()) {
            Entry entry = entries.get(uuid);
            if (entry == null) {
                //Marked for removal:
                File file = new File(context.getFilesDir(), generatePersistentFileName(uuid));
                file.delete();
                continue;
            }
            json = gson.toJson(entry, Entry.class);
            result = writeToFile(generatePersistentFileName(entry.getUuid()), json);
            if (!result) {
                savedEverything = false;
            }
        }
        return savedEverything;
    }


    /**
     * Method returns the UUID of the {@linkplain AbbreviatedEntry} at the specified index within
     * the {@linkplain ArrayList} that is returned by {@linkplain #getAbbreviatedEntries()}. If the
     * passed index is out of bounds, {@code -1} is returned.
     * This method is needed since ListViews in Android only return the index of a clicked item.
     * Therefore, this method allows an Entry to be identified through this index.
     *
     * @param index Index of the Entry within the described ArrayList.
     * @return      UUID of the entry at the specified index.
     */
    public String getUuidFromIndex(int index) {
        if (index >= 0 && index < abbreviatedEntries.size()) {
            return abbreviatedEntries.get(index).getUuid();
        }
        return null;
    }


    /**
     * Method loads all abbreviated entries from persistent memory and stores them within
     * {@linkplain #abbreviatedEntries}.
     */
    private void loadAbbreviatedEntries() {
        String json = readFromFile(generatePersistentFileName(FILE_NAME_ABBREVIATES));
        if (json != null) {
            abbreviatedEntries.addAll(gson.fromJson(json, new TypeToken<ArrayList<AbbreviatedEntry>>(){}.getType()));
        }
    }


    /**
     * Method tests whether an entry with the specified UUID exists in the list of entries. If no
     * entry with the specified UUID exists, {@code -1} is returned.
     *
     * @param uuid  UUID to be tested.
     * @return      Index of the entry with the specified UUID.
     */
    private int containsUuid(String uuid) {
        for (int i = 0; i < abbreviatedEntries.size(); i++) {
            if (abbreviatedEntries.get(i).getUuid().equals(uuid)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Method generates the filename for the file which stores the extended entry of the specified
     * UUID.
     *
     * @param uuid  UUID of the entry whose filename shall be generated.
     * @return      Path to the file that contains the extended entry.
     */
    private String generatePersistentFileName(String uuid) {
        return FILE_NAME_PREFIX + "_" + uuid + FILE_NAME_SUFFIX;
    }


    /**
     * Method writes the specified content to the specified file. If the file does not already
     * exist, it is created. Otherwise, the content of the original file are replaced with the new
     * content.
     *
     * @param filename  Path of the file to which the content shall be written.
     * @param content   Content which shall be written to the file.
     * @return          Whether the file was successfully written or not.
     */
    private boolean writeToFile(String filename, String content) {
        File file = new File(context.getFilesDir(), filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                //Could not create new file:
                return false;
            }
        }
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
        }
        catch (IOException e) {
            //Could not write to file:
            return false;
        }
        return true;
    }


    /**
     * Method reads the contents of the specified file and returns them as String. If the specified
     * file does not exist, {@code null} is returned.
     *
     * @param filename  Path of the file to be read.
     * @return          Content of the file.
     */
    @Nullable
    private String readFromFile(String filename) {
        File file = new File(context.getFilesDir(), filename);
        if (!file.exists()) {
            //File does not exist:
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(filename)))) {
            StringBuilder json = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                json.append(line);
                line = reader.readLine();
            }
            return json.toString();
        }
        catch (IOException e) {
            //Could not read the file:
            return null;
        }
    }

}
