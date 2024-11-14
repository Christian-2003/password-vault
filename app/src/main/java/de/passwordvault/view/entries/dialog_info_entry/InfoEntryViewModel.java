package de.passwordvault.view.entries.dialog_info_entry;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.io.Serializable;
import de.passwordvault.model.entry.EntryExtended;


/**
 * Class implements the view model for {@link InfoEntryDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class InfoEntryViewModel extends ViewModel {

    /**
     * Attribute stores the entry for which to display the info dialog.
     */
    @Nullable
    private EntryExtended entry;


    /**
     * Constructor instantiates a new view model.
     */
    public InfoEntryViewModel() {
        entry = null;
    }


    /**
     * Method returns the entry for which to display the dialog.
     *
     * @return  Entry for which to display the dialog.
     */
    @Nullable
    public EntryExtended getEntry() {
        return entry;
    }


    /**
     * Method processes the arguments that are passed to the {@link InfoEntryDialog}.
     *
     * @param args  Arguments that were passed.
     */
    public void processArguments(@Nullable Bundle args) {
        if (entry == null && args != null && args.containsKey(InfoEntryDialog.ARG_ENTRY)) {
            Serializable entry = args.getSerializable(InfoEntryDialog.ARG_ENTRY);
            if (entry instanceof EntryExtended) {
                this.entry = (EntryExtended)entry;
            }
        }
    }

}
