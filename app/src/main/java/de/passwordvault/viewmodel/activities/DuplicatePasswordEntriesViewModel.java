package de.passwordvault.viewmodel.activities;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.model.analysis.passwords.Password;


/**
 * Class implements a view model for the
 * {@link de.passwordvault.view.activities.DuplicatePasswordEntriesActivity}.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class DuplicatePasswordEntriesViewModel extends ViewModel {

    /**
     * Attribute stores the list of passwords that are duplicate.
     */
    private ArrayList<Password> passwords;


    /**
     * Constructor instantiates a new view model.
     */
    public DuplicatePasswordEntriesViewModel() {
        passwords = null;
    }


    /**
     * Method returns the list of duplicate passwords. This can be {@code null} if the list
     * was not set before.
     *
     * @return  List of duplicate passwords.
     */
    public ArrayList<Password> getPasswords() {
        return passwords;
    }

    /**
     * Method changes the list of duplicate passwords to the passed argument.
     *
     * @param passwords             List of duplicate passwords.
     * @throws NullPointerException The passed list is {@code null}.
     */
    public void setPasswords(ArrayList<Password> passwords) throws NullPointerException {
        if (passwords == null) {
            throw new NullPointerException();
        }
        this.passwords = passwords;
    }

}
