package de.passwordvault.frontend.main.entries;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import de.passwordvault.R;
import de.passwordvault.backend.Singleton;
import de.passwordvault.backend.entry.Entry;
import de.passwordvault.frontend.entry.EntryActivity;


/**
 * Class implements the {@linkplain Fragment} that displays a list of {@linkplain Entry} instances
 * within the {@linkplain de.passwordvault.frontend.main.MainActivity}.
 *
 * @author  Christian-2003
 * @version 2.2.0
 */
public class EntriesFragment extends Fragment implements AdapterView.OnItemClickListener, PopupMenu.OnMenuItemClickListener {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} for this fragment.
     */
    private EntriesViewModel viewModel;

    /**
     * Attribute stores the inflated view for the fragment.
     */
    private View inflated;

    /**
     * Attribute stores the ListAdapter for the ListView.
     */
    private ListAdapter adapter;


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
        viewModel = new ViewModelProvider(requireActivity()).get(EntriesViewModel.class);
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

        adapter = new ListAdapter(Singleton.ENTRIES.getEntries(), getContext());

        //Setup button to sort the entries:
        ImageButton sortButton = inflated.findViewById(R.id.entries_sort_button);
        sortButton.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(EntriesFragment.this.getContext(), sortButton);
            popup.getMenuInflater().inflate(R.menu.sort_entries_list, popup.getMenu());
            popup.setOnMenuItemClickListener(EntriesFragment.this);
            popup.show();
        });

        //Setup button to show / hide search bar:
        EditText searchBar = inflated.findViewById(R.id.entries_search_bar);
        ImageButton searchButton = inflated.findViewById(R.id.entries_search_button);
        searchButton.setOnClickListener(view -> {
            if (viewModel.isSearchBarVisible()) {
                viewModel.setSearchBarVisible(false);
                searchBar.setVisibility(View.GONE);
            }
            else {
                viewModel.setSearchBarVisible(true);
                searchBar.setVisibility(View.VISIBLE);
            }
        });
        if (viewModel.isSearchBarVisible()) {
            searchBar.setVisibility(View.VISIBLE);
        }
        else {
            searchBar.setVisibility(View.GONE);
        }
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                //Do nothing...
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ListAdapter adapter = (ListAdapter)((ListView)EntriesFragment.this.inflated.findViewById(R.id.abbreviated_entries)).getAdapter();
                adapter.getFilter().filter(s);
                populateListView();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Do nothing...
            }
        });

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
     * Method is called whenever a menu item of the menu {@link R.menu#sort_entries_list} is clicked.
     * This will sort the {@linkplain ListView} which displays all {@link Entry}-instances according
     * to the clicked menu item.
     *
     * @param item  Item which was clicked.
     * @return      Whether the click was successfully processed.
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_entries_not_sorted:
                Singleton.ENTRIES.removeAllSortings();
                break;
            case R.id.sort_entries_name_ascending:
                Singleton.ENTRIES.sortByName(false);
                break;
            case R.id.sort_entries_name_descending:
                Singleton.ENTRIES.sortByName(true);
                break;
            case R.id.sort_entries_created_ascending:
                Singleton.ENTRIES.sortByTime(false);
                break;
            case R.id.sort_entries_created_descending:
                Singleton.ENTRIES.sortByTime(true);
                break;
            default:
                return false;
        }
        populateListView();
        return true;
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
        ListView listView = inflated.findViewById(R.id.abbreviated_entries);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(this);
    }

}
