package de.passwordvault.view.entries.activity_entry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import de.passwordvault.model.entry.EntryExtended;


/**
 * Class implements a view model for the {@link EntryActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class EntryViewModel extends ViewModel {

    /**
     * Attribute stores the entry displayed by the activity.
     */
    @Nullable
    private EntryExtended entry;

    /**
     * Attribute indicates whether the invisible details are expanded.
     */
    private boolean invisibleDetailsExpanded;


    /**
     * Constructor instantiates a new view model.
     */
    public EntryViewModel() {
        entry = null;
        invisibleDetailsExpanded = false;
    }


    /**
     * Method returns the entry. This is {@code null} if the entry was not previously set through
     * {@link #setEntry(EntryExtended)}.
     *
     * @return  Extended entry.
     */
    @Nullable
    public EntryExtended getEntry() {
        return entry;
    }

    /**
     * Method changes the entry.
     *
     * @param entry New entry.
     */
    public void setEntry(@NonNull EntryExtended entry) {
        this.entry = entry;
    }

    /**
     * Method returns whether the list of invisible details is expanded.
     *
     * @return  Whether the list of invisible details is expanded.
     */
    public boolean areInvisibleDetailsExpanded() {
        return invisibleDetailsExpanded;
    }

    /**
     * Method changes whether the list of invisible details is expanded.
     *
     * @param invisibleDetailsExpanded  Whether the list of invisible details is expanded.
     */
    public void setInvisibleDetailsExpanded(boolean invisibleDetailsExpanded) {
        this.invisibleDetailsExpanded = invisibleDetailsExpanded;
    }

}
