package de.passwordvault.view.utils.recycler_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.detail.DetailSwipeAction;
import de.passwordvault.model.storage.settings.Config;


/**
 * Class implements a swipe callback for recycler items.
 *
 * @param <T>   Data type for the items displayed in the recycler view.
 * @author      Christian-2003
 * @version     3.6.1
 * @deprecated  Use {@link RecyclerViewSwipeCallback} instead.
 */
public class RecyclerItemSwipeCallback<T> extends ItemTouchHelper.Callback {

    /**
     * Interface can be implemented by a recycler view adapter.
     *
     * @param <T>   Data type for the items displayed in the recycler view.
     */
    public interface ItemSwipeContract<T> {

        /**
         * Method returns the context to use with this callback.
         *
         * @return  Context to use with the callback.
         */
        Context getContext();

        /**
         * Method is called whenever an item is being swiped.
         *
         * @param viewHolder    View holder of the swiped item.
         * @param swipeAction   Swipe action to call when an item was swiped.
         */
        void onSwiped(RecyclerView.ViewHolder viewHolder, OnRecyclerItemClickListener<T> swipeAction);

        /**
         * Method is called to return an item to it's original position after it was swiped left or
         * right. This method is automatically provided by a recycler view adapter and does not need
         * to be overridden manually.
         *
         * @param position  Position of the swiped item.
         */
        void notifyItemChanged(int position);

    }


    /**
     * Class models a swipe action which specifies attributes for item swipes.
     *
     * @param <T>   Data type for the items displayed in the recycler view.
     */
    public static class SwipeAction<T> {

        /**
         * Attribute stores the drawable to be displayed when an item is swiped.
         */
        @DrawableRes
        private final int drawableRes;

        /**
         * Attribute stores the color to be displayed when an item is swiped.
         */
        @ColorRes
        private final int colorRes;

        /**
         * Attribute stores the listener to be called when an item is swiped.
         */
        private final OnRecyclerItemClickListener<T> swipeListener;


        /**
         * Constructor instantiates a new swipe action.
         *
         * @param drawableRes   Drawable to display when the item is swiped.
         * @param colorRes      Color to display when the item is swiped.
         * @param swipeListener Listener for when the item is swiped.
         */
        public SwipeAction(@DrawableRes int drawableRes, @ColorRes int colorRes, @Nullable OnRecyclerItemClickListener<T> swipeListener) {
            this.drawableRes = drawableRes;
            this.colorRes = colorRes;
            this.swipeListener = swipeListener;
        }

        /**
         * Method returns the resource of the drawable to display when the item is swiped.
         *
         * @return  Drawable resource to display when the item is swiped.
         */
        @DrawableRes
        public int getDrawableRes() {
            return drawableRes;
        }

        /**
         * Method returns the resource of the color to display when the item is swiped.
         *
         * @return  Color resource to display when the item is swiped.
         */
        @ColorRes
        public int getColorRes() {
            return colorRes;
        }

        /**
         * Method returns the swipe listener to call when an item is swiped.
         *
         * @return  Swipe listener to call when an item is swiped.
         */
        public OnRecyclerItemClickListener<T> getSwipeListener() {
            return swipeListener;
        }

    }


    /**
     * Attribute stores the recycler view adapter.
     */
    private final ItemSwipeContract<T> adapter;

    /**
     * Attribute stores the left swipe action.
     */
    @Nullable
    private final SwipeAction<T> leftSwipeAction;

    /**
     * Attribute stores the right swipe action.
     */
    @Nullable
    private final SwipeAction<T> rightSwipeAction;


    /**
     * Constructor instantiates a new callback for the passed recycler view adapter.
     *
     * @param adapter               Adapter for the callback.
     * @param leftSwipeAction       Action for when an item is left swiped.
     * @param rightSwipeAction      Action for when an item is right swiped.
     * @throws NullPointerException The passed adapter is {@code null}.
     */
    public RecyclerItemSwipeCallback(ItemSwipeContract<T> adapter, @Nullable SwipeAction<T> leftSwipeAction, @Nullable SwipeAction<T> rightSwipeAction) throws NullPointerException {
        if (adapter == null) {
            throw new NullPointerException();
        }
        this.adapter = adapter;
        this.leftSwipeAction = leftSwipeAction;
        this.rightSwipeAction = rightSwipeAction;
    }


