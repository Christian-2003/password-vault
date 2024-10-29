package de.passwordvault.view.activity_main.fragment_entries;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.view.activity_main.MainViewModel;
import de.passwordvault.view.entries.activity_entry.EntryActivity;
import de.passwordvault.view.utils.components.PasswordVaultFragment;


/**
 * Class implements the {@linkplain Fragment} that displays a list of all entries.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class EntriesFragment extends PasswordVaultFragment<MainViewModel> implements Observer<ArrayList<EntryAbbreviated>> {

    /**
     * Attribute stores the adapter for the recycler view which displays the abbreviated entries.
     */
    private EntriesRecyclerViewAdapter adapter;

    /**
     * Attribute stores the launcher used to start the {@link EntryActivity}.
     */
    private ActivityResultLauncher<Intent> entryLauncher;


    /**
     * Constructor instantiates a new fragment.
     */
    public EntriesFragment() {
        super(MainViewModel.class, R.layout.fragment_entries);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            entryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                int adapterPosition = viewModel.getOpenedEntryAdapterPosition();
                if (adapterPosition >= EntriesRecyclerViewAdapter.OFFSET_ENTRIES && adapterPosition <= EntriesRecyclerViewAdapter.OFFSET_ENTRIES + adapter.getItemCount()) {
                    if (result.getResultCode() == EntryActivity.RESULT_DELETED) {
                        adapter.notifyItemRemoved(adapterPosition);
                    }
                    else if (result.getResultCode() == EntryActivity.RESULT_EDITED) {
                        adapter.notifyItemChanged(adapterPosition);
                    }
                }
            });
        }
    }

    /**
     * Method is called whenever the {@linkplain View} for the EntriesFragment is created.
     *
     * @param inflater              LayoutInflater for the fragment.
     * @param container             Container which contains the fragment.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Generated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            return null;
        }
        EntryManager.getInstance().addObserver(this);

        adapter = new EntriesRecyclerViewAdapter(requireActivity(), viewModel);
        adapter.setItemClickListener(this::onEntryClicked);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);

        return view;
    }


    /**
     * Method is called whenever the view for the fragment is destroyed.
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
        //adapter.notifyDataSetChanged();
    }


    /**
     * Method updates the recycler view of the fragment.
     */
    public void updateDataset() {
        if (adapter == null) {
            return;
        }
        //adapter.notifyDataSetChanged();
    }


    /**
     * Method is called whenever an entry is clicked.
     *
     * @param position  Adapter position of the entry clicked.
     */
    private void onEntryClicked(int position) {
        EntryAbbreviated entry = adapter.getEntryForAdapterPosition(position);
        if (entry != null) {
            Intent intent = new Intent(getActivity(), EntryActivity.class);
            intent.putExtra(EntryActivity.KEY_ID, entry.getUuid());
            viewModel.setOpenedEntryAdapterPosition(position);
            entryLauncher.launch(intent);
        }
    }

}
