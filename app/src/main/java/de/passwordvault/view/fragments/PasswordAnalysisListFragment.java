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
import de.passwordvault.view.activities.EntryActivity;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.PasswordsRecyclerViewAdapter;
import de.passwordvault.viewmodel.fragments.PasswordAnalysisListViewModel;


/**
 * Class implements the fragment which shows a list of all passwords that were analyzed during the
 * password security analysis.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class PasswordAnalysisListFragment extends Fragment implements OnRecyclerItemClickListener<Password> {

    /**
     * Attribute stores the view model of the fragment.
     */
    private PasswordAnalysisListViewModel viewModel;

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
        viewModel = new ViewModelProvider(requireActivity()).get(PasswordAnalysisListViewModel.class);
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
        view = inflater.inflate(R.layout.fragment_password_analysis_list, container, false);

        PasswordsRecyclerViewAdapter adapter = new PasswordsRecyclerViewAdapter(PasswordSecurityAnalysis.getInstance().getData(), this, true);
        RecyclerView recyclerView = view.findViewById(R.id.password_analysis_recycler_view);
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
        Intent intent = new Intent(requireActivity(), EntryActivity.class);
        intent.putExtra("uuid", item.getEntryUuid());
        startActivity(intent);
    }

}
