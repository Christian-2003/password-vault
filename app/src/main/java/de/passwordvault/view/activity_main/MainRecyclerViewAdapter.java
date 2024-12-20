package de.passwordvault.view.activity_main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.view.utils.recycler_view.OnRecyclerViewActionListener;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements the recycler view adapter for {@link MainActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class MainRecyclerViewAdapter extends RecyclerViewAdapter<MainViewModel> {

    /**
     * Class models the view holder for the entries displayed on this page.
     */
    public static class EntryViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the image view displaying the app icon of the entry.
         */
        public final ImageView logoImageView;

        /**
         * Attribute stores the text view displaying the abbreviation for the entry in case no
         * app icon is available.
         */
        public final TextView abbreviationTextView;

        /**
         * Attribute stores the text view displaying the name of the entry.
         */
        public final TextView nameTextView;

        /**
         * Attribute stores the text view displaying the description of the entry.
         */
        public final TextView descriptionTextView;


        /**
         * Constructor instantiates a new view holder for the passed view.
         *
         * @param itemView  Inflated view for which to create the view holder.
         */
        public EntryViewHolder(View itemView) {
            super(itemView);
            logoImageView = itemView.findViewById(R.id.image_logo);
            abbreviationTextView = itemView.findViewById(R.id.text_abbreviation);
            nameTextView = itemView.findViewById(R.id.text_name);
            descriptionTextView = itemView.findViewById(R.id.text_description);
        }

    }

    /**
     * Field stores the position of the warning displayed if a new app version is available.
     */
    public static final int POSITION_UPDATE_INFO = 0;

    /**
     * Field stores the position of the empty placeholder within the adapter.
     */
    public static final int POSITION_EMPTY_PLACEHOLDER = 1;

    /**
     * Field stores the offset for the list of entries within the adapter.
     */
    public static final int OFFSET_ENTRIES = 2;

    /**
     * Field stores the view type for entries.
     */
    private static final int TYPE_ENTRY = 0;


    /**
     * Attribute stores the action listener invoked when an item is clicked.
     */
    @Nullable
    private OnRecyclerViewActionListener itemClickListener;

    /**
     * Attribute stores the action listener invoked when the user clicks to update the app.
     */
    @Nullable
    private OnRecyclerViewActionListener updateClickListener;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model for the adapter.
     */
    public MainRecyclerViewAdapter(@NonNull Context context, @NonNull MainViewModel viewModel) {
        super(context, viewModel);
        itemClickListener = null;
        updateClickListener = null;
    }


    /**
     * Method changes the click listener invoked when an item is clicked.
     *
     * @param itemClickListener New listener.
     */
    public void setItemClickListener(@Nullable OnRecyclerViewActionListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * Method changes the click listener invoked when the user clicks to update the app.
     *
     * @param updateClickListener   New listener.
     */
    public void setUpdateClickListener(@Nullable OnRecyclerViewActionListener updateClickListener) {
        this.updateClickListener = updateClickListener;
    }


    /**
     * Method creates a new view holder.
     *
     * @param parent    Parent for the item view of the view holder.
     * @param viewType  The view type of the new View.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ENTRY) {
            View view = layoutInflater.inflate(R.layout.item_entry, parent, false);
            return new EntryViewHolder(view);
        }
        else if (viewType == TYPE_GENERIC_WARNING) {
            View view = layoutInflater.inflate(R.layout.item_generic_warning, parent, false);
            return new GenericWarningViewHolder(view);
        }
        else {
            View view = layoutInflater.inflate(R.layout.item_generic_empty_placeholder, parent, false);
            return new GenericEmptyPlaceholderViewHolder(view);
        }
    }


    /**
     * Method binds data to the view holder at the specified position.
     *
     * @param holder    View holder to update.
     * @param position  The position of the item within the adapter's data set.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EntryViewHolder && viewModel.getAllEntries() != null) {
            EntryViewHolder viewHolder = (EntryViewHolder)holder;
            EntryAbbreviated entry = viewModel.getAllEntries().get(position - OFFSET_ENTRIES);
            Drawable logo = entry.getLogo();
            viewHolder.logoImageView.setImageDrawable(logo);
            if (logo == null) {
                viewHolder.abbreviationTextView.setText("" + entry.getName().charAt(0));
            }
            viewHolder.abbreviationTextView.setVisibility(logo == null ? View.VISIBLE : View.GONE);
            viewHolder.nameTextView.setText(entry.getName());
            viewHolder.descriptionTextView.setText(entry.getDescription());
            viewHolder.itemView.setOnClickListener(view -> {
                if (itemClickListener != null) {
                    itemClickListener.onAction(viewHolder.getAdapterPosition());
                }
            });
        }
        else if (holder instanceof GenericEmptyPlaceholderViewHolder && viewModel.getAllEntries() != null) {
            GenericEmptyPlaceholderViewHolder viewHolder = (GenericEmptyPlaceholderViewHolder)holder;
            if (!viewModel.getAllEntries().isEmpty()) {
                viewHolder.itemView.setVisibility(View.GONE);
                viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            }
            else {
                viewHolder.itemView.setVisibility(View.VISIBLE);
                viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                viewHolder.headlineTextView.setText(context.getString(R.string.entries_empty_headline));
                viewHolder.supportTextView.setText(context.getString(R.string.entries_empty_support));
                viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.el_entries));
            }
        }
        else if (holder instanceof GenericWarningViewHolder) {
            GenericWarningViewHolder viewHolder = (GenericWarningViewHolder)holder;
            if (viewModel.isUpdateAvailable() && !viewModel.isDownloadWarningDismissed()) {
                //Warning is visible:
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int horizontalMargin = context.getResources().getDimensionPixelSize(R.dimen.space_horizontal);
                int verticalMargin = context.getResources().getDimensionPixelSize(R.dimen.space_vertical);
                params.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
                viewHolder.itemView.setLayoutParams(params);
                viewHolder.warningTextView.setText(R.string.warning_new_app_version);
                viewHolder.cancelButton.setText(R.string.button_later);
                viewHolder.cancelButton.setOnClickListener(view -> {
                    viewModel.dismissDownloadWarning();
                    notifyItemChanged(POSITION_UPDATE_INFO);
                });
                viewHolder.confirmButton.setText(R.string.button_download);
                viewHolder.confirmButton.setOnClickListener(view -> {
                    if (updateClickListener != null) {
                        updateClickListener.onAction(holder.getAdapterPosition());
                    }
                });
                viewHolder.itemView.setVisibility(View.VISIBLE);
            }
            else {
                //Warning is invisible:
                viewHolder.itemView.setVisibility(View.GONE);
                viewHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            }
        }
    }


    /**
     * Method returns the view type for the specified position.
     *
     * @param position  Position to query.
     * @return          View type for the queried position.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == POSITION_EMPTY_PLACEHOLDER) {
            return TYPE_GENERIC_EMPTY_PLACEHOLDER;
        }
        else if (position == POSITION_UPDATE_INFO) {
            return TYPE_GENERIC_WARNING;
        }
        return TYPE_ENTRY;
    }


    /**
     * Method returns the number of items displayed.
     *
     * @return  Number of items displayed.
     */
    @Override
    public int getItemCount() {
        int count = 2;
        if (viewModel.getAllEntries() != null) {
            count += viewModel.getAllEntries().size();
        }
        return count;
    }


    /**
     * Method returns the entry for the specified adapter position. If no entry is available for the
     * adapter position, {@code null} is returned.
     *
     * @param position  Adapter position whose entry to return.
     * @return          Entry for the specified adapter position.
     */
    @Nullable
    public EntryAbbreviated getEntryForAdapterPosition(int position) {
        if (viewModel.getAllEntries() != null) {
            if (position - OFFSET_ENTRIES >= 0 && position - OFFSET_ENTRIES < viewModel.getAllEntries().size()) {
                return viewModel.getAllEntries().get(position - OFFSET_ENTRIES);
            }
        }
        return null;
    }

}
