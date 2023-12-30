package de.passwordvault.model;


/**
 * Interface defines an implementing class to be observable by {@link Observer}s. Implementing classes
 * contain data which is being observed by registered Observers. Whenever the observed data is
 * changed, all registered observers are informed about the change through the
 * {@link #notifyObservers()} method, which calls the {@link Observer#update(Observable)}-method
 * of all observers.
 *
 * @param <T>   Type of the data which is being observed.
 * @author      Christian-2003
 * @version     3.1.0
 */
public interface Observable<T> {


    /**
     * Method registers the passed {@link Observer}. Whenever the relevant data of the implementing
     * class is changed, all Observers which were previously registered through this method are
     * informed about the changed data.
     *
     * @param o                     Observer to be registered.
     * @throws NullPointerException The passed Observer is {@code null}.
     */
    void addObserver(Observer<T> o) throws NullPointerException;


    /**
     * Method returns the data which is being observed. This method must always return the newest
     * data from the implemented instance.
     *
     * @return  Newest data which is being observed.
     */
    T getData();


    /**
     * Method notifies all registered {@link Observer}s about a change of the observed data through
     * their {@link Observer#update(Observable)}-method.
     */
    void notifyObservers();


    /**
     * Method removes the specified {@link Observer} from the implementing class. When the observed
     * data is changed in the future, the removed Observer will not be informed.
     *
     * @param o                     Observer to be removed from the implementing class' observers.
     * @return                      Whether the specified Observer was successfully removed.
     * @throws NullPointerException The passed Observer is {@code null}.
     */
    boolean removeObserver(Observer<T> o) throws NullPointerException;

}
