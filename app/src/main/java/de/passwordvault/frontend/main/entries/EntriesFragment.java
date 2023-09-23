package de.passwordvault.frontend.main.entries;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.backend.Singleton;
import de.passwordvault.backend.entry.AbbreviatedEntry;


/**
 * A simple {@link Fragment} subclass.
 */
public class EntriesFragment extends Fragment {

    public EntriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_entries, container, false);

        //Create names:
        ArrayList<AbbreviatedEntry> entries = Singleton.ENTRIES.getVisibleAbbreviatedEntries();
        String[] names = new String[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            names[i] = entries.get(i).getName();
        }
        ListView listView = inflated.findViewById(R.id.abbreviated_entries);
        ListAdapter listAdapter = new ListAdapter(entries, getContext());
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new ListItemSelectionListener(getActivity()));

        return inflated;
    }

}
