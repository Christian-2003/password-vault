package de.passwordvault.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;
import de.passwordvault.viewmodel.fragments.PasswordAnalysisGeneralViewModel;


/**
 * Class implements the fragment which displays general information about the analyzed password
 * security.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class PasswordAnalysisGeneralFragment extends Fragment {

    /**
     * Attribute stores the view model of the fragment.
     */
    private PasswordAnalysisGeneralViewModel viewModel;

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
        viewModel = new ViewModelProvider(requireActivity()).get(PasswordAnalysisGeneralViewModel.class);
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
        view = inflater.inflate(R.layout.fragment_password_analysis_general, container, false);

        String securityScore = (Math.floor(PasswordSecurityAnalysis.getInstance().getAverageSecurityScore() * 100) / 100) + "/" + QualityGateManager.getInstance().numberOfQualityGates();
        ((TextView)view.findViewById(R.id.password_analysis_general_security_score)).setText(securityScore);

        ProgressBar securityBar = view.findViewById(R.id.password_analysis_general_security_bar);
        securityBar.setMax(QualityGateManager.getInstance().numberOfQualityGates() * 1000);
        securityBar.setProgress((int)(PasswordSecurityAnalysis.getInstance().getAverageSecurityScore() * 1000));
        //TODO: Animate security bar...

        String duplicates = view.getContext().getString(R.string.password_results_general_duplicates_hint).replace("{arg}", "" + PasswordSecurityAnalysis.getInstance().getIdenticalPasswords().size());
        ((TextView)view.findViewById(R.id.password_analysis_general_duplicates)).setText(duplicates);
        //TODO: Add click listener to duplicates...

        return view;
    }

}
