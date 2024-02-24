package de.passwordvault.view.utils;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.passwordvault.model.detail.Detail;
import de.passwordvault.view.activities.AddEntryActivity;
import de.passwordvault.view.dialogs.ConfirmDeleteDetailDialog;


/**
 * Class implements a callback for movement of details within a recycler view.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class DetailsItemMoveCallback extends ItemTouchHelper.Callback {

    /**
     * Interface can be implemented by the recycler view adapter. The required methods are necessary
     * for the {@link DetailsItemMoveCallback} to work properly.
     */
    public interface ItemTouchHelperContract {

        /**
         * Method is called when an item is moved between two positions within the recycler view.
         *
         * @param fromPosition  Original position of the item.
         * @param toPosition    New position of the item.
         */
        void onRowMoved(int fromPosition, int toPosition);

        /**
         * Method is called when a specific item within the recycler view is selected.
         *
         * @param viewHolder    View holder of the selected item.
         */
        void onRowSelected(DetailsRecyclerViewAdapter.ViewHolder viewHolder);

        /**
         * Method is called when a previously selected item within the recycler view is being
         * unselected.
         *
         * @param viewHolder    View holder of the unselected item.
         */
        void onRowClear(DetailsRecyclerViewAdapter.ViewHolder viewHolder);

        /**
         * Method is called whenever an item is being swiped.
         *
         * @param viewHolder    View holder if the swiped item.
         * @param direction     Direction into which the item was swiped.
         */
        void onRowSwiped(DetailsRecyclerViewAdapter.ViewHolder viewHolder, int direction);

    }


    /**
     * Attribute stores the recycler view adapter of this callback.
     */
    private final ItemTouchHelperContract adapter;


    /**
     * Constructor instantiates a new callback for a recycler view.
     *
     * @param adapter               Recycler view adapter for which this callback is being constructed.
     * @throws NullPointerException The passed adapter is {@code null}.
     */
    public DetailsItemMoveCallback(ItemTouchHelperContract adapter) throws NullPointerException {
        if (adapter == null) {
            throw new NullPointerException();
        }
        this.adapter = adapter;
    }


    /**
     * Method defines whether drag and drop per long press is enabled.
     *
     * @return  Whether drag and drop per long press is enabled.
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * Method defines whether item swipe is enabled.
     *
     * @return  Whether item swipe is enabled.
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * Method is called when an item is swiped.
     *
     * @param viewHolder    The ViewHolder which has been swiped by the user.
     * @param direction     The direction to which the ViewHolder is swiped.
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onRowSwiped((DetailsRecyclerViewAdapter.ViewHolder)viewHolder, direction);
    }

    /**
     * Method returns the movement flags of the move action. Movement flags indicate into which direction
     * the item can be moved when it is being dragged. With this implementation, the item can only
     * be dragged up and down.
     *
     * @param recyclerView  The RecyclerView to which ItemTouchHelper is attached.
     * @param viewHolder    The ViewHolder for which the movement information is necessary.
     * @return              Movement flags.
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * Method is called whenever an item is being moved.
     *
     * @param recyclerView  The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder    The ViewHolder which is being dragged by the user.
     * @param target        The ViewHolder over which the currently active item is being dragged.
     * @return              {@code true}.
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        adapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     * Method is called whenever the selection of an item within the recycler view is being changed.
     *
     * @param viewHolder    The new ViewHolder that is being swiped or dragged. Might be null if
     *                      it is cleared.
     * @param actionState   Action state of the selection.
     */
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof DetailsRecyclerViewAdapter.ViewHolder) {
                adapter.onRowSelected((DetailsRecyclerViewAdapter.ViewHolder)viewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * Method is called when a previously selected item within the recycler view is unselected.
     *
     * @param recyclerView  The RecyclerView which is controlled by the ItemTouchHelper.
     * @param viewHolder    The View that was interacted by the user.
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof DetailsRecyclerViewAdapter.ViewHolder) {
            adapter.onRowClear((DetailsRecyclerViewAdapter.ViewHolder)viewHolder);
        }
    }

}
