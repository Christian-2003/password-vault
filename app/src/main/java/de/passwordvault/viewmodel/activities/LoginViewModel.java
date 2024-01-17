package de.passwordvault.viewmodel.activities;

import androidx.lifecycle.ViewModel;

import de.passwordvault.model.security.login.Account;


/**
 * Class implements the {@linkplain ViewModel} for the
 * {@link de.passwordvault.view.activities.LoginActivity}.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class LoginViewModel extends ViewModel {

    /**
     * Method tests whether the provided password is correct.
     *
     * @param s Password to be tested.
     * @return  Whether the password matches.
     */
    public boolean confirmPassword(String s) throws NullPointerException {
        return Account.getInstance().isPassword(s);
    }

}
