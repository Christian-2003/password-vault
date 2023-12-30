package de.passwordvault.model.entry;

import android.util.Log;
import java.util.ArrayList;
import de.passwordvault.model.storage.app.StorageManager;


/**
 * Class models a handle which can manage {@link Entry} instances. The class uses a singleton
 * pattern which means that an instance of this class can only be accessed through
 * {@link #getInstance()}.
 *
 * @author  Christian-2003
 * @version 3.1.0
 */
public class EntryHandle extends EntryHandleObservable {

    /**
     * Static instance of this class required for singleton pattern.
     */
    private static EntryHandle singleton;


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
     * Constructor constructs a new EntryHandle. This constructor is private to prevent the
     * instantiation of new instances, which would violate the singleton pattern.
     */
    private EntryHandle() {
        entries = new ArrayList<>();
        initialEntries = new ArrayList<>();
        StorageManager manager = new StorageManager();
        Log.d("EntryHandle", "Begin loading");
        ArrayList<Entry> entries = manager.load();
        if (entries != null) {
            this.entries.addAll(entries);
            initialEntries.addAll(entries);
        }
        Log.d("EntryHandle", "Finished loading");
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
        notifyObservers();
        return true;
    }


    /**
     * Method returns the data which is being observed. This method must always return the newest
     * data from the implemented instance.
     *
     * @return  Newest data which is being observed.
     */
    public ArrayList<Entry> getData() {
        return entries;
    }


    /**
     * Method returns the entries that are being handled by this instance. The returned entries
     * will not be sorted.
     *
     * @return  Handled entries.
     */
    public ArrayList<Entry> getEntries() {
        return entries;
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
     * Method returns a singleton instance of this {@link EntryHandle}.
     *
     * @return  Singleton-instance of this EntryHandle.
     */
    public static EntryHandle getInstance() {
        if (singleton == null) {
            singleton = new EntryHandle();
        }
        return singleton;
    }


    /**
     * Method removes all sortings from the handled entries.
     */
    public void removeAllSortings() {
        entries.clear();
        entries.addAll(initialEntries);
        notifyObservers();
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
        notifyObservers();
        return true;
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
        notifyObservers();
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
        notifyObservers();
    }


    /**
     * Method removes the {@link Entry} with the specified UUID permanently. This can only be undone
     * when discarding all changes and not calling {@link StorageManager#save()}.
     *
     * @param uuid                  UUID of the entry to remove.
     * @return                      Whether any entry was removed.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    public boolean remove(String uuid) throws NullPointerException {
        if (uuid == null) {
            throw new NullPointerException("Null is invalid UUID");
        }
        if (!initialEntries.contains(Entry.getInstance(uuid))) {
            //Entry does not exist:
            return false;
        }
        initialEntries.remove(Entry.getInstance(uuid));
        entries.remove(Entry.getInstance(uuid));
        notifyObservers();
        return true;
    }


    /**
     * Method replaces all handled entries with the passed list of entries.
     * This is meant to restore a backup.
     *
     * @param entries   New entries with which to replace the handled entries.
     */
    public void replaceAll(ArrayList<Entry> entries) {
        initialEntries.clear();
        this.entries.clear();
        initialEntries.addAll(entries);
        this.entries.addAll(initialEntries);
        notifyObservers();
    }

}
