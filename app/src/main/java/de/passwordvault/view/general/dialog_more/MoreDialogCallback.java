package de.passwordvault.view.general.dialog_more;


/**
 * Interface can be implemented by an activity or fragment as callback for the {@link MoreDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public interface MoreDialogCallback {

    /**
     * Method is called whenever any action within the {@link MoreDialog} is invoked.
     *
     * @param dialog    Dialog in which the action was invoked.
     * @param tag       Tag from the {@link Item} whose action is invoked.
     * @param position  Position of the {@link Item} within the dialog.
     */
    void onDialogItemClicked(MoreDialog dialog, String tag, int position);

}
