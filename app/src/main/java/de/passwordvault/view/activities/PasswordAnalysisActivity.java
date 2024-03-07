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
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.PasswordsRecyclerViewAdapter;
import de.passwordvault.viewmodel.activities.PasswordAnalysisViewModel;


/**
 * Class implements the activity which displays a list of duplicate passwords.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class PasswordAnalysisActivity extends AppCompatActivity implements Observer<ArrayList<Password>>, OnRecyclerItemClickListener<Password> {

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
        showAnalysisResults(viewModel.isAnalysisFinished());
        viewModel.getAnalysisAlgorithm().addObserver(this);
        if (!viewModel.isAnalysisFinished()) {
            viewModel.getAnalysisAlgorithm().analyze();
        }
    }


    /**
     * Method is called when the activity is closed.
     */
    @Override
    public void finish() {
        super.finish();
        viewModel.getAnalysisAlgorithm().removeObserver(this);
        if (!viewModel.isAnalysisFinished()) {
            viewModel.getAnalysisAlgorithm().cancel();
        }
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
            //Display security score:
            TextView scoreView = findViewById(R.id.password_analysis_average_security_score);
            String scoreString = (Math.floor(viewModel.getAnalysisAlgorithm().getAverageSecurityScore() * 100) / 100) + "/" + QualityGateManager.getInstance().numberOfQualityGates();
            scoreView.setText(scoreString);
            ProgressBar scoreBar = findViewById(R.id.password_analysis_average_security_score_bar);
            scoreBar.setMax(QualityGateManager.getInstance().numberOfQualityGates() * 1000);
            scoreBar.setProgress((int)(viewModel.getAnalysisAlgorithm().getAverageSecurityScore() * 1000));

            //Display duplicate passwords:
            TextView duplicatesTextView = findViewById(R.id.password_analysis_duplicates);
            duplicatesTextView.setText(getString(R.string.password_results_general_duplicates_hint).replace("{arg}", "" + viewModel.getAnalysisAlgorithm().getIdenticalPasswords().size()));

            //Display analyzed passwords:
            Log.d("PasswordAnalysis", "Analyzed " + viewModel.getAnalysisAlgorithm().getData().size() + " passwords");
            PasswordsRecyclerViewAdapter adapter = new PasswordsRecyclerViewAdapter(viewModel.getAnalysisAlgorithm().getData(), this, true);
            RecyclerView recyclerView = findViewById(R.id.password_analysis_recycler_view);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

            if (viewModel.getAnalysisAlgorithm().getData().size() > 0) {
                findViewById(R.id.password_analysis_duplicates_clickable).setOnClickListener(view -> {
                    Intent intent = new Intent(PasswordAnalysisActivity.this, DuplicatePasswordsActivity.class);
                    intent.putExtra(DuplicatePasswordsActivity.KEY_PASSWORDS, viewModel.getAnalysisAlgorithm().getIdenticalPasswords());
                    startActivity(intent);
                });
            }
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
        viewModel.setAnalysisFinished(true);
        runOnUiThread(() -> showAnalysisResults(true));
    }


    /**
     * Method is called when the item which is passed as argument is clicked by the user.
     *
     * @param item      Clicked item.
     * @param position  Index of the clicked item.
     */
    @Override
    public void onItemClick(Password item, int position) {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("uuid", item.getEntryUuid());
        startActivity(intent);
    }

}
