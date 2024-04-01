package de.passwordvault.service.autofill.caching;


/**
 * Class models a cache item for the {@link InvalidationCache}. Items of the invalidation cache are
 * of the following format:<br/>
 * ---------<br/>
 * <code>
 *     &lt;uuid&gt;;<br/>
 * </code>
 * ---------<br/>
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class InvalidationCacheItem extends CacheItem {

    /**
     * Constructor instantiates a new cache item for the passed identifier and content.
     *
     * @param identifier            Identifier for the cache item.
     * @param content               Content for the cache item.
     * @throws NullPointerException The passed identifier or content is {@code null}.
     */
    public InvalidationCacheItem(String identifier, String content) throws NullPointerException {
        super(identifier, content);
    }

    /**
     * Constructor instantiates a new cache item from the passed string representation. The passed
     * string representation must be generated through {@link #toString()} beforehand.
     *
     * @param s                     String representation of the cache item.
     * @throws NullPointerException The passed string representation is {@code null.}
     */
    public InvalidationCacheItem(String s) throws NullPointerException {
        super(s);
    }

}
