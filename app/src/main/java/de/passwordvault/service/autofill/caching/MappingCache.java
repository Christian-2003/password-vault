package de.passwordvault.service.autofill.caching;


/**
 * Class models the mapping cache which stores the mapping for package names (for any installed app)
 * and a UUID of an instance of {@link de.passwordvault.model.entry.EntryExtended}. The mapping
 * cache is of the following format:<br/>
 * ---------<br/>
 * <code>
 *     &lt;package_name1&gt;;&lt;uuid11&gt;,&lt;uuid12&gt;,...<br/>
 *     &lt;package_name2&gt;;&lt;uuid21&gt;,&lt;uuid22&gt;,...<br/>
 *     &lt;package_name3&gt;;&lt;uuid31&gt;,&lt;uuid32&gt;,...<br/>
 *     ...<br/>
 * </code>
 * ---------<br/>
 *
 * @author  Christian-2003
 * @version 3.5.3
 */
public class MappingCache extends Cache {

    /**
     * Field stores the singleton-instance of the mapping cache.
     */
    private static MappingCache singleton;

    /**
     * Field stores the name of the cache.
     */
    private static final String CACHE_NAME = "autofill_mapping.cache";


    /**
     * Constructor instantiates a new mapping cache.
     */
    private MappingCache() {
        super("autofill_mapping.cache");
    }


    /**
     * Method returns the singleton-instance of the mapping cache.
     *
     * @return  Singleton-instance.
     */
    public static MappingCache getInstance() {
        if (singleton == null) {
            singleton = new MappingCache();
        }
        return singleton;
    }


    /**
     * Method returns an instance of {@link MappingCacheItem} for the the specified line.
     *
     * @param s Line of the cache for which to generate the cache item.
     * @return  Generated cache item.
     */
    @Override
    protected CacheItem generateCacheItem(String s) {
        return new MappingCacheItem(s);
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
