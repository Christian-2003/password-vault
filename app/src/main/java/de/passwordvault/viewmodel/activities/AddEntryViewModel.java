package de.passwordvault.viewmodel.activities;

import androidx.lifecycle.ViewModel;
import de.passwordvault.model.entry.Entry;
import de.passwordvault.model.tags.TagCollection;
import de.passwordvault.view.activities.AddEntryActivity;


/**
 * Class models a {@linkplain ViewModel} for the {@linkplain AddEntryActivity} which contains all
 * relevant data that shall be persistent throughout activity changes.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class AddEntryViewModel extends ViewModel {

    /**
     * Attribute stores the entry that is currently being created / edited.
     */
    private Entry entry;


    /**
     * Constructor instantiates a new AddEntryViewModel with default values.
     */
    public AddEntryViewModel() {
        entry = null;
    }


    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

}
