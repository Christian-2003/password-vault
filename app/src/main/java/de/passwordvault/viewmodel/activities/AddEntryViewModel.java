package de.passwordvault.viewmodel.activities;

import androidx.lifecycle.ViewModel;
import de.passwordvault.model.entry.EntryExtended;
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
    private EntryExtended entry;

    /**
     * Attribute stores the tags of the entry that are being edited.
     */
    private TagCollection tags;


    /**
     * Constructor instantiates a new AddEntryViewModel with default values.
     */
    public AddEntryViewModel() {
        entry = null;
    }


    public EntryExtended getEntry() {
        return entry;
    }

    public void setEntry(EntryExtended entry) {
        this.entry = entry;
    }

    public TagCollection getTags() {
        return tags;
    }

    public void setTags(TagCollection tags) throws NullPointerException {
        if (tags == null) {
            throw new NullPointerException();
        }
        this.tags = tags;
    }

}
