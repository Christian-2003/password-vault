package de.passwordvault.service.autofill.caching;


/**
 * Class models a cache item for the {@link MappingCache}. Items of the mapping cache are of the
 * following format:<br/>
 * ---------<br/>
 * <code>
 *     &lt;package_name&gt;;&lt;uuid1&gt;,&lt;uuid2&gt;,...<br/>
 * </code>
 * ---------<br/>
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class MappingCacheItem extends CacheItem {

    /**
     * Attribute stores the cached string array of UUIDs of the mapping cache item.
     */
    private String[] uuids;


    /**
     * Constructor instantiates a new cache item for the passed identifier and content.
     *
     * @param identifier            Identifier for the cache item.
     * @param content               Content for the cache item.
     * @throws NullPointerException The passed identifier or content is {@code null}.
     */
    public MappingCacheItem(String identifier, String content) throws NullPointerException {
        super(identifier, content);
        uuids = null;
    }

    /**
     * Constructor instantiates a new cache item from the passed string representation. The passed
     * string representation must be generated through {@link #toString()} beforehand.
     *
     * @param s                     String representation of the cache item.
     * @throws NullPointerException The passed string representation is {@code null.}
     */
    public MappingCacheItem(String s) throws NullPointerException {
        super(s);
        uuids = null;
    }


    /**
     * Method returns a list of UUIDs
     * @return
     */
    public String[] getUuids() {
        if (uuids == null) {
            uuids = getContent().split(",");
        }
        return uuids;
    }

    /**
     * Method adds the passed UUID to the mapping cache item.
     *
     * @param uuid                  UUID to add.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    public void addUuid(String uuid) throws NullPointerException {
        if (uuid == null) {
            throw new NullPointerException();
        }
        getUuids(); //Generate UUIDs if required.
        boolean containsUuid = false;
        for (int i = 0; i < uuids.length; i++) {
            if (uuids[i].equals(uuid)) {
                containsUuid = true;
                break;
            }
        }
        if (containsUuid) {
            return;
        }
        setContent(getContent() + ',' + uuid);
        uuids = null;
    }

    /**
     * Method removes the passed UUID from the mapping cache item.
     *
     * @param uuid                  UUID to remove.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    public void removeUuid(String uuid) throws NullPointerException {
        if (uuid == null) {
            throw new NullPointerException();
        }
        getUuids(); //Generate UUIDs if required.
        int index = -1;
        for (int i = 0; i < uuids.length; i++) {
            if (uuids[i].equals(uuid)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < uuids.length; i++) {
            if (i != index) {
                builder.append(uuids[i]);
                if (i < uuids.length - 1 && (i != uuids.length - 2 || index != uuids.length - 1)) {
                    builder.append(',');
                }
            }
        }
        setContent(builder.toString());
        uuids = null;
    }

}
