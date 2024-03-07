package de.passwordvault.viewmodel.activities;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.model.analysis.passwords.Password;


/**
 * Class implements a view model for the
 * {@link de.passwordvault.view.activities.DuplicatePasswordsActivity}.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class DuplicatePasswordsViewModel extends ViewModel {

    /**
     * Attribute stores a list of duplicate passwords.
     */
    private ArrayList<ArrayList<Password>> passwords;


    /**
     * Constructor instantiates a new view model.
     */
    public DuplicatePasswordsViewModel() {
        passwords = null;
    }


    /**
     * Method returns the list of duplicate passwords.
     *
     * @return  List of duplicate passwords.
     */
    public ArrayList<ArrayList<Password>> getPasswords() {
        return passwords;
    }

    /**
     * Method changes the passwords.
     *
     * @param passwords             New list of duplicate passwords.
     * @throws NullPointerException The passed list is {@code null}.
     */
    public void setPasswords(ArrayList<ArrayList<Password>> passwords) throws NullPointerException {
        if (passwords == null) {
            throw new NullPointerException();
        }
        this.passwords = passwords;
    }

}
