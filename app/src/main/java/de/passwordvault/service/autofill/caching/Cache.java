package de.passwordvault.service.autofill.caching;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import de.passwordvault.App;


/**
 * Class models a cache. Specific types of caches must extend this class.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public abstract class Cache {

    /**
     * Attribute stores the cache items that were read from the cache.
     */
    private ArrayList<CacheItem> cacheItems;

    /**
     * Attribute stores the filename of the cache.
     */
    private final String filename;

    /**
     * Attribute stores whether changes were made to the dataset of the cache.
     */
    private boolean changesMadeToDataset;


    /**
     * Constructor instantiates a new cache.
     *
     * @param filename              Filename of the cache.
     * @throws NullPointerException The passed filename is {@code null}.
     */
    public Cache(String filename) throws NullPointerException {
        if (filename == null) {
            throw new NullPointerException();
        }
        this.filename = filename;
        cacheItems = null;
        changesMadeToDataset = false;
        readCache();
    }


    /**
     * Method returns the cache item with the passed identifier from the cache. If no item exists,
     * {@code null} is returned.
     *
     * @param identifier            Identifier of the item to return.
     * @return                      Cache item with the passed identifier.
     * @throws NullPointerException The passed identifier is {@code null}.
     */
    public CacheItem getItem(String identifier) throws NullPointerException {
        if (identifier == null) {
            throw new NullPointerException();
        }
        for (CacheItem item : cacheItems) {
            if (item.getIdentifier().equals(identifier)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Method adds / changes the passed item within the cache.
     *
     * @param item                  Item to be added / changed.
     * @throws NullPointerException The passed item is {@code null}.
     */
    public void putItem(CacheItem item) throws NullPointerException {
        if (item == null) {
            throw new NullPointerException();
        }
        int index = cacheItems.indexOf(item);
        if (index == -1) {
            cacheItems.add(item);
        }
        else {
            cacheItems.set(index, item);
        }
        changesMadeToDataset = true;
    }

    /**
     * Method removes the item with the passed identifier from the cache.
     *
     * @param identifier            Identifier whose item to remove.
     * @throws NullPointerException The passed identifier is {@code null}.
     */
    public void removeItem(String identifier) throws NullPointerException {
        if (identifier == null) {
            throw new NullPointerException();
        }
        for (CacheItem item : cacheItems) {
            if (item.getIdentifier().equals(identifier)) {
                cacheItems.remove(item);
                break;
            }
        }
        changesMadeToDataset = true;
    }

    /**
     * Method removes all items from the cache.
     */
    public void removeAllItems() {
        cacheItems.clear();
        changesMadeToDataset = true;
    }


    /**
     * Method saves the cache to the filesystem. If no changes were made to the cache, nothing
     * happens.
     */
    public void save() {
        if (!changesMadeToDataset) {
            return;
        }
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                return;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (CacheItem item : cacheItems) {
            builder.append(item.toString());
        }

        try (FileOutputStream os = App.getContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
            os.write(builder.toString().getBytes());
        }
        catch (IOException e) {
            //Ignore...
        }
    }


    /**
     * Method returns an instance of a subclass of {@link CacheItem} for the the specified line.
     *
     * @param s Line of the cache for which to generate the cache item.
     * @return  Generated cache item.
     */
    protected abstract CacheItem generateCacheItem(String s);


    /**
     * Method reads the entire cache.
     */
    private void readCache() {
        cacheItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(App.getContext().openFileInput(filename)))) {
            String line = reader.readLine();
            while (line != null && !line.isEmpty()) {
                cacheItems.add(generateCacheItem(line));
            }
        }
        catch (IOException e) {
            //Ignore...
        }
    }

}
