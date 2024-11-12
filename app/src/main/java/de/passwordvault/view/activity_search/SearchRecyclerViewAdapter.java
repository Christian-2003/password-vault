package de.passwordvault.view.activity_search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import de.passwordvault.R;
import de.passwordvault.model.search.SearchResult;
import de.passwordvault.model.search.SearchResultDetail;
import de.passwordvault.model.search.SearchResultEntry;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements the recycler view adapter for the {@link SearchActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class SearchRecyclerViewAdapter extends RecyclerViewAdapter<SearchViewModel> {

    /**
     * Class implements the view holder displaying an {@link de.passwordvault.model.search.SearchResultEntry}.
     */
    public static class EntryViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the image view displaying the app logo.
         */
        public final ImageView logoImageView;

        /**
         * Attribute stores the text view displaying the abbreviation for the entry name.
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
         * Attribute stores the container displaying the tags of the entry.
         */
        public final ChipGroup tagsContainer;


        /**
         * Constructor instantiates a new view holder for the specified view.
         *
         * @param itemView  Inflated view for which to create the view holder.
         */
        public EntryViewHolder(View itemView) {
            super(itemView);
            logoImageView = itemView.findViewById(R.id.image_logo);
            abbreviationTextView = itemView.findViewById(R.id.text_abbreviation);
            nameTextView = itemView.findViewById(R.id.text_name);
            descriptionTextView = itemView.findViewById(R.id.text_description);
            tagsContainer = itemView.findViewById(R.id.container_tags);
        }

    }

    /**
     * Class implements the view holder displaying an {@link de.passwordvault.model.search.SearchResultDetail}.
     */
    public static class DetailViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the image view displaying the image for the detail.
         */
        public final ImageView detailImageView;

        /**
         * Attribute stores the text view displaying the name of the detail.
         */
        public final TextView nameTextView;

        /**
         * Attribute stores the text view displaying the content of the detail.
         */
        public final TextView contentTextView;


        /**
         * Constructor instantiates a new view holder for the specified view.
         *
         * @param itemView  Inflated view for which to create the view holder.
         */
        public DetailViewHolder(View itemView) {
            super(itemView);
            detailImageView = itemView.findViewById(R.id.image_detail);
            nameTextView = itemView.findViewById(R.id.text_name);
            contentTextView = itemView.findViewById(R.id.text_content);
        }

    }


    /**
     * Field stores the offset with which the search results are displayed.
     */
    public static final int OFFSET_SEARCH_RESULTS = 0;

    /**
     * Field stores the view type for entries.
     */
    private static final int TYPE_ENTRY = 3;

    /**
     * Field stores the view type for details.
     */
    private static final int TYPE_DETAIL = 5;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the recycler view adapter.
     * @param viewModel View model from which to source data.
     */
    public SearchRecyclerViewAdapter(@NonNull Context context, @NonNull SearchViewModel viewModel) {
        super(context, viewModel);
    }


    /**
     * Method updates the specified view holder with data.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "Bind vh for pos " + position);
        if (viewModel.getSearchResults() != null) {
            SearchResult searchResult = viewModel.getSearchResults().get(position - OFFSET_SEARCH_RESULTS);
            Log.d(TAG, "Search result type " + searchResult.getType());
            switch (searchResult.getType()) {
                case SearchResult.TYPE_ENTRY: {
                    SearchResultEntry result = (SearchResultEntry)searchResult;
                    EntryViewHolder viewHolder = (EntryViewHolder)holder;
                    Log.d(TAG, "Bind vh for entry " + result.getEntry().getName());
                    viewHolder.nameTextView.setText(result.getEntry().getName());
                    viewHolder.descriptionTextView.setText(result.getEntry().getDescription());
                    Drawable logo = result.getEntry().getLogo();
                    if (logo == null) {
                        viewHolder.logoImageView.setVisibility(View.GONE);
                        viewHolder.abbreviationTextView.setVisibility(View.VISIBLE);
                        viewHolder.abbreviationTextView.setText("" + result.getEntry().getName().charAt(0));
                    }
                    else {
                        viewHolder.logoImageView.setVisibility(View.VISIBLE);
                        viewHolder.logoImageView.setImageDrawable(logo);
                        viewHolder.abbreviationTextView.setVisibility(View.GONE);
                    }
                    viewHolder.tagsContainer.removeAllViews();
                    for (Tag tag : result.getMatchingTags()) {
                        Chip chip = new Chip(context);
                        chip.setText(tag.getName());
                        viewHolder.tagsContainer.addView(chip);
                    }
                    break;
                }
                case SearchResult.TYPE_DETAIL: {
                    SearchResultDetail result = (SearchResultDetail)searchResult;
                    DetailViewHolder viewHolder = (DetailViewHolder)holder;
                    viewHolder.nameTextView.setText(result.getDetail().getName());
                    if (result.getDetail().isObfuscated()) {
                        viewHolder.contentTextView.setText(Utils.obfuscate(result.getDetail().getContent()));
                    }
                    else {
                        viewHolder.contentTextView.setText(result.getDetail().getContent());
                    }
                    viewHolder.detailImageView.setImageDrawable(ContextCompat.getDrawable(context, result.getDetail().getType().getDrawable()));
                    break;
                }
            }
        }
    }


    /**
     * Method creates a new view holder for the specified view type.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return         View holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_ENTRY: {
                view = layoutInflater.inflate(R.layout.item_search_entry, parent, false);
                return new EntryViewHolder(view);
            }
            case TYPE_DETAIL: {
                view = layoutInflater.inflate(R.layout.item_search_detail, parent, false);
                return new DetailViewHolder(view);
            }
            default: {
                return null;
            }
        }
    }


    /**
     * Method returns the view type for the item at the specified position.
     *
     * @param position  Position to query
     * @return          View type for the queried position.
     */
    @Override
    public int getItemViewType(int position) {
        if (viewModel.getSearchResults() != null) {
            if (viewModel.getSearchResults().get(position).getType() == SearchResult.TYPE_ENTRY) {
                return TYPE_ENTRY;
            }
            else {
                return TYPE_DETAIL;
            }
        }
        else {
            return super.getItemViewType(position);
        }
    }

    /**
     * Method returns the number of items within the adapter.
     *
     * @return  Number of items within the adapter.
     */
    @Override
    public int getItemCount() {
        if (viewModel.getSearchResults() != null) {
            return viewModel.getSearchResults().size();
        }
        return 0;
    }

}
