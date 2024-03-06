package de.passwordvault.view.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.viewmodel.activities.PasswordAnalysisViewModel;


public class PasswordAnalysisActivity extends AppCompatActivity implements Observer<ArrayList<Password>> {

    private PasswordAnalysisViewModel viewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_analysis);
        viewModel = new ViewModelProvider(this).get(PasswordAnalysisViewModel.class);

        findViewById(R.id.password_analysis_back_button).setOnClickListener(view -> finish());
        showAnalysisResults(viewModel.isAnalysisFinished());
    }


    @Override
    public void finish() {
        super.finish();
    }

    public void showAnalysisResults(boolean showResults) {
        if (showResults) {
            //Analysis has been finished
        }
        else {
            //Analysis has not been finished
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
        showAnalysisResults(true);
    }

}
