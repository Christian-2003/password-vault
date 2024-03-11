package de.passwordvault.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.view.activities.AddEntryActivity;
import de.passwordvault.view.activities.EntryActivity;
import de.passwordvault.view.activities.MainActivity;
import de.passwordvault.view.utils.adapters.EntriesRecyclerViewAdapter;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;


/**
 * Class implements the {@linkplain Fragment} that is used as home screen for the application. It is
 * used within the {@linkplain MainActivity}.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class HomeFragment extends Fragment implements Observer<ArrayList<EntryAbbreviated>>, OnRecyclerItemClickListener<EntryAbbreviated> {

    /**
     * Attribute stores the adapter for the recycler view displaying the most recently edited entries.
     */
    private EntriesRecyclerViewAdapter adapter;

    /**
     * Attribute stores the inflated view of the fragment.
     */
    private View view;


    /**
     * Default constructor instantiates a new HomeFragment.
     */
    public HomeFragment() {
        // Required empty public constructor
    }


    /**
     * Method is called whenever the HomeFragment is created or recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Method is called whenever the {@linkplain View} for the HomeFragment is created.
     *
     * @param inflater              LayoutInflater for the fragment.
     * @param container             Container which contains the fragment.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Generated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EntryManager.getInstance().addObserver(this);
        view = inflater.inflate(R.layout.fragment_home, container, false);

        Button addAccountButton = view.findViewById(R.id.home_add_account_button);
        addAccountButton.setOnClickListener(view -> startActivity(new Intent(HomeFragment.this.getActivity(), AddEntryActivity.class)));

        Button viewAccountsButton = view.findViewById(R.id.home_show_accounts_button);
        viewAccountsButton.setOnClickListener(view -> {
            FragmentActivity fragmentActivity = HomeFragment.this.getActivity();
            if (fragmentActivity instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity)fragmentActivity;
                mainActivity.switchToFragment(R.id.menu_entries);
            }
            else {
                Toast.makeText(getContext(), "Error, AppCompatActivity not MainActivity class.", Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new EntriesRecyclerViewAdapter(EntryManager.getInstance().getMostRecentlyEditedEntries(), this);
        RecyclerView recyclerView = view.findViewById(R.id.home_recently_changed_container);
        recyclerView.setAdapter(adapter);
        return view;
    }


    /**
     * Lifecycle-based method is called when the view is destroyed and no longer being used.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EntryManager.getInstance().removeObserver(this);
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
        adapter.notifyDataSetChanged();
    }

    /**
     * Method is called whenever an item within the recycler view of this fragment is clicked.
     *
     * @param item      Clicked item.
     * @param position  Index of the clicked item.
     */
    @Override
    public void onItemClick(EntryAbbreviated item, int position) {
        Intent intent = new Intent(getActivity(), EntryActivity.class);
        intent.putExtra("uuid", item.getUuid());
        getActivity().startActivity(intent);
    }

}
