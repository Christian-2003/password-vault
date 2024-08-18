package de.passwordvault.view.settings.activity_licenses;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


public class LicensesActivity extends PasswordVaultActivity<LicensesViewModel> {


    public LicensesActivity() {
        super(LicensesViewModel.class, R.layout.activity_licenses);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LicensesRecyclerViewAdapter adapter = new LicensesRecyclerViewAdapter(this, viewModel);
        recyclerView.setAdapter(adapter);
    }

}
