package de.passwordvault.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.view.utils.adapters.QualityGatesListAdapter;
import de.passwordvault.viewmodel.activities.QualityGatesViewModel;


/**
 * Class implements an activity which can add (or edit) entries.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class QualityGatesActivity extends AppCompatActivity implements Observer<ArrayList<QualityGate>> {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} of this activity.
     */
    private QualityGatesViewModel viewModel;

    /**
     * Attribute stores the list adapter for the list view displaying the quality gates.
     */
    private QualityGatesListAdapter listAdapter;


    /**
     * Method informs the {@link Observer} that the observed data has been changed. The passed
     * {@link Observable} references the object which is being observed.
     *
     * @param o                     Observed instance whose data was changed.
     * @throws NullPointerException The passed Observable is {@code null}.
     */
    @Override
    public void update(Observable<ArrayList<QualityGate>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException();
        }
        listAdapter.notifyDataSetChanged();
    }



    /**
     * Method is called whenever the activity is created / recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_gates);
        viewModel = new ViewModelProvider(this).get(QualityGatesViewModel.class);

        //QualityGateManager.getInstance().clearQualityGates();

        findViewById(R.id.quality_gates_back_button).setOnClickListener(view -> finish());
        findViewById(R.id.quality_gate_button_add).setOnClickListener(view -> startActivity(new Intent(QualityGatesActivity.this, QualityGateActivity.class)));

        ListView listView = findViewById(R.id.quality_gates_list_view);
        listAdapter = new QualityGatesListAdapter(QualityGateManager.getInstance().getData(), this);
        listView.setAdapter(listAdapter);

        QualityGateManager.getInstance().addObserver(this);
    }


    /**
     * Method is called whenever the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        QualityGateManager.getInstance().removeObserver(this);
        QualityGateManager.getInstance().saveAllQualityGates();
    }

}
