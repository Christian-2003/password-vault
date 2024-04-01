package de.passwordvault.service.autofill;

import java.util.ArrayList;
import java.util.HashMap;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.detail.DetailType;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.packages.Package;
import de.passwordvault.model.storage.app.StorageManager;


/**
 * Class models a data fetcher which can fetch user data from the internal storage.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class DataFetcher {

    /**
     * Attribute stores the storage manager used to load the entries.
     */
    private final StorageManager manager;

    /**
     * Attribute stores the entries that were loaded from storage.
     */
    private HashMap<String, EntryAbbreviated> entries;


    /**
     * Constructor instantiates a new data fetcher and automatically loads the abbreviated entries.
     */
    public DataFetcher() {
        manager = new StorageManager();
        try {
            entries = manager.loadAbbreviatedEntries();
        }
        catch (Exception e) {
            entries = new HashMap<>();
        }
    }


    /**
     * Method fetches the user data for the specified package.
     *
     * @param packageName   Name of the package for which the user data shall be fetched.
     * @return              List of fetched user data.
     */
    public ArrayList<UserData> fetchUserDataForPackage(String packageName) {
        ArrayList<UserData> fetchedData = new ArrayList<>();
        for (EntryAbbreviated abbreviated : entries.values()) {
            for (Package p : abbreviated.getPackages()) {
                if (p.getPackageName().equals(packageName)) {
                    //Found entry:
                    EntryExtended extended;
                    try {
                        extended = manager.loadExtendedEntry(abbreviated);
                        fetchedData.add(generateUserDataForEntry(extended));
                    }
                    catch (Exception e) {
                        //Ignore...
                    }
                }
            }
        }
        return fetchedData;
    }


    /**
     * Method fetches the user data for a single entry. If no entry exists, {@code null} is returned.
     *
     * @param uuid                  UUID of the entry for which userdata shall be fetched.
     * @return                      UserData for the respective entry.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    public UserData fetchUserDataForUuid(String uuid) throws NullPointerException {
        if (uuid == null) {
            throw new NullPointerException();
        }
        if (entries.containsKey(uuid)) {
            EntryExtended extended;
            try {
                extended = manager.loadExtendedEntry(entries.get(uuid));
                return generateUserDataForEntry(extended);
            }
            catch (Exception e) {
                //Ignore...
            }
        }
        return null;
    }


    /**
     * Method generates a UserData-instance for the passed extended entry.
     *
     * @param entry                 Entry for which to generate an instance of UserData.
     * @return                      Generated UserData-instance.
     * @throws NullPointerException The passed entry is {@code null}.
     */
    private UserData generateUserDataForEntry(EntryExtended entry) throws NullPointerException {
        if (entry == null) {
            throw new NullPointerException();
        }
        String username = null;
        String password = null;

        for (Detail detail : entry.getDetails()) {
            if (username == null && detail.isUsername()) {
                username = detail.getContent();
            }
            else if (password == null && detail.isPassword()) {
                password = detail.getContent();
            }
        }

        //If username or password were not specified, try to find next best option:
        if (username == null || password == null) {
            for (Detail detail : entry.getDetails()) {
                if (username == null && detail.getType() == DetailType.EMAIL) {
                    username = detail.getContent();
                }
                if (password == null && detail.getType() == DetailType.PASSWORD) {
                    password = detail.getContent();
                }
            }
        }

        return new UserData(entry.getName(), entry.getUuid(), username, password);
    }

}
