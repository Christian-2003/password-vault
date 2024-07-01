package de.passwordvault.view.entries.activity_add_entry;

import androidx.lifecycle.ViewModel;
import de.passwordvault.model.entry.EntryExtended;


/**
 * Class models a {@linkplain ViewModel} for the {@linkplain AddEntryActivity} which contains all
 * relevant data that shall be persistent throughout activity changes.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class AddEntryViewModel extends ViewModel {

    /**
     * Attribute stores the entry that is currently being created / edited.
     */
    private EntryExtended entry;


    /**
     * Constructor instantiates a new AddEntryViewModel with default values.
     */
    public AddEntryViewModel() {
        entry = null;
    }


    /**
     * Method returns the entry that is being edited.
     *
     * @return  Entry that is being edited.
     */
    public EntryExtended getEntry() {
        return entry;
    }

    /**
     * Method changes the entry that is being edited. The entry is copied through it's copy-constructor.
     * Therefore, changes do not automatically apply to the {@link de.passwordvault.model.entry.EntryManager}.
     *
     * @param entry Entry to edit.
     */
    public void setEntry(EntryExtended entry) {
        this.entry = new EntryExtended(entry);
    }

}
