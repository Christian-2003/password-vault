package de.passwordvault.viewmodel.fragments;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;
import de.passwordvault.model.analysis.passwords.Password;


/**
 * Class implements a view model for the
 * {@link de.passwordvault.view.fragments.PasswordAnalysisDuplicatesFragment}.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class PasswordAnalysisDuplicatesViewModel extends ViewModel {

    /**
     * Attribute stores the list of duplicate passwords.
     */
    private final ArrayList<Password> passwords;


    /**
     * Constructor instantiates a new view model.
     */
    public PasswordAnalysisDuplicatesViewModel() {
        passwords = new ArrayList<>();
        for (ArrayList<Password> password : PasswordSecurityAnalysis.getInstance().getIdenticalPasswords()) {
            passwords.add(password.get(0));
        }
    }


    /**
     * Method returns a list of passwords that are duplicate.
     *
     * @return  List of duplicate passwords.
     */
    public ArrayList<Password> getPasswords() {
        return passwords;
    }

}
