package de.passwordvault.backend.entry;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.passwordvault.backend.security.file.EncryptedFileReader;
import de.passwordvault.backend.security.file.EncryptedFileWriter;


/**
 * Class models a handle which can manage {@link Entry} instances.
 *
 * @author  Christian-2003
 * @version 2.2.1
 */
public class EntryHandle {

    /**
     * Constant stores the name of the file in which the entries shall be stored.
     */
    private static final transient String FILE_NAME_ENTRIES = "entries.json";


    /**
     * Attribute stores the context if the EntryHandle. This is needed for file handling.
     */
    private final Context context;

    /**
     * Attribute stores the {@linkplain Gson} instance to convert instances into JSON and vice versa.
     */
    private final Gson gson;

    /**
     * Attribute stores all {@link Entry}-instances that are passed to the frontend. These might be
     * sorted.
     */
    private final ArrayList<Entry> entries;

    /**
     * Attribute stores all {@link Entry}-instances in the order in which they were loaded from memory.
     */
    private final ArrayList<Entry> initialEntries;


    /**
     * Constructor constructs a new EntryHandle.
     */
    public EntryHandle(Context context) {
        this.context = context;
        entries = new ArrayList<>();
        initialEntries = new ArrayList<>();
        gson = new Gson();
        load();
    }


    public ArrayList<Entry> getEntries() {
        return entries;
    }

    /**
     * Method sorts all entries lexicographically according to their name.
     *
     * @param reverseSorted Defines whether the entries shall be reverse-sorted.
     * @see                 LexicographicComparator
     * @see                 LexicographicComparator#compare(Entry, Entry)
     */
    public void sortByName(boolean reverseSorted) {
        entries.sort(new LexicographicComparator(reverseSorted));
    }

    /**
     * Method sorts all entries lexicographically according to the date on which they were created.
     *
     * @param reverseSorted Defines whether the entries shall be reverse-sorted.
     * @see                 TimeComparator
     * @see                 TimeComparator#compare(Entry, Entry)
     */
    public void sortByTime(boolean reverseSorted) {
        entries.sort(new TimeComparator(reverseSorted));
    }


    /**
     * Method removes all sortings from the handled entries.
     */
    public void removeAllSortings() {
        entries.clear();
        entries.addAll(initialEntries);
    }


    /**
     * Method adds the specified {@link Entry} to the handled {@link #entries}. If an entry with the
     * same UUID already exists, nothing happens and {@code false} is returned. If the passed entry
     * was successfully added, {@code true} is returned.
     *
     * @param entry                 Entry to be added to the handled entries.
     * @return                      Whether the entry was added successfully.
     * @throws NullPointerException The passed entry is {@code null}.
     */
    public boolean add(Entry entry) {
        if (entry == null) {
            throw new NullPointerException("Null is invalid entry");
        }
        if (initialEntries.contains(entry)) {
            //Entry does already exist:
            return false;
        }
        initialEntries.add(entry);
        entries.add(entry);
        return true;
    }

    /**
     * Method removes the {@link Entry} with the specified UUID permanently. This can only be undone
     * when discarding all changes and not calling {@link #save()}.
     *
     * @param uuid                  UUID of the entry to remove.
     * @return                      Whether any entry was removed.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    public boolean remove(String uuid) {
        if (uuid == null) {
            throw new NullPointerException("Null is invalid UUID");
        }
        if (!initialEntries.contains(Entry.getInstance(uuid))) {
            //Entry does not exist:
            return false;
        }
        initialEntries.remove(Entry.getInstance(uuid));
        entries.remove(Entry.getInstance(uuid));
        return true;
    }

    /**
     * Method replaces the {@link Entry} (that has the same UUID as the passed entry) with the passed
     * entry. If no entry with the passed UUID exists, it is added instead. If the passed entry was
     * successfully added to the handled entries, {@code true} is returned. Otherwise {@code false}
     * will be returned.
     *
     * @param entry                 Entry which shall replace an entry of the same UUID.
     * @return                      Whether the entry was successfully added / replaced.
     * @throws NullPointerException The passed entry is {@code null}.
     */
    public boolean set(Entry entry) {
        if (entry == null) {
            throw new NullPointerException("Null is invalid entry");
        }
        if (!initialEntries.contains(entry)) {
            //Entry does not already exist:
            return add(entry);
        }
        initialEntries.set(initialEntries.indexOf(entry), entry);
        entries.set(entries.indexOf(entry), entry);
        return true;
    }

    /**
     * Method returns the {@link Entry} of the specified UUID. If no entry of the
     * passed UUID exists, {@code null} is returned.
     *
     * @param uuid                  UUID whose Entry shall be returned.
     * @return                      Entry of the specified UUID.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    public Entry getEntry(String uuid) {
        if (uuid == null) {
            throw new NullPointerException("Null is invalid UUID");
        }
        int index = entries.indexOf(Entry.getInstance(uuid));
        if (index == -1) {
            return null;
        }
        return entries.get(index);
    }


    /**
     * Method saves all the {@link Entry} instances that are currently being managed by this handle
     * to the persistent memory.
     *
     * @return  Whether everything was saved successfully.
     */
    public boolean save() {
        boolean savedEverything = true;
        //Save abbreviated entries:
        String json = gson.toJson(initialEntries, new TypeToken<ArrayList<Entry>>(){}.getType());
        boolean result = writeToFile(FILE_NAME_ENTRIES, json);
        if (!result) {
            savedEverything = false;
        }
        return savedEverything;
    }


    /**
     * Method returns the UUID of the {@link Entry} at the specified index within
     * the {@linkplain ArrayList} that is returned by {@link #getEntries()}. If the
     * passed index is out of bounds, {@code -1} is returned.
     * This method is needed since ListViews in Android only return the index of a clicked item.
     * Therefore, this method allows an Entry to be identified through this index.
     *
     * @param index Index of the Entry within the described ArrayList.
     * @return      UUID of the entry at the specified index.
     */
    public String getUuidFromIndex(int index) {
        if (index >= 0 && index < entries.size()) {
            return entries.get(index).getUuid();
        }
        return null;
    }


    /**
     * Method loads all {@link Entry} instances from persistent memory and stores them within
     * {@link #entries}.
     */
    private void load() {
        String json = readFromFile(FILE_NAME_ENTRIES);
        if (json != null) {
            try {
                initialEntries.clear();
                entries.clear();
                initialEntries.addAll(gson.fromJson(json, new TypeToken<ArrayList<Entry>>() {
                }.getType()));
                entries.addAll(initialEntries);
            }
            catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
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
        EncryptedFileWriter writer = new EncryptedFileWriter(context);
        try {
            return writer.write(filename, content);
        }
        catch (Exception e) {
            return false;
        }
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
        EncryptedFileReader reader = new EncryptedFileReader(context);
        try {
            return reader.read(filename);
        }
        catch (Exception e) {
            return null;
        }
    }

}
