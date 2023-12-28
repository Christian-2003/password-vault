package de.passwordvault.view.fragments;

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
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import de.passwordvault.R;
import de.passwordvault.model.entry.EntryHandle;
import de.passwordvault.view.viewmodel.EntriesViewModel;
import de.passwordvault.view.utils.ListAdapter;
import de.passwordvault.model.entry.Entry;
import de.passwordvault.view.activities.EntryActivity;
import de.passwordvault.view.activities.MainActivity;


/**
 * Class implements the {@linkplain Fragment} that displays a list of {@linkplain Entry} instances
 * within the {@linkplain MainActivity}.
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class EntriesFragment extends Fragment implements AdapterView.OnItemClickListener, PopupMenu.OnMenuItemClickListener {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} for this fragment.
     */
    private EntriesViewModel viewModel;

    /**
     * Attribute stores the ListAdapter for the ListView.
     */
    private ListAdapter adapter;

    /**
     * Attribute stores the {@linkplain ListView} which displays the {@link Entry}-instances.
     */
    private ListView entriesListView;


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
        View inflated = inflater.inflate(R.layout.fragment_entries, container, false);

        adapter = new ListAdapter(EntryHandle.getInstance().getEntries(), getContext());
        entriesListView = inflated.findViewById(R.id.abbreviated_entries);

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
                searchBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left));
                searchBar.postDelayed(() -> searchBar.setVisibility(View.GONE), getResources().getInteger(R.integer.default_anim_duration));
            }
            else {
                viewModel.setSearchBarVisible(true);
                searchBar.setVisibility(View.VISIBLE);
                searchBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left));
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
                ListAdapter adapter = (ListAdapter)(entriesListView.getAdapter());
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
    public void onItemClick(@NonNull AdapterView<?> parent, View view, int position, long id) {
        Object item = parent.getAdapter().getItem(position);
        if (item instanceof Entry) {
            String uuid = ((Entry)item).getUuid();
            if (uuid != null) {
                Intent intent = new Intent(getActivity(), EntryActivity.class);
                intent.putExtra("uuid", uuid);
                getActivity().startActivityForResult(intent, 1);
            }
        }
        else {
            Toast.makeText(getContext(), getString(R.string.error_cannot_show_entry), Toast.LENGTH_SHORT).show();
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
                EntryHandle.getInstance().removeAllSortings();
                break;
            case R.id.sort_entries_name_ascending:
                EntryHandle.getInstance().sortByName(false);
                break;
            case R.id.sort_entries_name_descending:
                EntryHandle.getInstance().sortByName(true);
                break;
            case R.id.sort_entries_created_ascending:
                EntryHandle.getInstance().sortByTime(false);
                break;
            case R.id.sort_entries_created_descending:
                EntryHandle.getInstance().sortByTime(true);
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
     * Method populates the {@linkplain ListView} that displays all {@linkplain de.passwordvault.model.entry.Entry}
     * instances.
     */
    private void populateListView() {
        entriesListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        entriesListView.setOnItemClickListener(this);
    }

}
