package de.passwordvault.viewmodel.activities;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;


/**
 * Class implements a view model for the
 * {@link de.passwordvault.view.activities.DuplicatePasswordEntriesActivity}.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class DuplicatePasswordEntriesViewModel extends ViewModel {

    /**
     * Attribute stores the list of passwords that are duplicate.
     */
    private ArrayList<Password> passwords;

    /**
     * Attribute stores the entries that are being displayed to the user.
     */
    private ArrayList<EntryAbbreviated> entries;

    /**
     * If the user clicked on an entry to display, a reference to the entry is stored here. In case
     * the user deletes the entry, this reference can be used to remove the deleted entry from the
     * list of entries.
     */
    private EntryAbbreviated displayedEntry;


    /**
     * Constructor instantiates a new view model.
     */
    public DuplicatePasswordEntriesViewModel() {
        passwords = null;
        entries = null;
        displayedEntry = null;
    }


    /**
     * Method returns the list of duplicate passwords. This can be {@code null} if the list
     * was not set before.
     *
     * @return  List of duplicate passwords.
     */
    public ArrayList<Password> getPasswords() {
        return passwords;
    }

    /**
     * Method changes the list of duplicate passwords to the passed argument.
     *
     * @param passwords             List of duplicate passwords.
     * @throws NullPointerException The passed list is {@code null}.
     */
    public void setPasswords(ArrayList<Password> passwords) throws NullPointerException {
        if (passwords == null) {
            throw new NullPointerException();
        }
        this.passwords = passwords;
        entries = new ArrayList<>();
        for (Password password : passwords) {
            EntryAbbreviated abbreviated = EntryManager.getInstance().getAbbreviated(password.getEntryUuid());
            if (abbreviated != null) {
                entries.add(abbreviated);
            }
        }
    }

    /**
     * Method returns the list of entries to display to the user. This is {@code null}, unless
     * the passwords have been set through {@link #setPasswords(ArrayList)}.
     *
     * @return  List of entries.
     */
    public ArrayList<EntryAbbreviated> getEntries() {
        return entries;
    }


    /**
     * Method returns the displayed entry.
     *
     * @return  Displayed entry.
     * @see     #displayedEntry
     */
    public EntryAbbreviated getDisplayedEntry() {
        return displayedEntry;
    }

    /**
     * Method changes the displayed entry.
     *
     * @param displayedEntry    New displayed entry.
     * @see                     #displayedEntry
     */
    public void setDisplayedEntry(EntryAbbreviated displayedEntry) {
        this.displayedEntry = displayedEntry;
    }

}
