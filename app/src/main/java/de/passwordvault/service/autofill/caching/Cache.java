package de.passwordvault.service.autofill.caching;

import android.content.Context;
import android.util.Log;
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
 * @version 3.5.3
 */
public abstract class Cache {

    /**
     * Field stores the tag used for debugging messages.
     */
    protected static final String TAG = "Cache";


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
        ArrayList<CacheItem> items;
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
        Log.d(TAG, "Begin saving cache " + filename);
        if (!changesMadeToDataset) {
            Log.d(TAG, "Cancelled saving cache " + filename + ", since no changes were made");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (CacheItem item : cacheItems) {
            Log.d(TAG, "Saving cache item '" + item.toString() + "'");
            builder.append(item.toString());
            builder.append(System.lineSeparator());
        }

        try (FileOutputStream os = App.getContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
            os.write(builder.toString().getBytes());
        }
        catch (IOException e) {
            Log.w(TAG, "Could not save cache file: " + e.getMessage());
            e.printStackTrace();
            //Ignore...
        }
        Log.d(TAG, "Finished saving cache " + filename);
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
        Log.d(TAG, "Begin reading cache " + filename);

        File file = new File(App.getContext().getFilesDir(), filename);
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (!created) {
                    Log.d(TAG, "Could not create new cache file");
                    return;
                }
            }
            catch (SecurityException | IOException e) {
                Log.w(TAG, "Cannot create cache file and associated parent directories: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            Log.d(TAG, "Created new cache file");
        }
        else {
            Log.d(TAG, "Cache file exists");
        }

        cacheItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(App.getContext().openFileInput(filename)))) {
            String line = reader.readLine();
            while (line != null && !line.isEmpty()) {
                cacheItems.add(generateCacheItem(line));
                Log.d(TAG, "Read cache item '" + line + "'");
                line = reader.readLine();
            }
        }
        catch (IOException e) {
            //Ignore...
            Log.w(TAG, "Error reading cache: " + e.getMessage());
            e.printStackTrace();
        }
        Log.d(TAG, "Finished reading cache " + filename);
    }


    /**
     * Method deletes the file of the cache whose name is passed as argument.
     *
     * @param filename  Name of the cache file to delete.
     * @return          Whether the cache file was deleted successfully.
     */
    protected static boolean deleteCache(String filename) {
        File file = new File(App.getContext().getFilesDir(), filename);
        if (file.exists()) {
            try {
                return file.delete();
            }
            catch (SecurityException e) {
                return false;
            }
        }
        return true;
    }

}
