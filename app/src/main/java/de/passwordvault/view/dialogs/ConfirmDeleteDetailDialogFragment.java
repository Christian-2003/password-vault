package de.passwordvault.view.dialogs;

import android.app.Dialog;

import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.model.detail.Detail;


/**
 * Class models a {@linkplain Dialog} which allows the user to confirm a deletion of a {@linkplain Detail}
 * The {@linkplain android.app.Activity} that creates this dialog, must implement the interface
 * {@linkplain DialogCallbackListener}!
 *
 * @author  Christian-2003
 * @version 1.0.0
 */
public class ConfirmDeleteDetailDialogFragment extends ConfirmDeleteDialogFragment {

    /**
     * Attribute stores the UUID of the detail to be deleted.
     */
    private String uuid;


    /**
     * Constructor constructs a new ConfirmDetailDeleteDialogFragment which allows the user to confirm
     * the deletion of the passed detail.
     *
     * @param detail    Detail to be deleted.
     */
    public ConfirmDeleteDetailDialogFragment(Detail detail) {
        super(detail.getName());
        uuid = detail.getUuid();
    }


    public String getUuid() {
        return uuid;
    }

}
