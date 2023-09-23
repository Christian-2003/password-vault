package de.passwordvault.frontend.main.entries;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import androidx.fragment.app.FragmentActivity;

import de.passwordvault.backend.Singleton;
import de.passwordvault.frontend.entry.EntryActivity;


/**
 * Class implements an {@linkplain AdapterView.OnItemSelectedListener} reacts whenever an entry
 * within a list of entries is selected. When an entry was selected, the respective entry displayed
 * within an instance of {@linkplain de.passwordvault.frontend.entry.EntryActivity}.
 *
 * @author  Christian-2003
 * @version 1.0.0
 */
public class ListItemSelectionListener implements AdapterView.OnItemClickListener {

    /**
     * Attribute stores the context in which the Listener is employed.
     */
    private FragmentActivity context;


    /**
     * Constructor instantiates a new ListItemSelectionListener which operates within the specified
     * context.
     *
     * @param context   Context for the listener.
     */
    public ListItemSelectionListener(FragmentActivity context) {
        super();
        this.context = context;
    }

    /**
     * Method is called when an item within the {@linkplain android.widget.ListView} is selected.
     *
     * @param parent    AdapterView that contains the selected item.
     * @param view      The view within the AdapterView that was selected.
     * @param position  The position of the view within the adapter.
     * @param id        The row ID of the item that was selected.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String uuid = Singleton.ENTRIES.getUuidFromIndex((int)id);
        if (uuid != null) {
            Intent intent = new Intent(context, EntryActivity.class);
            intent.putExtra("uuid", uuid);
            context.startActivity(intent);
        }
    }

}
