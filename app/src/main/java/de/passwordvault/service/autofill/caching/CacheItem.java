package de.passwordvault.service.autofill.caching;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Class models a cache item. Items for each cache type of the autofill service can extend this
 * class. Cache items are of the following format:<br/>
 * ---------<br/>
 * <code>
 *     &lt;identifier&gt;;&lt;content&gt;<br/>
 * </code>
 * ---------<br/>
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public abstract class CacheItem {

    /**
     * Attribute stores the identifier of the cache item.
     */
    private final String identifier;

    /**
     * Attribute stores the content of the cache item.
     */
    private String content;

    /**
     * Attribute stores whether changes were made to the content of the cache item.
     */
    private boolean changesMadeToDataset;

    /**
     * Attribute caches the string representation (generated through {@link #toString()}) of the
     * cache item.
     */
    private String stringCache;


    /**
     * Constructor instantiates a new cache item for the passed identifier and content.
     *
     * @param identifier            Identifier for the cache item.
     * @param content               Content for the cache item.
     * @throws NullPointerException The passed identifier or content is {@code null}.
     */
    public CacheItem(String identifier, String content) throws NullPointerException {
        if (identifier == null || content == null) {
            throw new NullPointerException();
        }
        this.identifier = identifier;
        this.content = content;
        stringCache = null;
        changesMadeToDataset = true;
    }

    /**
     * Constructor instantiates a new cache item from the passed string representation. The passed
     * string representation must be generated through {@link #toString()} beforehand.
     *
     * @param s                     String representation of the cache item.
     * @throws NullPointerException The passed string representation is {@code null.}
     */
    public CacheItem(String s) throws NullPointerException {
        if (s == null) {
            throw new NullPointerException();
        }
        int index = s.indexOf(';');
        if (index == -1) {
            identifier = s;
            content = "";
        }
        else {
            identifier = s.substring(0, index);
            content = s.substring(index + 1);
        }
        stringCache = null;
        changesMadeToDataset = true;
    }


    /**
     * Method returns the identifier of the cache item.
     *
     * @return  Identifier of the cache item.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Method returns the content of the cache item.
     *
     * @return  Content of the cache item.
     */
    public String getContent() {
        return content;
    }


    /**
     * Method tests whether the identifier of the passed cache item is identical to this cache item.
     * If the passed object is no cache item, {@code false} is returned.
     *
     * @param obj   Object to be tested.
     * @return      Whether the identifier ob the cache items are identical.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof CacheItem) {
            CacheItem item = (CacheItem)obj;
            return item.getIdentifier().equals(identifier);
        }
        return false;
    }

    /**
     * Method returns a hash code for the cache item. This hash code is identical to the hash code
     * of it's identifier.
     *
     * @return  Hash code for the cache item.
     */
    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    /**
     * Method converts the cache item into a string representation that can be stored within the cache
     * file. The returned string is of the following format:<br/>
     * <code>
     *     &lt;identifier&gt;;&lt;content&gt;
     * </code>
     *
     * @return  String representation of the cache item.
     */
    @NonNull
    @Override
    public String toString() {
        if (changesMadeToDataset) {
            //Despite the warning of the IDE, I would like to use this StringBuilder to create the
            //string with the correct capacity.
            StringBuilder builder = new StringBuilder(identifier.length() + content.length() + 1);
            builder.append(identifier);
            builder.append(';');
            builder.append(content);
            stringCache = builder.toString();
            changesMadeToDataset = false;
        }
        return stringCache;
    }


    /**
     * Method changes the content of the cache item.
     *
     * @param content               New content.
     * @throws NullPointerException The passed content is {@code null}.
     */
    protected void setContent(String content) throws NullPointerException {
        if (content == null) {
            throw new NullPointerException();
        }
        this.content = content;
        changesMadeToDataset = true;
    }

}
