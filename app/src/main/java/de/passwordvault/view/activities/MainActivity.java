package de.passwordvault.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationBarView;
import de.passwordvault.R;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.viewmodel.activities.MainViewModel;


/**
 * Class implements the MainActivity for this application. This MainActivity resembles the starting
 * point for the application and contains multiple fragments with different functionalities.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} for this activity.
     */
    private MainViewModel viewModel;


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
        findViewById(R.id.main_fab).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddEntryActivity.class)));

        //Configure bottom navigation bar:
        NavigationBarView navigationBarView = findViewById(R.id.main_navigation);
        navigationBarView.setOnItemSelectedListener(this);
        navigationBarView.setSelectedItemId(viewModel.getSelectedItem());
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
     * Method is called whenever the MainActivity is destroyed. This is usually done whenever the
     * application is closed.
     */
    @Override
    protected void onStop() {
        super.onStop();
        try {
            EntryManager.getInstance().save();
        }
        catch (Exception e) {
            Log.d("EntryManager", "Could not save entries from MainActivity: " + e.getMessage());
        }
    }

}
