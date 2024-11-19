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
     * Attribute stores the activity result code.
     */
    private int resultCode;

    /**
     * Attribute indicates whether the invisible details are expanded.
     */
    private boolean invisibleDetailsExpanded;

    /**
     * Attribute stores whether any data of the entry is edited by the user.
     */
    private boolean edited;

    /**
     * Attribute stores whether changes were made to the entry, that are visible to the lists that
     * display this entry. These changes usually include the name, description and app logo.
     */
    private boolean visiblyEdited;


    /**
     * Constructor instantiates a new view model.
     */
    public EntryViewModel() {
        entry = null;
        invisibleDetailsExpanded = false;
        edited = false;
        visiblyEdited = false;
        resultCode = -1;
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
     * Method returns the result code which will be returned by the activity.
     *
     * @return  Result code for the activity.
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Method changes the result code to return from the activity.
     *
     * @param resultCode    New result code.
     */
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
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

    /**
     * Method returns whether any information of the entry has been edited.
     *
     * @return  Whether any data has been edited.
     */
    public boolean isEdited() {
        return edited;
    }

    /**
     * Method informs the view model that the entry has been edited by the user.
     */
    public void entryEdited() {
        this.edited = true;
    }

    /**
     * Method returns whether any changes were made to the entry that are visible within the lists
     * that display this entry (e.g. name, description or app logo).
     *
     * @return  Whether any visible changes were made.
     */
    public boolean isVisiblyEdited() {
        return visiblyEdited;
    }

    /**
     * Method informs the view model that visible changes were made to the entry.
     */
    public void entryVisiblyEdited() {
        entryEdited();
        this.visiblyEdited = true;
    }

}
