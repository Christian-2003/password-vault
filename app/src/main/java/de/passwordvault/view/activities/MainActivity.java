package de.passwordvault.view.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationBarView;
import com.supersuman.apkupdater.ApkUpdater;

import de.passwordvault.R;
import de.passwordvault.model.UpdateManager;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;
import de.passwordvault.viewmodel.activities.MainViewModel;


/**
 * Class implements the MainActivity for this application. This MainActivity resembles the starting
 * point for the application and contains multiple fragments with different functionalities.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class MainActivity extends PasswordVaultBaseActivity implements NavigationBarView.OnItemSelectedListener, OnRecyclerItemClickListener<EntryAbbreviated> {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} for this activity.
     */
    private MainViewModel viewModel;

    /**
     * Attribute stores the activity result launcher used to start the activity which shows an entry.
     */
    private final ActivityResultLauncher<Intent> showEntryLauncher;

    /**
     * Attribute stores the activity result launcher used to start the activity through which to add
     * a new entry.
     */
    private final ActivityResultLauncher<Intent> addEntryLauncher;

    /**
     * Attribute stores the navigation bar / navigation rail of the activity.
     */
    private NavigationBarView navigationBarView;


    /**
     * Constructor instantiates a new activity.
     */
    public MainActivity() {
        //Show entry:
        showEntryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null && (result.getResultCode() == EntryActivity.RESULT_EDITED || result.getResultCode() == EntryActivity.RESULT_DELETED)) {
                viewModel.getHomeFragment().update(EntryManager.getInstance());
                viewModel.getEntriesFragment().updateDataset();
            }
        });

        //Add entry:
        addEntryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.d("AddEntry", "MainActivity: ResultCode=" + result.getResultCode());
            if (result != null && result.getResultCode() == RESULT_OK) {
                viewModel.getHomeFragment().update(EntryManager.getInstance());
                viewModel.getEntriesFragment().updateDataset();
            }
        });
    }


    /**
     * Method is called whenever a menu item in the {@linkplain NavigationBarView} is selected and
     * changes the view that is displayed within this MainActivity to the corresponding fragment.
     *
     * @param item  Menu item that was selected.
     * @return      Whether clicked menu item could change the fragment successfully.
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return switchToFragment(item.getItemId(), false);
    }


    /**
     * Method switches to the fragment of the passed id. This method is needed for fragments of this
     * activity to change the displayed fragment. This will update the {@linkplain NavigationBarView}
     * to display the item that is switched to as "selected".
     *
     * @param id    Id of the fragment to which to switch.
     * @return      Whether the fragment was switched successfully.
     */
    public boolean switchToFragment(int id) {
        return switchToFragment(id, true);
    }


    /**
     * Method is called whenever an entry within any recycler view is clicked.
     *
     * @param item      Clicked item.
     * @param position  Index of the clicked item.
     */
    @Override
    public void onItemClick(EntryAbbreviated item, int position) {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("uuid", item.getUuid());
        try {
            showEntryLauncher.launch(intent);
        }
        catch (Exception e) {
            Toast.makeText(this, "Cannot show entry", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Method starts the activity which allows the user to add a new entry.
     */
    public void addNewEntry() {
        Intent intent = new Intent(this, AddEntryActivity.class);
        try {
            addEntryLauncher.launch(intent);
        }
        catch (Exception e) {
            Toast.makeText(this, "Cannot add new entry", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Method is called whenever the MainActivity is created or recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        //Add action listener to FAB:
        findViewById(R.id.main_fab).setOnClickListener(view -> addNewEntry());

        //Configure navigation bar:
        navigationBarView = findViewById(R.id.main_navigation);
        navigationBarView.setOnItemSelectedListener(this);
        navigationBarView.setSelectedItemId(viewModel.getSelectedItem());

        //Check for updates:
        UpdateManager.getInstance(this, this::onUpdateStatusChanged);
    }


    /**
     * Method is called whenever the MainActivity is destroyed. This is usually done whenever the
     * application is closed.
     */
    @Override
    protected void onStop() {
        super.onStop();
        //Sometimes, when the app crashes (to me, this only happened during testing and development -
        //never production!), all data is, for some strange reason that I do not comprehend or understand,
        //deleted from the EntryManager. Therefore, all data would be lost when the app crashes. Not
        //saving data when the EntryManager is empty prevents this from happening. However, this also
        //prevents the user from manually deleting the last existing entry. As for now, I am willing
        //to accept this drawback.
        if (!EntryManager.getInstance().isEmpty()) {
            try {
                EntryManager.getInstance().save();
            }
            catch (Exception e) {
                Log.d("EntryManager", "Could not save entries from MainActivity: " + e.getMessage());
            }
        }
    }


    /**
     * Method switches to the fragment of the passed id. This method is needed for fragments of this
     * activity to change the displayed fragment. Pass {@code true} as argument {@code updateUi}
     * if the {@link NavigationBarView} shall be updated by this method. This is not required if
     * the method is called when the user clicks on the respective item.
     *
     * @param id        Id of the fragment to which to switch.
     * @param updateUi  Whether the UI shall be updated or not.
     * @return          Whether the fragment was switched successfully.
     */
    private boolean switchToFragment(int id, boolean updateUi) {
        switch (id) {
            case R.id.menu_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, viewModel.getHomeFragment()).commit();
                viewModel.setSelectedItem(R.id.menu_home);
                if (updateUi) {
                    ((NavigationBarView)findViewById(R.id.main_navigation)).setSelectedItemId(R.id.menu_home);
                }
                return true;
            case R.id.menu_entries:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, viewModel.getEntriesFragment()).commit();
                viewModel.setSelectedItem(R.id.menu_entries);
                if (updateUi) {
                    ((NavigationBarView)findViewById(R.id.main_navigation)).setSelectedItemId(R.id.menu_entries);
                }
                return true;

            case R.id.menu_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, viewModel.getSettingsFragment()).commit();
                viewModel.setSelectedItem(R.id.menu_settings);
                if (updateUi) {
                    ((NavigationBarView)findViewById(R.id.main_navigation)).setSelectedItemId(R.id.menu_settings);
                }
                return true;
        }
        return false;
    }


    /**
     * Method is called when the update state is registered the first time.
     *
     * @param updateAvailable   Whether an update is available.
     */
    private void onUpdateStatusChanged(boolean updateAvailable) {
        if (updateAvailable) {
            navigationBarView.getOrCreateBadge(R.id.menu_settings);
        }
    }

}
