package de.passwordvault.view.passwords.activity_analysis;

import androidx.lifecycle.ViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;


/**
 * Class implements a view model for the {@link PasswordAnalysisActivityDeprecated}.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class PasswordAnalysisViewModelDeprecated extends ViewModel {

    /**
     * Attribute stores a list of all weak passwords.
     */
    private ArrayList<Password> weakPasswords;


    /**
     * Constructor instantiates a new view model.
     */
    public PasswordAnalysisViewModelDeprecated() {
        weakPasswords = null;
    }


    /**
     * Method returns a list with all weak passwords.
     *
     * @return  List with all weak passwords.
     */
    public ArrayList<Password> getWeakPasswords() {
        if (weakPasswords == null) {
            weakPasswords = new ArrayList<>();
            calculateAllWeakPasswords();
        }
        return weakPasswords;
    }

    /**
     * Method calculates which passwords are considered to be weak. These are all passwords which passed
     * less than 50 % of all quality gates.
     */
    public void calculateAllWeakPasswords() {
        weakPasswords.clear();
        int requiredGatesToNotPass = (int)Math.round((double) QualityGateManager.getInstance().numberOfQualityGates() * 0.5D);
        for (Password password : PasswordSecurityAnalysis.getInstance().getData()) {
            if (password.getSecurityScore() <= requiredGatesToNotPass) {
                weakPasswords.add(password);
            }
        }
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
                tab.setText(R.string.password_analysis_menu_weak);
                break;
            case 2:
                tab.setText(R.string.password_analysis_menu_duplicates);
                break;
        }
    }

}
