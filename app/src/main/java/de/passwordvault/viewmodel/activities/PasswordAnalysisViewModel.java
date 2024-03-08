package de.passwordvault.viewmodel.activities;

import androidx.lifecycle.ViewModel;

import com.google.android.material.tabs.TabLayout;

import de.passwordvault.R;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;


/**
 * Class implements a view model for the {@link de.passwordvault.view.activities.PasswordAnalysisActivity}.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class PasswordAnalysisViewModel extends ViewModel {

    /**
     * Constructor instantiates a new view model.
     */
    public PasswordAnalysisViewModel() {

    }


    /**
     * Method updates the name of the passed tab at the specified position. The name is determined
     * based on the specified position within the tab bar.
     *
     * @param tab       Tab whose name to update.
     * @param position  Position of the tab within the tab bar.
     */
    public void updateTabName(TabLayout.Tab tab, int position) {
        switch (position) {
            case 0:
                tab.setText(R.string.password_analysis_menu_general);
                break;
            case 1:
                tab.setText(R.string.password_analysis_menu_list);
                break;
            case 2:
                tab.setText(R.string.password_analysis_menu_duplicates);
                break;
        }
    }

}
