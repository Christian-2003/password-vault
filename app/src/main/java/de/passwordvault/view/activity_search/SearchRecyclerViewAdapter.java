package de.passwordvault.view.activity_search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import de.passwordvault.view.utils.recycler_view.OnRecyclerViewActionListener;
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
            nameTextView = itemView.findViewById(R.id.text_name);
            contentTextView = itemView.findViewById(R.id.text_content);
        }

    }


    /**
     * Field stores the position of the empty placeholder within the adapter.
     */
    public static final int POSITION_EMPTY_PLACEHOLDER = 0;

    /**
     * Field stores the offset with which the search results are displayed.
     */
    public static final int OFFSET_SEARCH_RESULTS = 1;

    /**
     * Field stores the view type for entries.
     */
    private static final int TYPE_ENTRY = 3;

    /**
     * Field stores the view type for details.
     */
    private static final int TYPE_DETAIL = 5;


    /**
     * Attribute stores the action listener to invoke when a search result is clicked.
     */
    @Nullable
    private OnRecyclerViewActionListener searchResultClicked;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the recycler view adapter.
     * @param viewModel View model from which to source data.
     */
    public SearchRecyclerViewAdapter(@NonNull Context context, @NonNull SearchViewModel viewModel) {
        super(context, viewModel);
        searchResultClicked = null;
    }


    /**
     * Method changes the action listener to invoke when a search result is clicked.
     *
     * @param searchResultClicked   New listener to invoke.
     */
    public void setSearchResultClicked(@Nullable OnRecyclerViewActionListener searchResultClicked) {
        this.searchResultClicked = searchResultClicked;
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
        if (position == POSITION_EMPTY_PLACEHOLDER) {
            GenericEmptyPlaceholderViewHolder viewHolder = (GenericEmptyPlaceholderViewHolder)holder;
            if (viewModel.getSearchResults() == null || viewModel.getSearchResults().isEmpty()) {
                viewHolder.itemView.setVisibility(View.VISIBLE);
                viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.el_search));
                viewHolder.headlineTextView.setText(R.string.search_empty_headline);
                viewHolder.supportTextView.setText(R.string.search_empty_support);
            }
            else {
                viewHolder.itemView.setVisibility(View.GONE);
                viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            }
        }
        else if (viewModel.getSearchResults() != null) {
            SearchResult searchResult = viewModel.getSearchResults().get(position - OFFSET_SEARCH_RESULTS);
            switch (searchResult.getType()) {
                case SearchResult.TYPE_ENTRY: {
                    SearchResultEntry result = (SearchResultEntry)searchResult;
                    EntryViewHolder viewHolder = (EntryViewHolder)holder;
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
                    viewHolder.itemView.setOnClickListener(view -> {
                        if (searchResultClicked != null) {
                            searchResultClicked.onAction(holder.getAdapterPosition());
                        }
                    });
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
                    viewHolder.itemView.setOnClickListener(view -> {
                        if (searchResultClicked != null) {
                            searchResultClicked.onAction(holder.getAdapterPosition());
                        }
                    });
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
            case TYPE_GENERIC_EMPTY_PLACEHOLDER: {
                view = layoutInflater.inflate(R.layout.item_generic_empty_placeholder, parent, false);
                return new GenericEmptyPlaceholderViewHolder(view);
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
        if (position == 0) {
            return TYPE_GENERIC_EMPTY_PLACEHOLDER;
        }
        else if (viewModel.getSearchResults() != null) {
            if (viewModel.getSearchResults().get(position - OFFSET_SEARCH_RESULTS).getType() == SearchResult.TYPE_ENTRY) {
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
            return viewModel.getSearchResults().size() + 1;
        }
        return 1;
    }


    /**
     * Method returns the the search result for the specified adapter position. If no search
     * result is available, {@code null} is returned.
     *
     * @param position  Adapter position whose search result to return.
     * @return          Search result for the specified adapter position.
     */
    @Nullable
    public SearchResult getSearchResultForAdapterPosition(int position) {
        if (viewModel.getSearchResults() != null && position >= 0 || position < getItemCount()) {
            return viewModel.getSearchResults().get(position - OFFSET_SEARCH_RESULTS);
        }
        return null;
    }

}
