package de.passwordvault.view.entries.activity_entry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import de.passwordvault.view.utils.recycler_view.RecyclerViewSwipeCallback;


public class EntryRecyclerViewMoveCallback extends RecyclerViewSwipeCallback {

    /**
     * Class models a range in which a detail can be moved.
     */
    public static class MoveRange {

        /**
         * Attribute stores the min position to which a detail can be moved.
         */
        private final int min;

        /**
         * Attribute stores the max position to which a detail can be moved.
         */
        private final int max;


        /**
         * Constructor instantiates a new range in between a detail can be moved.
         *
         * @param min   Min position to which a detail can be moved.
         * @param max   Max position to which a detail can be moved.
         */
        public MoveRange(int min, int max) {
            this.min = min;
            this.max = max;
        }


        /**
         * Method returns the min position to which a detail can be moved.
         *
         * @return  Min position.
         */
        public int getMin() {
            return min;
        }

        /**
         * Method returns the max position to which a detail can be moved.
         *
         * @return  Max position.
         */
        public int getMax() {
            return max;
        }

    }


    /**
     * Interface needs to be implemented by the recycler view adapter for which to create the move
     * callback.
     */
    public interface EntryMoveContract extends SwipeContract {

        /**
         * Method is called to move a detail between the specified positions.
         *
         * @param from  Position of the detail to move.
         * @param to    Position to which to move the detail.
         */
        void onRowMove(int from, int to);

        /**
         * Method determines the range in between a detail at the specified position can be moved.
         * The method returns {@code null} if the item at the specified position does not support
         * moving (e.g. if the item is not a detail).
         *
         * @param position  Position of the detail for which to determine the movable range.
         * @return          Move range for the detail at the specified position.
         */
        @Nullable
        MoveRange getMoveRange(int position);

        /**
         * Method determines whether the item at the specified position can be moved.
         *
         * @param position  Position of the item for which to determine whether it can be moved.
         * @return          Whether the item at the specified position can be moved.
         */
        boolean supportsMove(int position);

    }


    /**
     * Attribute stores the recycler view adapter.
     */
    @NonNull
    private final EntryMoveContract adapter;


    /**
     * Constructor instantiates a new move callback for the passed recycler view adapter.
     *
     * @param adapter           Adapter for the callback.
     * @param leftSwipeAction   Action for when an item is left swiped.
     * @param rightSwipeAction  Action for when an item is right swiped.
     */
    public EntryRecyclerViewMoveCallback(@NonNull EntryMoveContract adapter, @Nullable SwipeAction leftSwipeAction, @Nullable SwipeAction rightSwipeAction) {
        super(adapter, leftSwipeAction, rightSwipeAction);
        this.adapter = adapter;
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
        MoveRange range = adapter.getMoveRange(viewHolder.getAdapterPosition());
        if (range != null && range.getMin() <= target.getAdapterPosition() && range.getMax() >= target.getAdapterPosition()) {
            adapter.onRowMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }
        return false;
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
        int swipeFlags = 0;
        int dragFlags = 0;
        if (adapter.supportsSwipe(viewHolder.getAdapterPosition())) {
            if (leftSwipeAction != null && rightSwipeAction != null) {
                swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
            else if (leftSwipeAction != null) {
                swipeFlags = ItemTouchHelper.LEFT;
            }
            else if (rightSwipeAction != null) {
                swipeFlags = ItemTouchHelper.RIGHT;
            }
        }
        if (adapter.supportsMove(viewHolder.getAdapterPosition())) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

}
