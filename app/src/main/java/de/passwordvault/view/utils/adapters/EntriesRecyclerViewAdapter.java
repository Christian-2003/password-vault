package de.passwordvault.view.utils.adapters;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;


/**
 * Class implements an adapter for a recycler view which can display abbreviated entries.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class EntriesRecyclerViewAdapter extends RecyclerView.Adapter<EntriesRecyclerViewAdapter.ViewHolder> implements Filterable {

    /**
     * Class implements a view holder for an abbreviated entry.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view which displays the name of the entry.
         */
        public final TextView name;

        /**
         * Attribute stores the text view which displays the description of the entry.
         */
        public final TextView description;

        /**
         * Attribute stores the view of a list item.
         */
        public final View itemView;

        /**
         * Attribute stores the image view displaying the logo.
         */
        public final ShapeableImageView logo;


        /**
         * Constructor instantiates a new view holder for the passed inflated view.
         *
         * @param itemView  Inflated view for which to create this view holder.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.entries_list_item_name);
            this.description = itemView.findViewById(R.id.entries_list_item_description);
            this.logo = itemView.findViewById(R.id.entries_list_item_logo);
            this.itemView = itemView;
        }

    }


    /**
     * Attribute stores the data that shall be displayed in this adapter.
     */
    private final ArrayList<EntryAbbreviated> data;

    /**
     * Attribute stores the filtered data which is being displayed.
     */
    private ArrayList<EntryAbbreviated> filteredData;

    /**
     * Attribute stores the click listener that is called when an item is clicked.
     */
    private final OnRecyclerItemClickListener<EntryAbbreviated> clickListener;


    /**
     * Constructor instantiates a new adapter for a recycler view to display abbreviated entries.
     *
     * @param data                  Array list of entries to be displayed.
     * @param clickListener         Item click listener which is called when an item is clicked.
     * @throws NullPointerException The passed array list is {@code null}.
     */
    public EntriesRecyclerViewAdapter(ArrayList<EntryAbbreviated> data, OnRecyclerItemClickListener<EntryAbbreviated> clickListener) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException();
        }
        this.data = data;
        filteredData = data;
        this.clickListener = clickListener;
    }


    /**
     * Method creates a new view holder for a recycler view item.
     *
     * @param parent    The ViewGroup into which the new View will be added after it is bound to
     *                  an adapter position.
     * @param viewType  The view type of the new View.
     * @return          Generated view holder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_entries, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Method binds the data of an abbreviated entry to a view holder.
     *
     * @param holder    The ViewHolder which should be updated to represent the contents of the
     *                  item at the given position in the data set.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EntryAbbreviated entry = filteredData.get(position);
        holder.name.setText(entry.getName());
        holder.description.setText(entry.getDescription());
        if (clickListener != null) {
            holder.itemView.setOnClickListener(view -> clickListener.onItemClick(entry, position));
        }
        holder.logo.setImageDrawable(entry.getLogo());
        Log.d("ERVA", "Bound ViewHolder" + position + " for " + entry.getName());
    }


    /**
     * Method returns the number of items that are displayed with the recycler view.
     *
     * @return  Number of abbreviated entries being displayed.
     */
    @Override
    public int getItemCount() {
        return filteredData.size();
    }


    /**
     * Method returns a filter that can be used to filter the data that is being displayed by the
     * recycler view.
     *
     * @return  Filter for filtering the displayed data.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {

            /**
             * Method filters {@link EntriesRecyclerViewAdapter#data} according to the specified
             * char sequence.
             *
             * @param s Filter to be applied to name or description of the entries.
             * @return  Generated filter results.
             */
            @Override
            protected FilterResults performFiltering(CharSequence s) {
                FilterResults results = new FilterResults();
                if (s == null || s.length() == 0) {
                    results.count = data.size();
                    results.values = data;
                }
                else {
                    ArrayList<EntryAbbreviated> filteredData = new ArrayList<>();
                    s = s.toString().toLowerCase();
                    for (EntryAbbreviated entry : data) {
                        if (entry.matchesFilter(s)) {
                            filteredData.add(entry);
                        }
                    }
                    results.count = filteredData.size();
                    results.values = filteredData;
                }
                return results;
            }

            /**
             * Method applies the passed filter results to the recycler view.
             *
             * @param s             Filter which was applied to name and description of the entries.
             * @param filterResults Filter results generated while filtering.
             */
            @Override
            protected void publishResults(CharSequence s, FilterResults filterResults) {
                EntriesRecyclerViewAdapter.this.filteredData = (ArrayList<EntryAbbreviated>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
