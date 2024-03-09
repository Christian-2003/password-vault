package de.passwordvault.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;
import de.passwordvault.view.activities.DuplicatePasswordEntriesActivity;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.adapters.PasswordsRecyclerViewAdapter;
import de.passwordvault.viewmodel.fragments.PasswordAnalysisDuplicatesViewModel;


/**
 * Class implements the fragment that shows a list of duplicate passwords to the user.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class PasswordAnalysisDuplicatesFragment extends Fragment implements OnRecyclerItemClickListener<Password> {

    /**
     * Attribute stores the view model of the fragment.
     */
    private PasswordAnalysisDuplicatesViewModel viewModel;

    /**
     * Attribute stores the inflated view of the fragment.
     */
    private View view;


    /**
     * Method is called whenever the fragment is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PasswordAnalysisDuplicatesViewModel.class);
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
        view = inflater.inflate(R.layout.fragment_password_analysis_duplicates, container, false);

        boolean duplicatesFound = PasswordSecurityAnalysis.getInstance().getIdenticalPasswords().size() != 0;
        view.findViewById(R.id.duplicate_passwords_none_container).setVisibility(duplicatesFound ? View.GONE : View.VISIBLE);

        PasswordsRecyclerViewAdapter adapter = new PasswordsRecyclerViewAdapter(viewModel.getPasswords(), this, false);
        RecyclerView recyclerView = view.findViewById(R.id.duplicate_passwords_recycler_view);
        recyclerView.setVisibility(duplicatesFound ? View.VISIBLE : View.GONE);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));

        return view;
    }


    /**
     * Method is called when the item which is passed as argument is clicked by the user.
     *
     * @param item      Clicked item.
     * @param position  Index of the clicked item.
     */
    @Override
    public void onItemClick(Password item, int position) {
        Intent intent = new Intent(requireActivity(), DuplicatePasswordEntriesActivity.class);
        intent.putExtra(DuplicatePasswordEntriesActivity.KEY_PASSWORDS, PasswordSecurityAnalysis.getInstance().getIdenticalPasswords().get(position));
        startActivity(intent);
    }

}
