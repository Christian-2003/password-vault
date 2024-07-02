package de.passwordvault.view.entries.activity_add_entry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.detail.DetailSwipeAction;
import de.passwordvault.model.storage.settings.Config;
import de.passwordvault.view.entries.activity_entry.DetailsRecyclerViewAdapter;


/**
 * Class implements a callback for movement of details within a recycler view.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class DetailsItemMoveCallback extends ItemTouchHelper.Callback {

    /**
     * Interface can be implemented by the recycler view adapter. The required methods are necessary
     * for the {@link DetailsItemMoveCallback} to work properly.
     */
    public interface ItemTouchHelperContract {

        /**
         * Method returns the context to use with this callback.
         *
         * @return  Context to use with the callback.
         */
        Context getContext();

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
         * @param viewHolder    View holder of the swiped item.
         * @param direction     Direction into which the item was swiped.
         */
        void onRowSwiped(DetailsRecyclerViewAdapter.ViewHolder viewHolder, int direction);

    }


    /**
     * Attribute stores the recycler view adapter of this callback.
     */
    private final ItemTouchHelperContract adapter;

    /**
     * Attribute stores whether the items in the view can be swiped.
     */
    private final boolean canSwipe;

    /**
     * Attribute stores whether the items in the view can be reordered.
     */
    private final boolean canReorder;


    /**
     * Constructor instantiates a new callback for a recycler view.
     *
     * @param adapter               Recycler view adapter for which this callback is being constructed.
     * @param canSwipe              Flag indicates whether the items in the recycler view can be
     *                              swiped.
     * @param canReorder            Flag indicates whether the items in the recycler view can be
     *                              reordered.
     * @throws NullPointerException The passed adapter is {@code null}.
     */
    public DetailsItemMoveCallback(ItemTouchHelperContract adapter, boolean canSwipe, boolean canReorder) throws NullPointerException {
        if (adapter == null) {
            throw new NullPointerException();
        }
        this.adapter = adapter;
        this.canSwipe = canSwipe;
        this.canReorder = canReorder;
    }


    /**
     * Method defines whether drag and drop per long press is enabled.
     *
     * @return  Whether drag and drop per long press is enabled.
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return canReorder;
    }

    /**
     * Method defines whether item swipe is enabled.
     *
     * @return  Whether item swipe is enabled.
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return canSwipe;
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
        /*
        if (adapter instanceof DetailsRecyclerViewAdapter) {
            ((DetailsRecyclerViewAdapter)adapter).notifyItemChanged(viewHolder.getAdapterPosition());
        }
        */
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


    /**
     * Method is called whenever an item is swiped to draw whatever is beneath the swiped item.
     *
     * @param canvas            The canvas which RecyclerView is drawing its children
     * @param recyclerView      The RecyclerView to which ItemTouchHelper is attached to
     * @param viewHolder        The ViewHolder which is being interacted by the User or it was
     *                          interacted and simply animating to its original position
     * @param dX                The amount of horizontal displacement caused by user's action
     * @param dY                The amount of vertical displacement caused by user's action
     * @param actionState       The type of interaction on the View.
     * @param isCurrentlyActive True if this view is currently being controlled by the user or
     *                          false it is simply animating back to its original state.
     */
    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;
            Paint paint = new Paint();
            Bitmap icon;
            int iconMargin = adapter.getContext().getResources().getDimensionPixelSize(R.dimen.space_horizontal);
            if (dX > 1) {
                DetailSwipeAction swipeAction = Config.getInstance().rightSwipeAction.get();
                if (swipeAction == DetailSwipeAction.DELETE) {
                    paint.setColor(adapter.getContext().getColor(R.color.pv_red));
                    icon = getBitmap(R.drawable.ic_delete);
                }
                else {
                    paint.setColor(adapter.getContext().getColor(R.color.pv_primary));
                    icon = getBitmap(R.drawable.ic_edit);
                }
                canvas.drawRect((float)itemView.getLeft(), (float)itemView.getTop(), dX, (float)itemView.getBottom(), paint);
                if (icon != null) {
                    canvas.drawBitmap(icon, (float) itemView.getLeft() + iconMargin, (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2, paint);
                }
            }
            else if (dX < -1) {
                DetailSwipeAction swipeAction = Config.getInstance().leftSwipeAction.get();
                if (swipeAction == DetailSwipeAction.DELETE) {
                    paint.setColor(adapter.getContext().getColor(R.color.pv_red));
                    icon = getBitmap(R.drawable.ic_delete);
                }
                else {
                    paint.setColor(adapter.getContext().getColor(R.color.pv_primary));
                    icon = getBitmap(R.drawable.ic_edit);
                }
                canvas.drawRect((float)itemView.getRight() + dX, (float)itemView.getTop(), (float)itemView.getRight(), (float)itemView.getBottom(), paint);
                if (icon != null) {
                    canvas.drawBitmap(icon, (float)itemView.getRight() - iconMargin - icon.getWidth(), (float)itemView.getTop() + ((float)itemView.getBottom() - (float)itemView.getTop() - icon.getHeight()) / 2, paint);
                }
            }
            float alpha = 1F - Math.abs(dX) / (float)itemView.getWidth();
            itemView.setAlpha(alpha);
            itemView.setTranslationX(dX);
        }
        else {
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }


    /**
     * Method returns the passed drawable vector graphic as bitmap.
     *
     * @param drawableId    Drawable vector graphic to be returned as bitmap.
     * @return              Bitmap generated from the passed vector drawable.
     */
    private Bitmap getBitmap(int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(App.getContext(), drawableId);
        if (drawable == null) {
            return null;
        }
        drawable.setTint(adapter.getContext().getColor(R.color.pv_container));
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
