package de.passwordvault.view.utils.recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


/**
 * Class implements a generic recycler view adapter which provides basic functionalities that are
 * required by other adapters within the app.
 * The adapter implements the {@link RecyclerItemSwipeCallback.ItemSwipeContract} and is therefore
 * suited for swipe actions.
 *
 * @param <V>   Type of the ViewHolder for the adapter.
 * @param <D>   Type of the data displayed by the adapter.
 * @author      Christian-2003
 * @version     3.6.0
 */
public abstract class GenericRecyclerViewAdapter<V extends RecyclerView.ViewHolder, D> extends RecyclerView.Adapter<V> implements RecyclerItemSwipeCallback.ItemSwipeContract<D> {

    /**
     * Attribute stores a layout inflater with which subclasses can inflate layouts.
     */
    protected final LayoutInflater layoutInflater;

    /**
     * Attribute stores the data for the adapter.
     */
    protected final ArrayList<D> data;

    /**
     * Attribute stores the context for the adapter.
     */
    private final Context context;


    /**
     * Constructor instantiates a new generic recycler view adapter.
     *
     * @param context   Activity context.
     * @param data      Data for the adapter.
     */
    public GenericRecyclerViewAdapter(@NonNull Context context, @NonNull ArrayList<D> data) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }


    /**
     * Method returns the context of the adapter.
     *
     * @return  Context of the adapter.
     */
    public Context getContext() {
        return context;
    }


    /**
     * Method returns the number of items within the adapter.
     *
     * @return  Number of items.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }


    /**
     * Method is called whenever an item is being swiped.
     *
     * @param viewHolder    View holder of the swiped item.
     * @param swipeAction   Swipe action to call when an item was swiped.
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, OnRecyclerItemClickListener<D> swipeAction) {
        int position = viewHolder.getAdapterPosition();
        swipeAction.onItemClick(data.get(position), position);
    }

}
