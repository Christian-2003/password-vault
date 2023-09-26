package de.passwordvault.frontend.main.entries;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.backend.Singleton;
import de.passwordvault.backend.entry.AbbreviatedEntry;
import de.passwordvault.backend.entry.Entry;
import de.passwordvault.frontend.entry.EntryActivity;


/**
 * Class implements the {@linkplain Fragment} that displays a list of {@linkplain Entry} instances
 * within the {@linkplain de.passwordvault.frontend.main.MainActivity}.
 *
 * @author  Christian-2003
 * @version 1.0.1
 */
public class EntriesFragment extends Fragment implements AdapterView.OnItemClickListener {

    /**
     * Attribute stores the inflated view for the fragment.
     */
    private View inflated;


    /**
     * Default constructor instantiates a new EntriesFragment.
     */
    public EntriesFragment() {
        // Required empty public constructor
    }


    /**
     * Method is called whenever the EntriesFragment is created or recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Method is called whenever the {@linkplain View} for the EntriesFragment is created.
     *
     * @param inflater              LayoutInflater for the fragment.
     * @param container             Container which contains the fragment.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Generated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflated = inflater.inflate(R.layout.fragment_entries, container, false);

        populateListView();

        return inflated;
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
            Intent intent = new Intent(getActivity(), EntryActivity.class);
            intent.putExtra("uuid", uuid);
            getActivity().startActivityForResult(intent, 1);
        }
    }


    /**
     * Method is called whenever any {@linkplain android.app.Activity} that was started by this
     * Fragment through {@linkplain #startActivityForResult(Intent, int, Bundle)} has finished.
     * This is done to redraw this Fragment with an edited / updated {@linkplain Entry}-list.
     *
     * @param requestCode   Code that was requested from the started Activity.
     * @param resultCode    Result code from the started Activity.
     * @param data          Supplied data.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            //Update ListView:
            populateListView();
        }
    }


    /**
     * Method populates the {@linkplain ListView} that displays all {@linkplain de.passwordvault.backend.entry.Entry}
     * instances.
     */
    private void populateListView() {
        ArrayList<AbbreviatedEntry> entries = Singleton.ENTRIES.getVisibleAbbreviatedEntries();
        ListView listView = inflated.findViewById(R.id.abbreviated_entries);
        ListAdapter listAdapter = new ListAdapter(entries, getContext());
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
    }

}
