package de.passwordvault.view.passwords.activity_analysis.fragment_duplicates;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;
import de.passwordvault.view.passwords.activity_duplicates.DuplicatePasswordEntriesActivity;
import de.passwordvault.view.utils.components.PasswordVaultFragment;


/**
 * Class implements the fragment that shows a list of duplicate passwords to the user.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class PasswordAnalysisDuplicatesFragment extends PasswordVaultFragment<PasswordAnalysisDuplicatesViewModel> {

    public PasswordAnalysisDuplicatesFragment() {
        super(PasswordAnalysisDuplicatesViewModel.class, R.layout.fragment_password_analysis_duplicates);
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

        PasswordAnalysisDuplicatesRecyclerViewAdapter adapter = new PasswordAnalysisDuplicatesRecyclerViewAdapter(requireActivity(), viewModel);
        adapter.setItemClickListener(this::onPasswordClicked);
        RecyclerView recyclerView = fragmentView.findViewById(R.id.duplicate_passwords_recycler_view);
        recyclerView.setAdapter(adapter);

        return fragmentView;
    }


    /**
     * Method is called when the item which is passed as argument is clicked by the user.
     *
     * @param position  Index of the clicked item.
     */
    private void onPasswordClicked(int position) {
        Intent intent = new Intent(requireActivity(), DuplicatePasswordEntriesActivity.class);
        intent.putExtra(DuplicatePasswordEntriesActivity.EXTRA_PASSWORDS, PasswordSecurityAnalysis.getInstance().getIdenticalPasswords().get(position - PasswordAnalysisDuplicatesRecyclerViewAdapter.OFFSET_PASSWORDS));
        startActivity(intent);
    }

}
