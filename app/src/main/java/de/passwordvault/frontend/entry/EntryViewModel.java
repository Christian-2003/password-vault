package de.passwordvault.frontend.entry;

import androidx.lifecycle.ViewModel;
import de.passwordvault.backend.entry.Entry;


/**
 * Class models a {@linkplain ViewModel} for the {@linkplain EntryActivity} which contains all
 * relevant data that shall be persistent throughout activity changes.
 *
 * @author  Christian-2003
 * @version 1.0.0
 */
public class EntryViewModel extends ViewModel {

    /**
     * Attribute stores the entry that is displayed through the EntryActivity.
     */
    private Entry entry;

    /**
     * Attribute stores whether the extended info of the entry is currently shown.
     */
    private boolean extendedInfoShown;


    /**
     * Constructor instantiates a new EntryViewModel with default values.
     */
    public EntryViewModel() {
        entry = null;
        extendedInfoShown = false;
    }


    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public boolean isExtendedInfoShown() {
        return extendedInfoShown;
    }

    public void setExtendedInfoShown(boolean extendedInfoShown) {
        this.extendedInfoShown = extendedInfoShown;
    }
}
