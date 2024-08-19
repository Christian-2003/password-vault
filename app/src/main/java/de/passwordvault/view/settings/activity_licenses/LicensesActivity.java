package de.passwordvault.view.settings.activity_licenses;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the activity displaying a list of all software used by the app.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class LicensesActivity extends PasswordVaultActivity<LicensesViewModel> {

    /**
     * Constructor instantiates a new licenses activity.
     */
    public LicensesActivity() {
        super(LicensesViewModel.class, R.layout.activity_licenses);
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LicensesRecyclerViewAdapter adapter = new LicensesRecyclerViewAdapter(this, viewModel);
        recyclerView.setAdapter(adapter);
    }

}
