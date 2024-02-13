package de.passwordvault.model;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Abstract class models a generic manager which can manage any number of {@link Identifiable}-
 * instances. Subclasses of this class must implement the methods required by {@link PersistableManager}
 * to load and save data.
 * Internally, the manager works with a hash map to accelerate access to elements. Therefore, no
 * indexes are supported working with this manager.
 *
 * @param <T>   Type of Identifiable-instances to be managed.
 * @author      Christian-2003
 * @version     3.3.0
 */
public abstract class GenericManager<T extends Identifiable> implements Manager<T>, Observable<ArrayList<T>> {

    /**
     * Attribute stores whether changes were made to the managed items. This is used to determine
     * whether it is necessary to save the items to persistent storage.
     */
    protected boolean changesMade;

    /**
     * Attribute stores the array list that was generated to be returned by the observer.
     */
    private final ArrayList<T> cachedArrayList;

    /**
     * Attribute stores whether changes were made to the arraylist
     */
    private boolean changesSinceLastCachedArrayList;

    /**
     * Attribute stores the items that are being managed by this instance. Internally, this is the
     * main method of managing the items.
     * Key for the hash map is the UUID of the respective {@link Identifiable}-item.
     */
    protected HashMap<String, T> items;

    /**
     * Attribute stores the registered observers of the manager.
     */
    private final ArrayList<Observer<ArrayList<T>>> observers;


    /**
     * Constructor instantiates a new <c>GenericManager</c>. The constructor automatically loads
     * all items from persistent storage.
     */
    public GenericManager() {
        items = new HashMap<>();
        observers = new ArrayList<>();
        cachedArrayList = new ArrayList<>();
        changesMade = false;
        changesSinceLastCachedArrayList = true; //Set to true so that new array list is generated when first accessed.
    }


    /**
     * Method adds the specified item to the managed items.
     *
     * @param item                  Item to be added.
     * @throws NullPointerException The passed item is {@code null}.
     */
    @Override
    public void add(T item) throws NullPointerException {
        if (item == null) {
            throw new NullPointerException();
        }
        items.put(item.getUuid(), item);
        changesMade = true;
        changesSinceLastCachedArrayList = true;
        notifyObservers();
    }


    /**
     * Method removes all items from the manager.
     */
    @Override
    public void clear() {
        items.clear();
        changesMade = true;
        changesSinceLastCachedArrayList = true;
        notifyObservers();
    }


    /**
     * Method tests whether the manager contains the passed item.
     *
     * @param item                  Item for which to test whether it is being managed.
     * @return                      Whether the passed item is being managed.
     * @throws NullPointerException The passed item is {@code null}.
     */
    @Override
    public boolean contains(T item) throws NullPointerException {
        if (item == null) {
            throw new NullPointerException();
        }
        return items.containsValue(item);
    }

    /**
     * Method tests whether the manager contains an item with the passed UUID.
     *
     * @param uuid                  UUID for which to test whether an item with this UUID is being
     *                              managed.
     * @return                      Whether an item with the passed UUID is being managed.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    @Override
    public boolean contains(String uuid) throws NullPointerException {
        if (uuid == null) {
            throw new NullPointerException();
        }
        return items.containsKey(uuid);
    }


    /**
     * Method returns the item of the specified UUID. If no item with the specified UUID exists,
     * {@code null} is returned.
     *
     * @param uuid                  UUID of the item to return.
     * @return                      Item of the specified UUID.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    @Override
    public T get(String uuid) throws NullPointerException {
        if (uuid == null) {
            throw new NullPointerException();
        }
        return items.get(uuid);
    }


    /**
     * Method returns whether the manager is not managing any items.
     *
     * @return  Whether the manager is currently empty.
     */
    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }


    /**
     * Method removes the passed item from the managed items.
     *
     * @param item                  Item which shall be removed.
     * @return                      Whether the item was removed.
     * @throws NullPointerException The passed item is {@code null}.
     */
    @Override
    public boolean remove(T item) throws NullPointerException {
        if (item == null) {
            throw new NullPointerException();
        }
        boolean removed = items.remove(item.getUuid()) != null;
        if (removed) {
            changesMade = true;
            changesSinceLastCachedArrayList = true;
            notifyObservers();
        }
        return removed;
    }

    /**
     * Method removes the item with the passed UUID from the managed items.
     *
     * @param uuid                  UUID of the item to be removed.
     * @return                      Whether the item was removed.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    @Override
    public T remove(String uuid) throws NullPointerException {
        if (uuid == null) {
            throw new NullPointerException();
        }
        T removed = items.remove(uuid);
        if (removed != null) {
            changesMade = true;
            changesSinceLastCachedArrayList = true;
            notifyObservers();
        }
        return removed;
    }


    /**
     * Method replaces the manged item of the specified UUID with the passed item.
     *
     * @param item                          Item with which to replace the managed item.
     * @param uuid                          UUID of the managed item to be replaced.
     * @throws NullPointerException         The passed item or UUID is {@code null}.
     */
    @Override
    public void set(T item, String uuid) throws NullPointerException {
        if (item == null || uuid == null) {
            throw new NullPointerException();
        }
        if (items.get(uuid) != item) {
            items.put(uuid, item);
            changesMade = true;
            changesSinceLastCachedArrayList = true;
            notifyObservers();
        }
    }


    /**
     * Method returns the number of items that are being managed.
     *
     * @return  Number of items being managed.
     */
    @Override
    public int size() {
        return items.size();
    }



    /**
     * Method registers the passed {@link Observer}. Whenever the relevant data of the implementing
     * class is changed, all Observers which were previously registered through this method are
     * informed about the changed data.
     *
     * @param o                     Observer to be registered.
     * @throws NullPointerException The passed Observer is {@code null}.
     */
    @Override
    public void addObserver(Observer<ArrayList<T>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException();
        }
        observers.add(o);
    }


    /**
     * Method returns the data which is being observed. This method must always return the newest
     * data from the implemented instance.
     *
     * @return  Newest data which is being observed.
     */
    @Override
    public ArrayList<T> getData() {
        if (changesSinceLastCachedArrayList) {
            generateArrayListCache();
        }
        return cachedArrayList;
    }


    /**
     * Method notifies all registered {@link Observer}s about a change of the observed data through
     * their {@link Observer#update(Observable)}-method.
     */
    @Override
    public void notifyObservers() {
        for (Observer<ArrayList<T>> o : observers) {
            o.update(this);
        }
    }


    /**
     * Method removes the specified {@link Observer} from the implementing class. When the observed
     * data is changed in the future, the removed Observer will not be informed.
     *
     * @param o                     Observer to be removed from the implementing class' observers.
     * @return                      Whether the specified Observer was successfully removed.
     * @throws NullPointerException The passed Observer is {@code null}.
     */
    @Override
    public boolean removeObserver(Observer<ArrayList<T>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException();
        }
        return observers.remove(o);
    }


    /**
     * Method generates a new array list cache to be returned by the manager as observable.
     */
    private void generateArrayListCache() {
        cachedArrayList.clear();
        cachedArrayList.addAll(items.values());
        changesSinceLastCachedArrayList = false;
    }

}
