package de.passwordvault.view.general.dialog_more;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
         * Method is called whenever the action of an item is invoked.
         *
         * @param tag       Tag of the item whose action was invoked.
         * @param position  Position of the item within the adapter.
         */
        void onActionInvoked(String tag, int position);

    }

    /**
     * Class models the view holder for the items to display.
     */
    public static abstract class ItemViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view of the item.
         */
        public final TextView textView;


        /**
         * Constructor instantiates a new view holder for the passed view.
         *
         * @param itemView  Inflated view from which to create the view holder.
         */
        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }

    }

    /**
     * Class models the view holder for the item button to display.
     */
    public static class ItemButtonViewHolder extends ItemViewHolder {

        /**
         * Attribute stores the image view of the item.
         */
        public final ImageView imageView;


        /**
         * Constructor instantiates a new view holder for the passed view.
         *
         * @param itemView  Inflated view from which to create the view holder.
         */
        public ItemButtonViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }

    }

    /**
     * Class models the view holder for the item checkbox to display.
     */
    public static class ItemCheckboxViewHolder extends ItemViewHolder {

        /**
         * Attribute stores the checkbox of the item.
         */
        public final CheckBox checkBox;


        /**
         * Constructor instantiates a new view holder for the passed view.
         *
         * @param itemView  Inflated view from which to create the view holder.
         */
        public ItemCheckboxViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
        }

    }


    /**
     * Field stores the view type for the button item.
     */
    public static final int TYPE_BUTTON = 1;

    /**
     * Field stores the view type for the checkbox item.
     */
    public static final int TYPE_CHECKBOX = 3;


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
        View view;
        switch (viewType) {
            case TYPE_CHECKBOX:
                view = layoutInflater.inflate(R.layout.item_more_checkbox, parent, false);
                return new ItemCheckboxViewHolder(view);
            default:
                view = layoutInflater.inflate(R.layout.item_more_button, parent, false);
                return new ItemButtonViewHolder(view);
        }
    }


    /**
     * Method binds data to a view holder.
     *
     * @param holder    View holder to update.
     * @param position  Position of the view holder in the adapter.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = viewModel.getItems().get(position);
        switch (item.getType()) {
            case Item.TYPE_BUTTON: {
                ItemButton buttonItem = (ItemButton)item;
                ItemButtonViewHolder viewHolder = (ItemButtonViewHolder)holder;
                viewHolder.imageView.setImageResource(buttonItem.getIcon());
                viewHolder.textView.setText(buttonItem.getTitle());
                viewHolder.itemView.setOnClickListener(view -> {
                    dialog.onActionInvoked(buttonItem.getTag(), holder.getAdapterPosition());
                });
                break;
            }
            case Item.TYPE_CHECKBOX: {
                ItemCheckbox checkboxItem = (ItemCheckbox)item;
                ItemCheckboxViewHolder viewHolder = (ItemCheckboxViewHolder)holder;
                viewHolder.textView.setText(checkboxItem.getTitle());
                viewHolder.checkBox.setChecked(checkboxItem.isChecked());
                viewHolder.itemView.setOnClickListener(view -> {
                    viewHolder.checkBox.setChecked(!viewHolder.checkBox.isChecked());
                });
                viewHolder.checkBox.setOnCheckedChangeListener((button, checked) -> {
                    dialog.onActionInvoked(checkboxItem.getTag(), holder.getAdapterPosition());
                });
                break;
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (viewModel.getItems().get(position).getType() == Item.TYPE_BUTTON) {
            return TYPE_BUTTON;
        }
        else {
            return TYPE_CHECKBOX;
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
