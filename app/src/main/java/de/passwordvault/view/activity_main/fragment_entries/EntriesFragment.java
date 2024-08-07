package de.passwordvault.view.activity_main.fragment_entries;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.view.utils.components.PasswordVaultBaseFragment;
import de.passwordvault.view.utils.components.SearchBarView;
import de.passwordvault.view.activity_main.MainActivity;


/**
 * Class implements the {@linkplain Fragment} that displays a list of {@linkplain EntryAbbreviated} instances
 * within the {@linkplain MainActivity}.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class EntriesFragment extends PasswordVaultBaseFragment implements PopupMenu.OnMenuItemClickListener, Observer<ArrayList<EntryAbbreviated>> {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} for this fragment.
     */
    private EntriesViewModel viewModel;

    /**
     * Attribute stores the adapter for the recycler view which displays the abbreviated entries.
     */
    private EntriesRecyclerViewAdapter adapter;

    /**
     * Attribute stores the view of the fragment.
     */
    private View view;


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

        adapter = new EntriesRecyclerViewAdapter(EntryManager.getInstance().getData(), (MainActivity)requireActivity());
        RecyclerView recyclerView = view.findViewById(R.id.abbreviated_entries);
        recyclerView.setAdapter(adapter);
        if (EntryManager.getInstance().getData().isEmpty()) {
            view.findViewById(R.id.entries_container_none).setVisibility(View.VISIBLE);
            view.findViewById(R.id.abbreviated_entries).setVisibility(View.GONE);
        }

        //Setup button to sort the entries:
        ImageButton sortButton = view.findViewById(R.id.button_sort);
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
        SearchBarView searchBar = view.findViewById(R.id.search_bar);
        ImageButton searchButton = view.findViewById(R.id.button_search);
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
        searchBar.setVisibility(viewModel.isSearchBarVisible() ? View.VISIBLE : View.GONE);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                //Do nothing...
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Do nothing...
            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EntryManager.getInstance().removeObserver(this);
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
        EntryManager.getInstance().getData(); // getData() forces cache to be sorted, which updates the sorting of the recycler view.
        adapter.notifyDataSetChanged();
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
        adapter.getFilter().filter(((SearchBarView)view.findViewById(R.id.search_bar)).getText());
    }


    /**
     * Method updates the recycler view of the fragment.
     */
    public void updateDataset() {
        if (adapter == null) {
            return;
        }
        boolean currentlyEmpty = adapter.getItemCount() == 0;
        view.findViewById(R.id.entries_container_none).setVisibility(currentlyEmpty ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.abbreviated_entries).setVisibility(currentlyEmpty ? View.GONE : View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

}
