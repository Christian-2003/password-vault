package de.passwordvault.view.utils.recycler_view;


/**
 * Functional interface provides a generic action listener for recycler items.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public interface OnRecyclerViewActionListener {

    /**
     * Method is called whenever the recycler view action is invoked.
     *
     * @param position    Position of the item within the recycler view adapter for which the action
     *                    is invoked.
     */
    void onAction(int position);

}
