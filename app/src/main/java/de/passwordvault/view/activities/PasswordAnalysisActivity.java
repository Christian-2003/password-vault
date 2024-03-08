package de.passwordvault.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.PasswordAnalysisFragmentStateAdapter;
import de.passwordvault.view.utils.PasswordsRecyclerViewAdapter;
import de.passwordvault.viewmodel.activities.PasswordAnalysisViewModel;


/**
 * Class implements the activity which displays a list of duplicate passwords.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class PasswordAnalysisActivity extends AppCompatActivity implements Observer<ArrayList<Password>> {

    /**
     * Attribute stores the view model of the activity.
     */
    private PasswordAnalysisViewModel viewModel;


    /**
     * Method creates a new activity.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_analysis);
        viewModel = new ViewModelProvider(this).get(PasswordAnalysisViewModel.class);

        findViewById(R.id.password_analysis_back_button).setOnClickListener(view -> finish());
        findViewById(R.id.password_analysis_reload_button).setOnClickListener(view -> restartAnalysis());
        showAnalysisResults(PasswordSecurityAnalysis.getInstance().isAnalysisCompleted());
        PasswordSecurityAnalysis.getInstance().addObserver(this);
        if (!PasswordSecurityAnalysis.getInstance().isAnalysisCompleted() && !PasswordSecurityAnalysis.getInstance().isAnalysisRunning()) {
            PasswordSecurityAnalysis.getInstance().analyze();
        }
    }


    /**
     * Method is called when the activity is closed.
     */
    @Override
    public void finish() {
        super.finish();
        PasswordSecurityAnalysis.getInstance().removeObserver(this);
    }


    /**
     * Method shows the analysis results to the user (= {@code true}). Alternatively, the activity
     * can display a 'waiting screen' which informs the user that the passwords are currently being
     * analyzed (= {@code false}).
     *
     * @param showResults   Whether to show the analysis results or the waiting screen.
     */
    public void showAnalysisResults(boolean showResults) {
        findViewById(R.id.password_analysis_container_analyzing).setVisibility(showResults ? View.GONE : View.VISIBLE);
        findViewById(R.id.password_analysis_container_results).setVisibility(showResults ? View.VISIBLE : View.GONE);
        if (showResults) {
            PasswordAnalysisFragmentStateAdapter adapter = new PasswordAnalysisFragmentStateAdapter(this);
            ViewPager2 viewPager = findViewById(R.id.password_analysis_view_pager);
            viewPager.setAdapter(adapter);

            TabLayout tabs = findViewById(R.id.password_analysis_tabs);
            new TabLayoutMediator(tabs, viewPager, (tab, position) -> PasswordAnalysisActivity.this.viewModel.updateTabName(tab, position)).attach();
        }
    }


    /**
     * Method informs the {@link de.passwordvault.model.Observer} that the observed data has been changed. The passed
     * {@link Observable} references the object which is being observed.
     *
     * @param o                     Observed instance whose data was changed.
     * @throws NullPointerException The passed Observable is {@code null}.
     */
    @Override
    public void update(Observable<ArrayList<Password>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException();
        }
        runOnUiThread(() -> showAnalysisResults(true));
    }





    private void restartAnalysis() {
        showAnalysisResults(false);
        PasswordSecurityAnalysis.getInstance().analyze(true);
    }

}
