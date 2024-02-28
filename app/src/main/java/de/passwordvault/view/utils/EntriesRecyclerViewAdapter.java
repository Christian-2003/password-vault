package de.passwordvault.view.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.entry.EntryAbbreviated;


/**
 * Class implements an adapter for a recycler view which can display abbreviated entries.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class EntriesRecyclerViewAdapter extends RecyclerView.Adapter<EntriesRecyclerViewAdapter.ViewHolder> {

    /**
     * Class implements a view holder for an abbreviated entry.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view which displays the name of the entry.
         */
        public TextView name;

        /**
         * Attribute stores the text view which displays the description of the entry.
         */
        public TextView description;

        /**
         * Attribute stores the view of a list item.
         */
        public View itemView;


        /**
         * Constructor instantiates a new view holder for the passed inflated view.
         *
         * @param itemView  Inflated view for which to create this view holder.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.entries_list_item_name);
            this.description = itemView.findViewById(R.id.entries_list_item_description);
            this.itemView = itemView;
        }

    }


    /**
     * Attribute stores the data that shall be displayed in this adapter.
     */
    private final ArrayList<EntryAbbreviated> data;

    private final OnRecyclerItemClickListener<EntryAbbreviated> clickListener;


    /**
     * Constructor instantiates a new adapter for a recycler view to display abbreviated entries.
     *
     * @param data                  Array list of entries to be displayed.
     * @param clickListener         Item click listener which is called when an item is clicked.
     * @throws NullPointerException The passed array list is {@code null}.
     */
    public  EntriesRecyclerViewAdapter(ArrayList<EntryAbbreviated> data, OnRecyclerItemClickListener<EntryAbbreviated> clickListener) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException();
        }
        this.data = data;
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
        EntryAbbreviated entry = data.get(position);
        holder.name.setText(entry.getName());
        holder.description.setText(entry.getDescription());
        if (clickListener != null) {
            holder.itemView.setOnClickListener(view -> clickListener.onItemClick(entry));
        }
    }


    /**
     * Method returns the number of items that are displayed with the recycler view.
     *
     * @return  Number of abbreviated entries being displayed.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

}
