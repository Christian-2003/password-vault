package de.passwordvault.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import de.passwordvault.R;
import de.passwordvault.model.packages.SerializablePackageCollection;
import de.passwordvault.view.utils.adapters.PackagesFragmentStateAdapter;
import de.passwordvault.viewmodel.activities.PackagesViewModel;


/**
 * Class implements the activity which shows a list of installed apps (packages) to the user, from
 * which the user can select one.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class PackagesActivity extends AppCompatActivity {

    /**
     * Field stores the key that needs to be used when passing the selected package as argument.
     */
    public static final String KEY_PACKAGES = "packages";


    /**
     * Attribute stores the view model of the activity.
     */
    private PackagesViewModel viewModel;


    /**
     * Method is called whenever a new view is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PackagesViewModel.class);
        setContentView(R.layout.activity_packages);

        Bundle args = getIntent().getExtras();
        if (!viewModel.processArguments(args)) {
            Log.d("PA", "ProcessArguments returned false");
            finish();
            return;
        }

        findViewById(R.id.packages_back_button).setOnClickListener(view -> finish());

        PackagesFragmentStateAdapter adapter = new PackagesFragmentStateAdapter(this);
        ViewPager2 viewPager = findViewById(R.id.packages_view_pager);
        viewPager.setAdapter(adapter);

        TabLayout tabs = findViewById(R.id.packages_tabs);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> PackagesActivity.this.viewModel.updateTabName(tab, position)).attach();
    }


    /**
     * Method is called whenever the activity is closed.
     */
    @Override
    public void finish() {
        Intent intent = new Intent(PackagesActivity.this, AddEntryActivity.class);
        SerializablePackageCollection packages = new SerializablePackageCollection(viewModel.getPackages());
        intent.putExtra(PackagesActivity.KEY_PACKAGES, packages);
        setResult(RESULT_OK, intent);
        super.finish();
    }

}
