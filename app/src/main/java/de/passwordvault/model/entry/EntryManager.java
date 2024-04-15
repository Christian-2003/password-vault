package de.passwordvault.model.entry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import de.passwordvault.model.CachableManager;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.PersistableManager;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.app.StorageManager;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.service.autofill.caching.InvalidationCache;
import de.passwordvault.service.autofill.caching.InvalidationCacheItem;


/**
 * Class models an entry manager which manages all available entries for the application. The class
 * uses singleton-pattern to assure that only a single instance of this class exists, which can be
 * retrieved through {@link #getInstance()}.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class EntryManager implements CachableManager<EntryExtended>, Observable<ArrayList<EntryAbbreviated>>, PersistableManager {

    /**
     * Field stores the singleton-instance of the entry manager.
     */
    private static EntryManager singleton;

    /**
     * Field stores how many most recently edited entries shall be generated.
     */
    private static final int NUMBER_OF_RECENTLY_EDITED_ENTRIES = 5;


    /**
     * Attribute stores all registered observers.
     */
    private final ArrayList<Observer<ArrayList<EntryAbbreviated>>> observers;

    /**
     * Attribute stores all abbreviated entries that are available within the application.
     */
    private HashMap<String, EntryAbbreviated> abbreviatedEntries;

    /**
     * Attribute stores all extended entries that have been loaded from storage since starting the
     * application.
     */
    private final HashMap<String, EntryExtended> extendedEntryCache;

    /**
     * Attribute stores the cached array list of abbreviated entries as they are requested by
     * observers.
     */
    private final ArrayList<EntryAbbreviated> abbreviatedEntriesArrayListCache;

    /**
     * Attribute stores the cache of the array list of the most recently edited entries.
     */
    private final ArrayList<EntryAbbreviated> mostRecentlyEditedEntriesCache;

    /**
     * Attribute stores the comparator which shall be used to sort the
     * {@link #abbreviatedEntriesArrayListCache}. If no sorting shall be applied, this is
     * {@code null}.
     */
    private GenericComparator<EntryAbbreviated> abbreviatedEntriesArrayListCacheSortingAlgorithm;

    /**
     * Attribute stores the storage manager which is used to save and load data.
     */
    private final StorageManager storageManager;

    /**
     * Attribute stores whether changes were made to the entries that require saving them to
     * persistent storage.
     */
    private boolean changesMade;

    /**
     * Attribute stores whether changes were made to abbreviated entries since the last time the
     * {@link #abbreviatedEntriesArrayListCache} was generated.
     */
    private boolean changesMadeSinceCachedAbbreviatedList;


    /**
     * Constructor instantiates a new entry manager which manages all available entries. Previously
     * stored entries must be loaded manually.
     */
    private EntryManager() {
        observers = new ArrayList<>();
        abbreviatedEntries = new HashMap<>();
        extendedEntryCache = new HashMap<>();
        abbreviatedEntriesArrayListCache = new ArrayList<>();
        mostRecentlyEditedEntriesCache = new ArrayList<>();
        abbreviatedEntriesArrayListCacheSortingAlgorithm = null;
        storageManager = new StorageManager();
        changesMade = true;
        changesMadeSinceCachedAbbreviatedList = true;
    }


    /**
     * Static method returns the singleton-instance of the entry manager.
     *
     * @return  Singleton-instance of the entry manager.
     */
    public static EntryManager getInstance() {
        if (singleton == null) {
            singleton = new EntryManager();
        }
        return singleton;
    }


    /**
     * Method clears all items from the primary storage.
     */
    public void clearCache() {
        extendedEntryCache.clear();
        abbreviatedEntries.clear();
        changesMadeSinceCachedAbbreviatedList = true;
    }

    /**
     * Method returns the abbreviated entry of the passed UUID. If no entry exists, {@code null} is
     * returned.
     *
     * @param uuid  UUID of the abbreviated entry to be returned.
     * @return      Abbreviated entry of the passed UUID.
     */
    public EntryAbbreviated getAbbreviated(String uuid) {
        return abbreviatedEntries.get(uuid);
    }


    /**
     * Method adds the specified item to the managed items.
     *
     * @param item                  Item to be added.
     * @throws NullPointerException The passed item is {@code null}.
     */
    @Override
    public void add(EntryExtended item) throws NullPointerException {
        extendedEntryCache.put(item.getUuid(), item);
        abbreviatedEntries.put(item.getUuid(), new EntryAbbreviated(item));
        changesMade = true;
        changesMadeSinceCachedAbbreviatedList = true;
        notifyObservers();
    }

    /**
     * Method removes all items from the manager AND storage.
     */
    @Override
    public void clear() {
        for (EntryAbbreviated entry : abbreviatedEntries.values()) {
            storageManager.deleteExtendedEntry(entry.getUuid());
        }
        extendedEntryCache.clear();
        abbreviatedEntries.clear();
        try {
            storageManager.saveAbbreviatedEntries(abbreviatedEntries.values());
        }
        catch (EncryptionException e) {
            //Ignore...
        }
        changesMade = true;
        changesMadeSinceCachedAbbreviatedList = true;
        notifyObservers();
    }

    /**
     * Method tests whether the manager contains the passed item.
     *
     * @param item                  Item for which to test whether it is being managed.
     * @return                      Whether the passed item is being managed.
     * @throws NullPointerException The passed item is {@code null}.
     */
    @Override
    public boolean contains(EntryExtended item) throws NullPointerException {
        return contains(item.getUuid());
    }

    /**
     * Method tests whether the manager contains an item with the passed UUID.
     *
     * @param uuid                  UUID for which to test whether an item with this UUID is being
     *                              managed.
     * @return                      Whether an item with the passed UUID is being managed.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    @Override
    public boolean contains(String uuid) throws NullPointerException {
        return abbreviatedEntries.containsKey(uuid);
    }

    /**
     * Method returns the item of the specified UUID. If no item with the specified UUID exists,
     * {@code null} is returned.
     *
     * @param uuid                  UUID of the item to return.
     * @return                      Item of the specified UUID.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    @Override
    public EntryExtended get(String uuid) throws NullPointerException {
        return get(uuid, true);
    }

    /**
     * Method returns the item of the specified UUID. The loaded item is cached when {@code true} is
     * passed as second argument. If {@code false} is passed, the item will not be cached.
     * If no item with the specified UUID exists, {@code null} is returned.
     *
     * @param uuid                  UUID of the item to return.
     * @param cache                 Defines whether the item to be loaded shall be added to a cache.
     * @return                      Item of the specified UUID.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    public EntryExtended get(String uuid, boolean cache) throws NullPointerException {
        if (extendedEntryCache.containsKey(uuid)) {
            return extendedEntryCache.get(uuid);
        }
        EntryExtended entry;
        try {
            EntryAbbreviated abbreviated = abbreviatedEntries.get(uuid);
            if (abbreviated == null) {
                return null;
            }
            entry = storageManager.loadExtendedEntry(abbreviated);
            if (entry == null) {
                return null;
            }
        }
        catch (Exception e) {
            return null;
        }
        if (cache) {
            extendedEntryCache.put(entry.getUuid(), entry);
        }
        return entry;
    }

    /**
     * Method returns whether the manager is not managing any items.
     *
     * @return  Whether the manager is currently empty.
     */
    @Override
    public boolean isEmpty() {
        return abbreviatedEntries.isEmpty();
    }

    /**
     * Method removes the passed item from the managed items.
     *
     * @param item                  Item which shall be removed.
     * @return                      Whether the item was removed.
     * @throws NullPointerException The passed item is {@code null}.
     */
    @Override
    public boolean remove(EntryExtended item) throws NullPointerException {
        EntryAbbreviated removedAbbreviated = abbreviatedEntries.remove(item.getUuid());
        if (removedAbbreviated == null) {
            return false;
        }
        EntryExtended removedExtended = extendedEntryCache.remove(item.getUuid());
        if (removedExtended == null) {
            storageManager.deleteExtendedEntry(item.getUuid());
        }
        changesMade = true;
        changesMadeSinceCachedAbbreviatedList = true;
        notifyObservers();
        return true;
    }

    /**
     * Method removes the item with the passed UUID from the managed items.
     *
     * @param uuid                  UUID of the item to be removed.
     * @return                      The item that was removed or {@code null} if no item could be
     *                              removed.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    @Override
    public EntryExtended remove(String uuid) throws NullPointerException {
        abbreviatedEntries.remove(uuid);
        extendedEntryCache.remove(uuid);
        storageManager.deleteExtendedEntry(uuid);
        changesMade = true;
        changesMadeSinceCachedAbbreviatedList = true;
        notifyObservers();
        return null;
    }

    /**
     * Method replaces the manged item of the specified UUID with the passed item.
     *
     * @param item                          Item with which to replace the managed item.
     * @param uuid                          UUID of the managed item to be replaced.
     * @throws NullPointerException         The passed item or UUID is {@code null}.
     */
    @Override
    public void set(EntryExtended item, String uuid) throws NullPointerException {
        abbreviatedEntries.put(uuid, new EntryAbbreviated(item));
        extendedEntryCache.put(uuid, item);
        changesMade = true;
        changesMadeSinceCachedAbbreviatedList = true;
        notifyObservers();
    }

    /**
     * Method returns the number of items that are being managed.
     *
     * @return  Number of items being managed.
     */
    @Override
    public int size() {
        return abbreviatedEntries.size();
    }


    /**
     * Method registers the passed {@link Observer}. Whenever the relevant data of the implementing
     * class is changed, all Observers which were previously registered through this method are
     * informed about the changed data.
     *
     * @param o                     Observer to be registered.
     * @throws NullPointerException The passed Observer is {@code null}.
     */
    @Override
    public void addObserver(Observer<ArrayList<EntryAbbreviated>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException();
        }
        observers.add(o);
    }

    /**
     * Method returns the data which is being observed. This method must always return the newest
     * data from the implemented instance.
     *
     * @return  Newest data which is being observed.
     */
    @Override
    public ArrayList<EntryAbbreviated> getData() {
        if (changesMadeSinceCachedAbbreviatedList) {
            generateCacheAfterChangesMade();
        }
        return abbreviatedEntriesArrayListCache;
    }

    /**
     * Method notifies all registered {@link Observer}s about a change of the observed data through
     * their {@link Observer#update(Observable)}-method.
     */
    @Override
    public void notifyObservers() {
        for (Observer<ArrayList<EntryAbbreviated>> o : observers) {
            o.update(this);
        }
    }

    /**
     * Method removes the specified {@link Observer} from the implementing class. When the observed
     * data is changed in the future, the removed Observer will not be informed.
     *
     * @param o                     Observer to be removed from the implementing class' observers.
     * @return                      Whether the specified Observer was successfully removed.
     * @throws NullPointerException The passed Observer is {@code null}.
     */
    @Override
    public boolean removeObserver(Observer<ArrayList<EntryAbbreviated>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException();
        }
        return observers.remove(o);
    }


    /**
     * Method loads previously saved items into the manager.
     *
     * @throws StorageException The data could not be loaded.
     */
    @Override
    public void load() throws StorageException {
        try {
            abbreviatedEntries = storageManager.loadAbbreviatedEntries();
            changesMade = true;
            changesMadeSinceCachedAbbreviatedList = true;
        }
        catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
    }

    /**
     * Method saves the managed items into persistent storage.
     *
     * @throws StorageException The data could not be saved.
     */
    @Override
    public void save() throws StorageException {
        save(false);
    }

    /**
     * Method saves the managed items into persistent storage.
     *
     * @param force             Indicates whether the managed items shall be saved, even if no
     *                          changes were made to the managed items.
     * @throws StorageException The data could not be saved.
     */
    @Override
    public void save(boolean force) throws StorageException {
        if (!changesMade && !force) {
            return;
        }
        try {
            storageManager.saveAbbreviatedEntries(abbreviatedEntries.values());
        }
        catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
        for (EntryExtended entry : extendedEntryCache.values()) {
            try {
                storageManager.saveExtendedEntry(entry);
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new StorageException(e.getMessage());
            }
        }
    }


    /**
     * Method returns a collection containing all cached extended entries.
     *
     * @return  Collection containing all extended entries.
     */
    public Collection<EntryExtended> getExtendedEntryCache() {
        return extendedEntryCache.values();
    }

    /**
     * Method returns a list with the most recently edited entries. These entries are determined
     * based on {@link EntryAbbreviated#getChanged()}.
     *
     * @return  List of the most recently edited entries.
     */
    public ArrayList<EntryAbbreviated> getMostRecentlyEditedEntries() {
        if (changesMadeSinceCachedAbbreviatedList) {
            generateCacheAfterChangesMade();
        }
        return mostRecentlyEditedEntriesCache;
    }


    /**
     * Method sorts the array list of entries (retrieved through {@link #getData()}) by their name.
     *
     * @param reverseSorted Whether the data shall be reverse-sorted.
     */
    public void sortByName(boolean reverseSorted) {
        if (abbreviatedEntriesArrayListCacheSortingAlgorithm instanceof LexicographicComparator && ((LexicographicComparator)abbreviatedEntriesArrayListCacheSortingAlgorithm).reverseSorted == reverseSorted) {
            return;
        }
        abbreviatedEntriesArrayListCacheSortingAlgorithm = new LexicographicComparator(reverseSorted);
        changesMadeSinceCachedAbbreviatedList = true;
        notifyObservers();
    }

    /**
     * Method sorts the array list of entries (retrieved through {@link #getData()}) by the date on
     * which they were created.
     *
     * @param reverseSorted Whether the data shall be reverse-sorted.
     */
    public void sortByTime(boolean reverseSorted) {
        if (abbreviatedEntriesArrayListCacheSortingAlgorithm instanceof TimeComparator && ((TimeComparator)abbreviatedEntriesArrayListCacheSortingAlgorithm).reverseSorted == reverseSorted) {

            return;
        }
        abbreviatedEntriesArrayListCacheSortingAlgorithm = new TimeComparator(reverseSorted);
        changesMadeSinceCachedAbbreviatedList = true;
        notifyObservers();
    }

    /**
     * Method removes all sortings from the array list of entries (retrieved through
     * {@link #getData()}).
     */
    public void removeAllSortings() {
        if (abbreviatedEntriesArrayListCacheSortingAlgorithm == null) {
            return;
        }
        abbreviatedEntriesArrayListCacheSortingAlgorithm = null;
        changesMadeSinceCachedAbbreviatedList = true;
        notifyObservers();
    }


    /**
     * Method regenerates all caches after changes were made to the dataset, which is indicated
     * through the flag {@link #changesMadeSinceCachedAbbreviatedList}.
     */
    private void generateCacheAfterChangesMade() {
        generateAbbreviatedEntriesArrayListCache();
        generateMostRecentlyEditedEntriesArrayListCache();
        changesMadeSinceCachedAbbreviatedList = false;
    }

    /**
     * Method generates the cached array list of entries as it is requested by observers.
     */
    private void generateAbbreviatedEntriesArrayListCache() {
        abbreviatedEntriesArrayListCache.clear();
        abbreviatedEntriesArrayListCache.addAll(abbreviatedEntries.values());
        if (abbreviatedEntriesArrayListCacheSortingAlgorithm != null) {
            abbreviatedEntriesArrayListCache.sort(abbreviatedEntriesArrayListCacheSortingAlgorithm);
        }
    }

    /**
     * Method generates the cached array list of the most recently changed entries.
     * <b>Important:</b> Only ever call this method AFTER calling
     * {@link #generateAbbreviatedEntriesArrayListCache()}!
     */
    private void generateMostRecentlyEditedEntriesArrayListCache() {
        if (abbreviatedEntriesArrayListCache.isEmpty()) {
            mostRecentlyEditedEntriesCache.clear();
            return;

        }
        mostRecentlyEditedEntriesCache.clear();
        EntryAbbreviated mostRecentlyChanged = abbreviatedEntriesArrayListCache.get(0);
        for (int i = 0; i < abbreviatedEntriesArrayListCache.size(); i++) {
            if (abbreviatedEntriesArrayListCache.get(i).getChanged().compareTo(mostRecentlyChanged.getChanged()) > 0) {
                mostRecentlyChanged = abbreviatedEntriesArrayListCache.get(i);
            }
        }
        mostRecentlyEditedEntriesCache.add(mostRecentlyChanged);

        for (int i = 0; i < NUMBER_OF_RECENTLY_EDITED_ENTRIES - 1; i++) {
            EntryAbbreviated newMostRecentlyChanged = null;
            for (int j = 0; j < abbreviatedEntriesArrayListCache.size(); j++) {
                if (abbreviatedEntriesArrayListCache.get(j).getChanged().compareTo(mostRecentlyChanged.getChanged()) < 0) {
                    newMostRecentlyChanged = abbreviatedEntriesArrayListCache.get(j);
                    break;
                }
            }
            if (newMostRecentlyChanged == null) {
                break;
            }
            for (int j = 1; j < abbreviatedEntriesArrayListCache.size(); j++) {
                if (abbreviatedEntriesArrayListCache.get(j).getChanged().compareTo(newMostRecentlyChanged.getChanged()) > 0 && abbreviatedEntriesArrayListCache.get(j).getChanged().compareTo(mostRecentlyChanged.getChanged()) < 0) {
                    newMostRecentlyChanged = abbreviatedEntriesArrayListCache.get(j);
                }
            }
            mostRecentlyChanged = newMostRecentlyChanged;
            mostRecentlyEditedEntriesCache.add(mostRecentlyChanged);
        }
    }

}
