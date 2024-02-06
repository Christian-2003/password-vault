package de.passwordvault.model;


/**
 * Interface requires implementing classes to have methods that are capable of loading and storing
 * the managed items from / into persistent storage.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public interface PersistableManager {

    /**
     * Method loads previously saved items into the manager.
     */
    void load();

    /**
     * Method saves the managed items into persistent storage.
     */
    void save();

    /**
     * Method saves the managed items into persistent storage.
     *
     * @param force Indicates whether the managed items shall be saved, even if no changes were
     *              made to the managed items.
     */
    void save(boolean force);

}
