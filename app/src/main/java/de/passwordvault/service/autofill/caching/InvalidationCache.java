package de.passwordvault.service.autofill.caching;

import java.io.File;
import de.passwordvault.App;


/**
 * Class models the invalidation cache which stores the UUIDs of all instances of
 * {@link de.passwordvault.model.entry.EntryExtended} that are no longer up to date (e.g. due to
 * changes made by the user). The invalidation cache is of the following format:<br/>
 * ---------<br/>
 * <code>
 *     &lt;uuid1&gt;;<br/>
 *     &lt;uuid2&gt;;<br/>
 *     &lt;uuid3&gt;;<br/>
 *     ...<br/>
 * </code>
 * ---------<br/>
 *
 * @author  Christian-2003
 * @version 3.5.3
 */
public class InvalidationCache extends Cache {

    /**
     * Field stores the singleton-instance of the invalidation cache.
     */
    private static InvalidationCache singleton;

    /**
     * Field stores the name of the cache.
     */
    private static final String CACHE_NAME = "autofill/invalidation.cache";


    /**
     * Constructor instantiates a new invalidation cache.
     */
    private InvalidationCache() {
        super(CACHE_NAME);
    }


    /**
     * Method returns the singleton-instance of the invalidation cache.
     *
     * @return  Singleton-instance.
     */
    public static InvalidationCache getInstance() {
        if (singleton == null) {
            singleton = new InvalidationCache();
        }
        return singleton;
    }

    /**
     * Method returns whether the invalidation cache is loaded.
     *
     * @return  Whether the invalidation cache is loaded.
     */
    public static boolean isLoaded() {
        return singleton != null;
    }


    /**
     * Method returns an instance of {@link InvalidationCacheItem} for the the specified line.
     *
     * @param s Line of the cache for which to generate the cache item.
     * @return  Generated cache item.
     */
    @Override
    protected CacheItem generateCacheItem(String s) {
        return new InvalidationCacheItem(s);
    }


    /**
     * Method permanently deletes the cache.
     *
     * @return  Whether the cache was deleted successfully.
     */
    public static boolean deleteCache() {
        return deleteCache(CACHE_NAME);
    }

}
