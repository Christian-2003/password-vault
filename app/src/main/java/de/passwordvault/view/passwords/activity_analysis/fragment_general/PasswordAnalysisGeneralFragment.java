package de.passwordvault.view.passwords.activity_analysis.fragment_general;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;
import de.passwordvault.view.passwords.activity_analysis.PasswordAnalysisActivityDeprecated;
import de.passwordvault.view.passwords.activity_list.PasswordAnalysisListActivity;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.passwords.activity_analysis.PasswordAnalysisViewModel;
import de.passwordvault.view.utils.components.PasswordVaultFragment;


/**
 * Class implements the fragment which displays general information about the analyzed password
 * security.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class PasswordAnalysisGeneralFragment extends PasswordVaultFragment<PasswordAnalysisViewModel> {

    public PasswordAnalysisGeneralFragment() {
        super(PasswordAnalysisViewModel.class, R.layout.fragment_password_analysis_general);
    }


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
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            return null;
        }

        //Security score:
        String securityScore = Utils.formatNumber(PasswordSecurityAnalysis.getInstance().getAverageSecurityScore()) + " / " + QualityGateManager.getInstance().numberOfQualityGates();
        TextView securityScoreTextView = view.findViewById(R.id.password_analysis_general_security_score);
        securityScoreTextView.setText(securityScore);
        securityScoreTextView.setTextColor(Utils.getPasswordSecurityScoreColor((int)PasswordSecurityAnalysis.getInstance().getAverageSecurityScore()));

        //Security score bar:
        ProgressBar securityBar = view.findViewById(R.id.password_analysis_general_security_bar);
        securityBar.setMax(QualityGateManager.getInstance().numberOfQualityGates() * 1000);
        ValueAnimator animator = ValueAnimator.ofInt(0, (int)(PasswordSecurityAnalysis.getInstance().getAverageSecurityScore() * 1000));
        animator.setDuration(requireActivity().getResources().getInteger(R.integer.default_anim_duration) * 5L);
        animator.addUpdateListener(animation -> securityBar.setProgress((int) animation.getAnimatedValue()));
        animator.start();

        //Analyzed passwords:
        String analyzed;
        if (PasswordSecurityAnalysis.getInstance().getData().size() == 1) {
            analyzed = view.getContext().getString(R.string.password_results_general_analyzed_hint_singular).replace("{arg}", "" + PasswordSecurityAnalysis.getInstance().getData().size());
        }
        else {
            analyzed = view.getContext().getString(R.string.password_results_general_analyzed_hint).replace("{arg}", "" + PasswordSecurityAnalysis.getInstance().getData().size());
        }
        ((TextView)view.findViewById(R.id.password_analysis_general_analyzed)).setText(analyzed);
        view.findViewById(R.id.password_analysis_analyzed_clickable).setOnClickListener(v -> showAllPasswords());

        //Duplicate passwords:
        String duplicates;
        if (PasswordSecurityAnalysis.getInstance().getIdenticalPasswords().size() == 1) {
            duplicates = view.getContext().getString(R.string.password_results_general_duplicates_hint_singular).replace("{arg}", "" + PasswordSecurityAnalysis.getInstance().getIdenticalPasswords().size());
        }
        else {
            duplicates = view.getContext().getString(R.string.password_results_general_duplicates_hint).replace("{arg}", "" + PasswordSecurityAnalysis.getInstance().getIdenticalPasswords().size());
        }
        ((TextView)view.findViewById(R.id.password_analysis_general_duplicates)).setText(duplicates);
        view.findViewById(R.id.password_analysis_duplicates_clickable).setOnClickListener(v -> showDuplicatePasswords());

        //Weak passwords:
        String weak;
        if (viewModel.getWeakPasswords().size() == 1) {
            weak = view.getContext().getString(R.string.password_results_general_weak_hint_singular).replace("{arg}", "" + viewModel.getWeakPasswords().size());
        }
        else {
            weak = view.getContext().getString(R.string.password_results_general_weak_hint).replace("{arg}", "" + viewModel.getWeakPasswords().size());
        }
        ((TextView)view.findViewById(R.id.password_analysis_general_weak)).setText(weak);
        view.findViewById(R.id.password_analysis_weak_clickable).setOnClickListener(v -> showWeakPasswords());

        return view;
    }


    /**
     * Method shows the fragment in which the duplicate passwords are displayed.
     */
    private void showDuplicatePasswords() {
        try {
            PasswordAnalysisActivityDeprecated activity = (PasswordAnalysisActivityDeprecated)requireActivity();
            activity.showFragmentPage(2);
        }
        catch (ClassCastException e) {
            Log.d("PasswordAnalysisGeneralFragment", "Cannot cast FragmentActivity to PasswordAnalysisActivity");
        }
    }


    /**
     * Method shows the fragment in which the weak passwords are displayed.
     */
    private void showWeakPasswords() {
        try {
            PasswordAnalysisActivityDeprecated activity = (PasswordAnalysisActivityDeprecated)requireActivity();
            activity.showFragmentPage(1);
        }
        catch (ClassCastException e) {
            Log.d("PasswordAnalysisGeneralFragment", "Cannot cast FragmentActivity to PasswordAnalysisActivity");
        }
    }


    /**
     * Method shows the activity which displays all analyzed passwords to the user.
     */
    private void showAllPasswords() {
        Intent intent = new Intent(requireActivity(), PasswordAnalysisListActivity.class);
        startActivity(intent);
    }

}
