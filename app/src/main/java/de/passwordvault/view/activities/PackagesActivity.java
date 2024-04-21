package de.passwordvault.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import de.passwordvault.R;
import de.passwordvault.model.packages.SerializablePackageCollection;
import de.passwordvault.view.fragments.PackagesListFragment;
import de.passwordvault.view.fragments.PackagesSelectedFragment;
import de.passwordvault.view.utils.adapters.PackagesFragmentStateAdapter;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;
import de.passwordvault.viewmodel.activities.PackagesViewModel;


/**
 * Class implements the activity which shows a list of installed apps (packages) to the user, from
 * which the user can select one.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class PackagesActivity extends PasswordVaultBaseActivity {

    /**
     * Field stores the key that needs to be used when passing the selected package as argument.
     */
    public static final String KEY_PACKAGES = "packages";


    /**
     * Attribute stores the view model of the activity.
     */
    private PackagesViewModel viewModel;

    /**
     * Attribute stores the adapter for the view pager displaying the tabs.
     */
    private PackagesFragmentStateAdapter adapter;


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
            finish();
            return;
        }

        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        adapter = new PackagesFragmentStateAdapter(this);
        ViewPager2 viewPager = findViewById(R.id.packages_view_pager);
        viewPager.setAdapter(adapter);

        ImageButton searchButton = findViewById(R.id.button_search);
        searchButton.setOnClickListener(this::searchButtonClicked);

        TabLayout tabs = findViewById(R.id.packages_tabs);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> PackagesActivity.this.viewModel.updateTabName(tab, position)).attach();
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                searchButton.setVisibility(tab.getPosition() == 1 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        searchButton.setVisibility(tabs.getSelectedTabPosition() == 1 ? View.VISIBLE : View.GONE);
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


    /**
     * Method notifies the first fragment of changes to the items.
     */
    public void notifyPackageAdded() {
        Fragment fragment = adapter.getItemAt(0);
        if (fragment instanceof PackagesSelectedFragment) {
            ((PackagesSelectedFragment)fragment).notifyPackageAdded();
        }
    }


    /**
     * Method notifies the second fragment of changes to the items.
     */
    public void notifyPackageUnselected() {
        Fragment fragment = adapter.getItemAt(1);
        if (fragment instanceof PackagesListFragment) {
            ((PackagesListFragment)fragment).notifyPackageUnselected();
        }
    }


    /**
     * Method is called whenever the search-button is clicked.
     *
     * @param button    Button that was clicked.
     */
    private void searchButtonClicked(View button) {
        Fragment fragment = adapter.getItemAt(1);
        if (fragment instanceof PackagesListFragment) {
            PackagesListFragment listFragment = (PackagesListFragment)fragment;
            listFragment.searchButtonClicked();
        }
    }

}
