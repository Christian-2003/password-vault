package de.passwordvault.viewmodel.activities;

import androidx.lifecycle.ViewModel;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.view.activities.EntryActivity;


/**
 * Class models a {@linkplain ViewModel} for the {@linkplain EntryActivity} which contains all
 * relevant data that shall be persistent throughout activity changes.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class EntryViewModel extends ViewModel {

    /**
     * Attribute stores the entry that is displayed through the EntryActivity.
     */
    private EntryExtended entry;

    /**
     * Attribute stores whether the extended info of the entry is currently shown.
     */
    private boolean extendedInfoShown;

    /**
     * Attribute stores the result code that is sent to the activity that launched {@link EntryActivity}.
     */
    private int resultCode;


    /**
     * Constructor instantiates a new EntryViewModel with default values.
     */
    public EntryViewModel() {
        entry = null;
        extendedInfoShown = false;
        resultCode = EntryActivity.RESULT_OK;
    }


    /**
     * Method returns the entry being displayed.
     *
     * @return  Displayed entry.
     */
    public EntryExtended getEntry() {
        return entry;
    }

    /**
     * Method changes the entry being displayed.
     *
     * @param entry New entry to display.
     */
    public void setEntry(EntryExtended entry) {
        this.entry = entry;
    }

    /**
     * Method returns whether extended info is shown.
     *
     * @return  Whether extended info is shown.
     */
    public boolean isExtendedInfoShown() {
        return extendedInfoShown;
    }

    /**
     * Method changes whether extended info is shown.
     *
     * @param extendedInfoShown Whether extended info is currently shown.
     */
    public void setExtendedInfoShown(boolean extendedInfoShown) {
        this.extendedInfoShown = extendedInfoShown;
    }

    /**
     * Method returns the result code that shall be sent to the activity that launched the
     * {@link EntryActivity}. This is {@link EntryActivity#RESULT_OK} by default.
     *
     * @return  Result code.
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Method changes the result code that shall be sent to the activity that launched the
     * {@link EntryActivity}.
     *
     * @param resultCode    New result code.
     */
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

}
