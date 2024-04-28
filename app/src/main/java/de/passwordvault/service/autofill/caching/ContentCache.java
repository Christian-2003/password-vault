package de.passwordvault.service.autofill.caching;


/**
 * Class models the content cache which stores the username and password for an instance of
 * {@link de.passwordvault.model.entry.EntryExtended}. The content cache is of the following format:<br/>
 * ---------<br/>
 * <code>
 *     &lt;uuid1&gt;;&lt;username1&gt;,&lt;password1&gt;<br/>
 *     &lt;uuid2&gt;;&lt;username2&gt;,&lt;password2&gt;<br/>
 *     &lt;uuid3&gt;;&lt;username3&gt;,&lt;password3&gt;<br/>
 *     ...<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;^^^^^^^^^^^^^^^^^^^^^^^ <- encrypted<br/>
 * </code>
 * ---------<br/>
 *
 * @author  Christian-2003
 * @version 3.5.3
 */
public class ContentCache extends Cache {

    /**
     * Field stores the singleton-instance of the content cache.
     */
    private static ContentCache singleton;

    /**
     * Field stores the name of the cache.
     */
    private static final String CACHE_NAME = "autofill_content.cache";


    /**
     * Constructor instantiates a new content cache.
     */
    private ContentCache() {
        super(CACHE_NAME);
    }


    /**
     * Method returns the singleton-instance of the content cache.
     *
     * @return  Singleton-instance.
     */
    public static ContentCache getInstance() {
        if (singleton == null) {
            singleton = new ContentCache();
        }
        return singleton;
    }


    /**
     * Method returns an instance of {@link ContentCacheItem} for the the specified line.
     *
     * @param s Line of the cache for which to generate the cache item.
     * @return  Generated cache item.
     */
    @Override
    protected CacheItem generateCacheItem(String s) {
        return new ContentCacheItem(s);
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
