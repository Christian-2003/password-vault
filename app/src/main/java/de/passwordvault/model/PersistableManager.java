package de.passwordvault.model;


import de.passwordvault.model.storage.app.StorageException;

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
     *
     * @throws StorageException The data could not be loaded.
     */
    void load() throws StorageException;

    /**
     * Method saves the managed items into persistent storage.
     *
     * @throws StorageException The data could not be saved.
     */
    void save() throws StorageException;

    /**
     * Method saves the managed items into persistent storage.
     *
     * @param force             Indicates whether the managed items shall be saved, even if no
     *                          changes were made to the managed items.
     * @throws StorageException The data could not be saved.
     */
    void save(boolean force) throws StorageException;

}
