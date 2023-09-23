package de.passwordvault.frontend.main.entries;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import de.passwordvault.R;
import de.passwordvault.backend.Singleton;
import de.passwordvault.backend.entry.AbbreviatedEntry;
import de.passwordvault.frontend.entry.EntryActivity;


/**
 * Class models a custom {@linkplain ArrayAdapter} for {@linkplain AbbreviatedEntry} instances which
 * are displayed through a {@linkplain android.widget.ListView} within the {@linkplain EntriesFragment}.
 *
 * @author  Christian-2003
 * @version 1.0.0
 */
// implements AdapterView.OnItemClickListener
public class ListAdapter extends ArrayAdapter<AbbreviatedEntry> {

    /**
     * Attribute stores the context for the adapter.
     */
    private final Context context;

    /**
     * Stores the position of the list item that was last generated.
     */
    private int lastPosition;


    /**
     * Constructor instantiates a new ListAdapter with the specified arguments.
     *
     * @param entries   AbbreviatedEntries to be displayed with the ListView.
     * @param context   Context for the adapter.
     */
    public ListAdapter(ArrayList<AbbreviatedEntry> entries, Context context) {
        super(context, R.layout.entries_list_item, entries);
        this.context = context;
        lastPosition = -1;
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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AbbreviatedEntry entry = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.entries_list_item, parent, false);
        ((TextView)convertView.findViewById(R.id.entries_list_item_name)).setText(entry.getName());
        ((TextView)convertView.findViewById(R.id.entries_list_item_description)).setText(entry.getDescription());
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.entries_list_show_item);
        convertView.startAnimation(animation);
        lastPosition = position;
        return convertView;
    }

}
