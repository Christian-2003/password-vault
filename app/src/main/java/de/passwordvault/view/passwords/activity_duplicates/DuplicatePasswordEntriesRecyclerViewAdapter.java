package de.passwordvault.view.passwords.activity_duplicates;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import de.passwordvault.R;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.view.utils.recycler_view.OnRecyclerViewActionListener;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements the recycler view adapter for the {@link DuplicatePasswordEntriesActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class DuplicatePasswordEntriesRecyclerViewAdapter extends RecyclerViewAdapter<DuplicatePasswordEntriesViewModel> {

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
     * Field stores the offset for the list of entries within the adapter.
     */
    public static final int OFFSET_ENTRIES = 0;

    /**
     * Field stores the view type for entries.
     */
    private static final int TYPE_DUPLICATES_ENTRY = 0;


    /**
     * Attribute stores the action listener invoked when an item is clicked.
     */
    @Nullable
    private OnRecyclerViewActionListener itemClickListener;

    /**
     * Attribute stores a list of (filtered) entries to display to the user.
     */
    @NonNull
    private final ArrayList<EntryAbbreviated> filteredEntries;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model for the adapter.
     */
    public DuplicatePasswordEntriesRecyclerViewAdapter(@NonNull Context context, @NonNull DuplicatePasswordEntriesViewModel viewModel) {
        super(context, viewModel);
        filteredEntries = new ArrayList<>(viewModel.getEntries());
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
     * Method creates a new view holder.
     *
     * @param parent    Parent for the item view of the view holder.
     * @param viewType  The view type of the new View.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_entry, parent, false);
        return new EntryViewHolder(view);
    }


    /**
     * Method binds data to the view holder at the specified position.
     *
     * @param holder    View holder to update.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EntryViewHolder viewHolder = (EntryViewHolder)holder;
        EntryAbbreviated entry = filteredEntries.get(position);
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


    /**
     * Method returns the view type for the specified position.
     *
     * @param position  Position to query.
     * @return          View type for the queried position.
     */
    @Override
    public int getItemViewType(int position) {
        return TYPE_DUPLICATES_ENTRY;
    }


    /**
     * Method returns the number of items displayed.
     *
     * @return  Number of items displayed.
     */
    @Override
    public int getItemCount() {
        return filteredEntries.size();
    }


    /**
     * Method resets the filter for the adapter.
     */
    public void resetFilter() {
        filteredEntries.clear();
        filteredEntries.addAll(viewModel.getEntries());
        notifyDataSetChanged();
    }

    /**
     * Method uses the passed search query to filter the displayed entries.
     *
     * @param query Query to use for filtering.
     */
    public void filter(@Nullable String query) {
        if (query == null || query.isEmpty()) {
            resetFilter();
            return;
        }
        filteredEntries.clear();
        query = query.toLowerCase();
        for (EntryAbbreviated entry : viewModel.getEntries()) {
            if (entry.matchesFilter(query)) {
                filteredEntries.add(entry);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Method returns the entry for the passed adapter position.
     *
     * @param position  Position whose entry to return.
     * @return          Entry at the specified position.
     */
    public EntryAbbreviated getEntryForAdapterPosition(int position) {
        return filteredEntries.get(position - OFFSET_ENTRIES);
    }

}
