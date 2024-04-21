package de.passwordvault.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;
import de.passwordvault.view.activities.EntryActivity;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.adapters.PasswordsRecyclerViewAdapter;
import de.passwordvault.view.utils.components.PasswordVaultBaseFragment;
import de.passwordvault.viewmodel.activities.PasswordAnalysisViewModel;


/**
 * Class implements the fragment used to display weak passwords to the user.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class PasswordAnalysisWeakFragment extends PasswordVaultBaseFragment implements OnRecyclerItemClickListener<Password> {

    /**
     * Attribute stores the view model for the fragment.
     */
    private PasswordAnalysisViewModel viewModel;

    /**
     * Attribute stores the inflated view for the fragment.
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
        viewModel = new ViewModelProvider(requireActivity()).get(PasswordAnalysisViewModel.class);
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
        view = inflater.inflate(R.layout.fragment_password_analysis_weak, container, false);

        boolean passwordsFound = PasswordSecurityAnalysis.getInstance().getData().size() != 0;
        view.findViewById(R.id.container_none).setVisibility(passwordsFound ? View.GONE : View.VISIBLE);

        PasswordsRecyclerViewAdapter adapter = new PasswordsRecyclerViewAdapter(viewModel.getWeakPasswords(), this, true);
        RecyclerView recyclerView = view.findViewById(R.id.passwords_recycler_view);
        recyclerView.setVisibility(passwordsFound ? View.VISIBLE : View.GONE);
        recyclerView.setAdapter(adapter);

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
