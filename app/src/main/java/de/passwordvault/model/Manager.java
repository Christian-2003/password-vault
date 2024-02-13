package de.passwordvault.model;


/**
 * Interface contains methods that allow the manipulation of items that are being managed by a
 * manager.
 *
 * @param <T>   Type of the items being managed.
 * @author      Christian-2003
 * @version     3.3.0
 */
public interface Manager<T extends Identifiable> {

    /**
     * Method adds the specified item to the managed items.
     *
     * @param item                  Item to be added.
     * @throws NullPointerException The passed item is {@code null}.
     */
    void add(T item) throws NullPointerException;


    /**
     * Method removes all items from the manager.
     */
    void clear();


    /**
     * Method tests whether the manager contains the passed item.
     *
     * @param item                  Item for which to test whether it is being managed.
     * @return                      Whether the passed item is being managed.
     * @throws NullPointerException The passed item is {@code null}.
     */
    boolean contains(T item) throws NullPointerException;

    /**
     * Method tests whether the manager contains an item with the passed UUID.
     *
     * @param uuid                  UUID for which to test whether an item with this UUID is being
     *                              managed.
     * @return                      Whether an item with the passed UUID is being managed.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    boolean contains(String uuid) throws NullPointerException;


    /**
     * Method returns the item of the specified UUID. If no item with the specified UUID exists,
     * {@code null} is returned.
     *
     * @param uuid                  UUID of the item to return.
     * @return                      Item of the specified UUID.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    T get(String uuid) throws NullPointerException;


    /**
     * Method returns whether the manager is not managing any items.
     *
     * @return  Whether the manager is currently empty.
     */
    boolean isEmpty();


    /**
     * Method removes the passed item from the managed items.
     *
     * @param item                  Item which shall be removed.
     * @return                      Whether the item was removed.
     * @throws NullPointerException The passed item is {@code null}.
     */
    boolean remove(T item) throws NullPointerException;

    /**
     * Method removes the item with the passed UUID from the managed items.
     *
     * @param uuid                  UUID of the item to be removed.
     * @return                      The item that was removed or {@code null} if no item could be
     *                              removed.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    T remove(String uuid) throws NullPointerException;


    /**
     * Method replaces the manged item of the specified UUID with the passed item.
     *
     * @param item                          Item with which to replace the managed item.
     * @param uuid                          UUID of the managed item to be replaced.
     * @throws NullPointerException         The passed item or UUID is {@code null}.
     */
    void set(T item, String uuid) throws NullPointerException;


    /**
     * Method returns the number of items that are being managed.
     *
     * @return  Number of items being managed.
     */
    int size();

}
