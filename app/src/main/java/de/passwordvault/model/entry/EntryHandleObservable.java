package de.passwordvault.model.entry;

import java.util.ArrayList;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;


/**
 * Abstract class implements the {@link Observer} for the {@link EntryHandle}.
 *
 * @author  Christian-2003
 * @version 3.1.0
 */
public abstract class EntryHandleObservable implements Observable<ArrayList<Entry>> {

    /**
     * Attribute stores all registered observers.
     */
    private final ArrayList<Observer<ArrayList<Entry>>> observers;


    /**
     * Constructor instantiates a new {@link EntryHandleObservable}-instance.
     */
    public EntryHandleObservable() {
        observers = new ArrayList<>();
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
    public void addObserver(Observer<ArrayList<Entry>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException("Null is invalid Observer");
        }
        observers.add(o);
    }


    /**
     * Method notifies all registered {@link Observer}s about a change of the observed data through
     * their {@link Observer#update(Observable)}-method.
     */
    @Override
    public void notifyObservers() {
        for (Observer<ArrayList<Entry>> o : observers) {
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
    public boolean removeObserver(Observer<ArrayList<Entry>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException("Null is invalid Observer");
        }
        return observers.remove(o);
    }

}
