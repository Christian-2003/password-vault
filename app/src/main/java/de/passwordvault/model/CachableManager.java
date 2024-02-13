package de.passwordvault.model;


/**
 * Interface contains methods that allow the manipulation of items that are being managed by a
 * manager. Furthermore, all methods retrieving items from the manager must provide an option to
 * specify whether the retrieved data shall be cached within the manager.
 *
 * @param <T>   Type of the items being managed.
 * @author      Christian-2003
 * @version     3.3.0
 */
public interface CachableManager<T extends Identifiable> extends Manager<T> {

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
    T get(String uuid, boolean cache) throws NullPointerException;

}
