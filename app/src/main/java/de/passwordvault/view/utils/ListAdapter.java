package de.passwordvault.view.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.entry.Entry;
import de.passwordvault.view.fragments.EntriesFragment;


/**
 * Class models a custom {@linkplain ArrayAdapter} for {@linkplain Entry} instances which
 * are displayed through a {@linkplain android.widget.ListView} within the {@linkplain EntriesFragment}.
 *
 * @author  Christian-2003
 * @version 2.2.0
 */
public class ListAdapter extends BaseAdapter implements Filterable {

    /**
     * Inner class models a {@link ViewHolder} for the generated {@linkplain View}-instances of
     * the {@link ListAdapter}.
     */
    private static class ViewHolder {

        /**
         * Attribute stores the {@link TextView} to display the name.
         */
        public TextView name;

        /**
         * Attribute stores the {@link TextView} to display the description.
         */
        public TextView description;

    }


    /**
     * Attribute stores the {@link Entry}-instances that shall be displayed by the {@link ListAdapter}.
     */
    private ArrayList<Entry> originalEntries;

    /**
     * Attribute stores the {@link Entry}-instances that are displayed by the {@link ListAdapter}.
     */
    private ArrayList<Entry> displayedEntries;

    /**
     * Attribute stores the layout inflater to create the views.
     */
    private final LayoutInflater inflater;


    /**
     * Constructor instantiates a new ListAdapter with the specified arguments.
     *
     * @param entries   Entries to be displayed with the ListView.
     * @param context   Context for the adapter.
     */
    public ListAdapter(ArrayList<Entry> entries, Context context) {
        originalEntries = entries;
        displayedEntries = entries;
        inflater = LayoutInflater.from(context);
    }


    /**
     * Method returns the number of displayed entries.
     *
     * @return  Number of displayed entries.
     */
    @Override
    public int getCount() {
        return displayedEntries.size();
    }


    /**
     * Method returns the item at the specified position.
     *
     * @param position  Index of the item to be returned.
     * @return          Item at the specified index.
     */
    @Override
    public Object getItem(int position) {
        return displayedEntries.get(position);
    }


    /**
     * Method returns the ID of the item at the specified position.
     *
     * @param position  Position whose item ID shall be returned.
     * @return          ID of the item at the specified ID.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * Method generates a {@linkplain View} for the {@linkplain android.widget.ListView}.
     *
     * @param position      Position of the item whose View shall be created.
     * @param convertView   Previous view which shall be replaced.
     * @param parent        Parent of the view.
     * @return              Generated view.
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Entry entry = (Entry)getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.entries_list_item, parent, false);
            holder.name = convertView.findViewById(R.id.entries_list_item_name);
            holder.description = convertView.findViewById(R.id.entries_list_item_description);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.name.setText(entry.getName());
        holder.description.setText(entry.getDescription());
        return convertView;
    }


    /**
     * Method returns the {@link Filter} that is used with the {@link ListAdapter}.
     *
     * @return  Used filter.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {

            /**
             * Method filters the data according to the passed constraint.
             *
             * @param constraint    The constraint used to filter the data.
             * @return              The result of the filtering operation.
             */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Entry> filteredList = new ArrayList<>();
                if (originalEntries == null) {
                    originalEntries = new ArrayList<>(displayedEntries);
                }
                if (constraint == null || constraint.length() == 0) {
                    //No filter applied: Set original values as result:
                    results.count = originalEntries.size();
                    results.values = originalEntries;
                }
                else {
                    constraint = constraint.toString().toLowerCase();
                    for (Entry entry : originalEntries) {
                        if (entry.matchesFilter(constraint)) {
                            filteredList.add(new Entry(entry));
                        }
                    }
                    results.count = filteredList.size();
                    results.values = filteredList;
                }
                return results;
            }

            /**
             * Method applies the filter results to the {@link ListAdapter} and calls
             * {@linkplain ListAdapter#notifyDataSetChanged()} to update the {@linkplain android.widget.ListView}.
             *
             * @param constraint    The constraint used to filter the data.
             * @param results       The result of the filtering operation.
             */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                displayedEntries = (ArrayList<Entry>)results.values;
                notifyDataSetChanged();
            }

        };
    }

}
