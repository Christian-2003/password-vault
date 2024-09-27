package de.passwordvault.view.entries.activity_packages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import de.passwordvault.R;
import de.passwordvault.model.packages.SerializablePackageCollection;
import de.passwordvault.view.entries.activity_add_entry.AddEntryActivity;
import de.passwordvault.view.entries.activity_packages.fragment_list.PackagesListFragment;
import de.passwordvault.view.entries.activity_packages.fragment_selected.PackagesSelectedFragment;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;


/**
 * Class implements the activity which shows a list of installed apps (packages) to the user, from
 * which the user can select one.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class PackagesActivity extends PasswordVaultActivity<PackagesViewModel> {

    /**
     * Field stores the key that needs to be used when passing the selected package as argument.
     */
    public static final String KEY_PACKAGES = "packages";

    /**
     * Attribute stores the adapter for the view pager displaying the tabs.
     */
    private PackagesFragmentStateAdapter adapter;

    private TextView appBarTextView;

    private EditText searchBarEditText;

    private ImageButton searchButton;


    /**
     * Constructor instantiates a new activity.
     */
    public PackagesActivity() {
        super(PackagesViewModel.class, R.layout.activity_packages);
    }


    /**
     * Method is called whenever a new view is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Process arguments:
        Bundle args = getIntent().getExtras();
        if (!viewModel.processArguments(args)) {
            finish();
            return;
        }

        //Setup app bar:
        appBarTextView = findViewById(R.id.text_appbar);
        appBarTextView.setVisibility(viewModel.getSearchQuery() == null ? View.VISIBLE : View.GONE);
        searchBarEditText = findViewById(R.id.input_search);
        searchBarEditText.setVisibility(viewModel.getSearchQuery() == null ? View.GONE : View.VISIBLE);
        searchBarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Fragment fragment = adapter.getItemAt(1);
                if (fragment instanceof PackagesListFragment) {
                    ((PackagesListFragment)fragment).filter(charSequence.toString());
                    Log.d("Filter", "Search query: " + charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Search button:
        searchButton = findViewById(R.id.button_search);
        searchButton.setOnClickListener(view -> enableSearch(true));

        //View pager:
        adapter = new PackagesFragmentStateAdapter(this);
        ViewPager2 viewPager = findViewById(R.id.packages_view_pager);
        viewPager.setAdapter(adapter);

        //Tab layout:
        TabLayout tabs = findViewById(R.id.packages_tabs);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> PackagesActivity.this.viewModel.updateTabName(tab, position)).attach();
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                searchButton.setVisibility(tab.getPosition() == 1 && viewModel.getSearchQuery() == null ? View.VISIBLE : View.GONE);
                if (tab.getPosition() == 1 && viewModel.getSearchQuery() != null) {
                    enableSearch(false);
                }
                if (tab.getPosition() == 0) {
                    disableSearch(false);
                    searchButton.setVisibility(View.GONE);
                }
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
        if (searchBarEditText.getVisibility() == View.VISIBLE) {
            disableSearch(true);
        }
        else {
            Intent intent = new Intent(PackagesActivity.this, AddEntryActivity.class);
            SerializablePackageCollection packages = new SerializablePackageCollection(viewModel.getSelectedPackages());
            intent.putExtra(PackagesActivity.KEY_PACKAGES, packages);
            setResult(RESULT_OK, intent);
            super.finish();
        }
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
     * Method disables the search bar.
     */
    private void disableSearch(boolean clearQuery) {
        searchBarEditText.setVisibility(View.GONE);
        appBarTextView.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        if (clearQuery) {
            viewModel.setSearchQuery(null);
            Fragment fragment = adapter.getItemAt(1);
            if (fragment instanceof PackagesListFragment) {
                ((PackagesListFragment)fragment).resetFilter();
            }
        }
        searchBarEditText.clearFocus();
        InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(searchBarEditText.getWindowToken(), 0);
    }

    /**
     * Method enables the search bar.
     */
    private void enableSearch(boolean focusSearchBar) {
        searchBarEditText.setVisibility(View.VISIBLE);
        appBarTextView.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        if (focusSearchBar) {
            viewModel.setSearchQuery("");
            searchBarEditText.setText(viewModel.getSearchQuery());
            searchBarEditText.requestFocus();
            InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.showSoftInput(searchBarEditText, 0);
        }
    }

}
