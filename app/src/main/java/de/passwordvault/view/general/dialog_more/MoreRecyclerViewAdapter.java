package de.passwordvault.view.general.dialog_more;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements a recycler view adapter for the items displayed in the {@link MoreDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class MoreRecyclerViewAdapter extends RecyclerViewAdapter<MoreViewModel> {

    /**
     * Interface models a contract that needs to be implemented by the dialog.
     */
    public interface DialogContract {

        /**
         * Method is called whenever an item is clicked.
         *
         * @param view  View of the item that was clicked.
         * @param item  Item that was clicked.
         */
        void onItemClicked(View view, MoreDialog.Item item);

    }

    /**
     * Class models the view holder for the items to display.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view of the item.
         */
        public final TextView textView;

        /**
         * Attribute stores the image view of the item.
         */
        public final ImageView imageView;


        /**
         * Constructor instantiates a new view holder for the passed view.
         *
         * @param itemView  Inflated view from which to create the view holder.
         */
        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
            imageView = itemView.findViewById(R.id.image);
        }

    }


    /**
     * Attribute stores the dialog in which this adapter is used.
     */
    @NonNull
    private final DialogContract dialog;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model for the adapter.
     * @param dialog    Dialog in which the adapter is used.
     */
    public MoreRecyclerViewAdapter(@NonNull Context context, @NonNull MoreViewModel viewModel, @NonNull DialogContract dialog) {
        super(context, viewModel);
        this.dialog = dialog;
    }


    /**
     * Method creates a new view holder.
     *
     * @param parent    Parent for the view within the view holder.
     * @param viewType  The type of view.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_more_button, parent, false);
        return new ItemViewHolder(view);
    }


    /**
     * Method binds data to a view holder.
     *
     * @param holder    View holder to update.
     * @param position  Position of the view holder in the adapter.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder viewHolder = (ItemViewHolder)holder;
            MoreDialog.Item item = viewModel.getItems().get(position);
            viewHolder.imageView.setImageResource(item.getIcon());
            viewHolder.textView.setText(item.getTitle());
            viewHolder.itemView.setOnClickListener(view -> {
                dialog.onItemClicked(view, item);
            });
        }
    }


    /**
     * Method returns the number of items to display.
     *
     * @return  Number of items to display
     */
    @Override
    public int getItemCount() {
        return viewModel.getItems().size();
    }

}
