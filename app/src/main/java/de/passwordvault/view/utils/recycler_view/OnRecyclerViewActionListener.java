package de.passwordvault.view.utils.recycler_view;

import androidx.recyclerview.widget.RecyclerView;


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
     * @param holder    View holder on which the action is invoked.
     */
    void onAction(RecyclerView.ViewHolder holder);

}
