package de.passwordvault.model;


/**
 * Interface defines an implementing class to be an observer for a specific {@link Observable}-
 * instance. If the data of the Observable is changed, all implementing classes of this interface
 * which are registered within the Observable are notified that the observed data is changed. The
 * Observable will call the {@link #update(Observable)}-method of all registered Observers.
 *
 * @param <T>   Type of the data which is being observed.
 * @author      Christian-2003
 * @version     3.1.0
 */
public interface Observer<T> {

    /**
     * Method informs the {@link Observer} that the observed data has been changed. The passed
     * {@link Observable} references the object which is being observed.
     *
     * @param o                     Observed instance whose data was changed.
     * @throws NullPointerException The passed Observable is {@code null}.
     */
    void update(Observable<T> o) throws NullPointerException;

}