    /**
     * Method defines whether item swipe is enabled.
     *
     * @return  Whether item swipe is enabled.
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return leftSwipeAction != null || rightSwipeAction != null;
    }


    /**
     * Method is called when an item is swiped.
     *
     * @param viewHolder    The ViewHolder which has been swiped by the user.
     * @param direction     The direction to which the ViewHolder is swiped.
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT && leftSwipeAction != null && leftSwipeAction.getSwipeListener() != null) {
            adapter.onSwiped(viewHolder, leftSwipeAction.getSwipeListener());
            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
        }
        else if (direction == ItemTouchHelper.RIGHT && rightSwipeAction != null && rightSwipeAction.getSwipeListener() != null) {
            adapter.onSwiped(viewHolder, rightSwipeAction.getSwipeListener());
            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
        }
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
        return true; //Class is not intended to cover move events...
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
        if (leftSwipeAction != null && rightSwipeAction != null) {
            swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        }
        else if (leftSwipeAction != null) {
            swipeFlags = ItemTouchHelper.LEFT;
        }
        else if (rightSwipeAction != null) {
            swipeFlags = ItemTouchHelper.RIGHT;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
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
            int iconOffset = adapter.getContext().getResources().getDimensionPixelSize(R.dimen.space_horizontal);
            if (dX > 1 && rightSwipeAction != null) {
                //Right swipe:
                paint.setColor(adapter.getContext().getColor(rightSwipeAction.getColorRes()));
                icon = getBitmap(rightSwipeAction.getDrawableRes());
                canvas.drawRect((float)itemView.getLeft(), (float)itemView.getTop(), dX, (float)itemView.getBottom(), paint);
                if (icon != null) {
                    canvas.drawBitmap(icon, (float) itemView.getLeft() + iconOffset, (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2, paint);
                }
            }
            else if (dX < -1 && leftSwipeAction != null) {
                //Left swipe:
                paint.setColor(adapter.getContext().getColor(leftSwipeAction.getColorRes()));
                icon = getBitmap(leftSwipeAction.getDrawableRes());
                canvas.drawRect((float)itemView.getRight() + dX, (float)itemView.getTop(), (float)itemView.getRight(), (float)itemView.getBottom(), paint);
                if (icon != null) {
                    canvas.drawBitmap(icon, (float)itemView.getRight() - iconOffset - icon.getWidth(), (float)itemView.getTop() + ((float)itemView.getBottom() - (float)itemView.getTop() - icon.getHeight()) / 2, paint);
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
    private Bitmap getBitmap(@DrawableRes int drawableId) {
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


    /**
     * Method makes the left swipe action (either to delete or edit) based on the configuration from
     * {@link Config}.
     *
     * @param editListener      Listener to call when the item is edited.
     * @param deleteListener    Listener to call when the item is deleted.
     * @return                  Swipe action for when an item is swiped left.
     * @param <T>               Generic type for the swipe action.
     */
    public static<T> RecyclerItemSwipeCallback.SwipeAction<T> makeLeftSwipeAction(OnRecyclerItemClickListener<T> editListener, OnRecyclerItemClickListener<T> deleteListener) {
        RecyclerItemSwipeCallback.SwipeAction<T> swipeAction = null;
        DetailSwipeAction action = Config.getInstance().leftSwipeAction.get();
        if (action == DetailSwipeAction.DELETE) {
            swipeAction = new RecyclerItemSwipeCallback.SwipeAction<>(R.drawable.ic_delete, R.color.text_critical, deleteListener);
        }
        else if (action == DetailSwipeAction.EDIT) {
            swipeAction = new RecyclerItemSwipeCallback.SwipeAction<>(R.drawable.ic_edit, R.color.pv_primary, editListener);
        }
        return swipeAction;
    }


    /**
     * Method makes the right swipe action (either to delete or edit) based on the configuration from
     * {@link Config}.
     *
     * @param editListener      Listener to call when the item is edited.
     * @param deleteListener    Listener to call when the item is deleted.
     * @return                  Swipe action for when an item is swiped left.
     * @param <T>               Generic type for the swipe action.
     */
    public static<T> RecyclerItemSwipeCallback.SwipeAction<T> makeRightSwipeAction(OnRecyclerItemClickListener<T> editListener, OnRecyclerItemClickListener<T> deleteListener) {
        RecyclerItemSwipeCallback.SwipeAction<T> swipeAction = null;
        DetailSwipeAction action = Config.getInstance().rightSwipeAction.get();
        if (action == DetailSwipeAction.DELETE) {
            swipeAction = new RecyclerItemSwipeCallback.SwipeAction<>(R.drawable.ic_delete, R.color.text_critical, deleteListener);
        }
        else if (action == DetailSwipeAction.EDIT) {
            swipeAction = new RecyclerItemSwipeCallback.SwipeAction<>(R.drawable.ic_edit, R.color.pv_primary, editListener);
        }
        return swipeAction;
    }

}
