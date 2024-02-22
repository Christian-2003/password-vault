package de.passwordvault.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.viewmodel.fragments.EntriesViewModel;
import de.passwordvault.view.utils.EntriesListAdapter;
import de.passwordvault.view.activities.EntryActivity;
import de.passwordvault.view.activities.MainActivity;


/**
 * Class implements the {@linkplain Fragment} that displays a list of {@linkplain EntryAbbreviated} instances
 * within the {@linkplain MainActivity}.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class EntriesFragment extends Fragment implements AdapterView.OnItemClickListener, PopupMenu.OnMenuItemClickListener, Observer<ArrayList<EntryAbbreviated>> {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} for this fragment.
     */
    private EntriesViewModel viewModel;

    /**
     * Attribute stores the ListAdapter for the ListView.
     */
    private EntriesListAdapter adapter;

    /**
     * Attribute stores the {@linkplain ListView} which displays the {@link EntryAbbreviated}-instances.
     */
    private ListView entriesListView;

    /**
     * Attribute stores the view of the fragment.
     */
    private View view;


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
        EntryManager.getInstance().addObserver(this);
        view = inflater.inflate(R.layout.fragment_entries, container, false);

        adapter = new EntriesListAdapter(EntryManager.getInstance().getData(), getContext());
        entriesListView = view.findViewById(R.id.abbreviated_entries);

        //Setup button to sort the entries:
        ImageButton sortButton = view.findViewById(R.id.entries_sort_button);
        sortButton.setOnClickListener(view -> {
            Context wrapper = new ContextThemeWrapper(EntriesFragment.this.getContext(), R.style.popup_menu);
            PopupMenu popup = new PopupMenu(wrapper, sortButton);
            popup.getMenuInflater().inflate(R.menu.sort_entries_list, popup.getMenu());
            popup.getMenu().findItem(viewModel.getSelectedEntrySorting()).setIcon(R.drawable.ic_check);
            popup.setOnMenuItemClickListener(EntriesFragment.this);
            popup.setForceShowIcon(true);
            popup.show();
        });

        //Setup button to show / hide search bar:
        TextInputLayout searchBarLayout = view.findViewById(R.id.entries_search_bar_container);
        ImageButton searchButton = view.findViewById(R.id.entries_search_button);
        searchButton.setOnClickListener(view -> {
            if (viewModel.isSearchBarVisible()) {
                viewModel.setSearchBarVisible(false);
                searchBarLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left));
                searchBarLayout.postDelayed(() -> searchBarLayout.setVisibility(View.GONE), getResources().getInteger(R.integer.default_anim_duration));
            }
            else {
                viewModel.setSearchBarVisible(true);
                searchBarLayout.setVisibility(View.VISIBLE);
                searchBarLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left));
            }
        });
        if (viewModel.isSearchBarVisible()) {
            searchBarLayout.setVisibility(View.VISIBLE);
        }
        else {
            searchBarLayout.setVisibility(View.GONE);
        }
        TextInputEditText searchBar = view.findViewById(R.id.entries_search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                //Do nothing...
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EntriesListAdapter adapter = (EntriesListAdapter)(entriesListView.getAdapter());
                adapter.getFilter().filter(s);
                populateListView();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Do nothing...
            }
        });

        populateListView();

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EntryManager.getInstance().removeObserver(this);
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
        if (item instanceof EntryAbbreviated) {
            String uuid = ((EntryAbbreviated)item).getUuid();
            if (uuid != null) {
                Intent intent = new Intent(getActivity(), EntryActivity.class);
                intent.putExtra("uuid", uuid);
                getActivity().startActivity(intent);
            }
        }
        else {
            Toast.makeText(getContext(), getString(R.string.error_cannot_show_entry), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Method is called whenever a menu item of the menu {@link R.menu#sort_entries_list} is clicked.
     * This will sort the {@linkplain ListView} which displays all {@link EntryAbbreviated}-instances according
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
                viewModel.setSelectedEntrySorting(R.id.sort_entries_not_sorted);
                EntryManager.getInstance().removeAllSortings();
                break;
            case R.id.sort_entries_name_ascending:
                viewModel.setSelectedEntrySorting(R.id.sort_entries_name_ascending);
                EntryManager.getInstance().sortByName(false);
                break;
            case R.id.sort_entries_name_descending:
                viewModel.setSelectedEntrySorting(R.id.sort_entries_name_descending);
                EntryManager.getInstance().sortByName(true);
                break;
            case R.id.sort_entries_created_ascending:
                viewModel.setSelectedEntrySorting(R.id.sort_entries_created_ascending);
                EntryManager.getInstance().sortByTime(false);
                break;
            case R.id.sort_entries_created_descending:
                viewModel.setSelectedEntrySorting(R.id.sort_entries_created_descending);
                EntryManager.getInstance().sortByTime(true);
                break;
            default:
                return false;
        }
        viewModel.setSelectedEntrySorting(item.getItemId());
        populateListView();
        return true;
    }


    /**
     * Method informs the {@link Observer} that the observed data has been changed. The passed
     * {@link Observable} references the object which is being observed.
     *
     * @param o                     Observed instance whose data was changed.
     * @throws NullPointerException The passed Observable is {@code null}.
     */
    @Override
    public void update(Observable<ArrayList<EntryAbbreviated>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException("Null is invalid Observable");
        }
        adapter = new EntriesListAdapter(o.getData(), getContext());
        populateListView();
    }


    /**
     * Method populates the {@linkplain ListView} that displays all {@linkplain de.passwordvault.model.entry.EntryAbbreviated}
     * instances.
     */
    private void populateListView() {
        entriesListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        entriesListView.setOnItemClickListener(this);
    }

}
