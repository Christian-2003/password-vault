package de.passwordvault.view.passwords.activity_analysis.fragment_weak;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.view.entries.activity_entry.EntryActivity;
import de.passwordvault.view.utils.components.PasswordVaultFragment;
import de.passwordvault.view.passwords.activity_analysis.PasswordAnalysisViewModelDeprecated;


/**
 * Class implements the fragment used to display weak passwords to the user.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class PasswordAnalysisWeakFragment extends PasswordVaultFragment<PasswordAnalysisViewModelDeprecated> {

    /**
     * Attribute stores the adapter for the fragment.
     */
    private PasswordAnalysisWeakRecyclerViewAdapter adapter;


    /**
     * Constructor instantiates a new fragment.
     */
    public PasswordAnalysisWeakFragment() {
        super(PasswordAnalysisViewModelDeprecated.class, R.layout.fragment_password_analysis_weak);
    }


    /**
     * Method is called whenever the view for the fragment is created.
     *
     * @param inflater              The LayoutInflater object that can be used to inflate any views
     *                              in the fragment.
     * @param container             If non-null, this is the parent view that the fragment's UI
     *                              should be attached to.  The fragment should not add the view
     *                              itself, but this can be used to generate the LayoutParams of the
     *                              view.
     * @param savedInstanceState    If non-null, this fragment is being re-constructed from a
     *                              previous saved state as given here.
     * @return                      Inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        adapter = new PasswordAnalysisWeakRecyclerViewAdapter(requireContext(), viewModel);
        adapter.setItemClickListener(this::onItemClick);
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);

        return fragmentView;
    }


    /**
     * Method is called when the item which is passed as argument is clicked by the user.
     *
     * @param position  Index of the clicked item.
     */
    public void onItemClick(int position) {
        Intent intent = new Intent(requireActivity(), EntryActivity.class);
        Password item = viewModel.getWeakPasswords().get(position - PasswordAnalysisWeakRecyclerViewAdapter.OFFSET_PASSWORDS);
        intent.putExtra("uuid", item.getEntryUuid());
        startActivity(intent);
    }

}
