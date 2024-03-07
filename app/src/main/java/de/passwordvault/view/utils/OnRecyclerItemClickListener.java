package de.passwordvault.view.utils;


/**
 * Interface can be implemented by classes to be called when an item within a recycler view is clicked.
 *
 * @param <T>   Type of the data that is being displayed by the recycler view.
 * @author      Christian-2003
 * @version     3.4.0
 */
public interface OnRecyclerItemClickListener<T> {

    /**
     * Method is called when the item which is passed as argument is clicked by the user.
     *
     * @param item      Clicked item.
     * @param position  Index of the clicked item.
     */
    void onItemClick(T item, int position);

}
