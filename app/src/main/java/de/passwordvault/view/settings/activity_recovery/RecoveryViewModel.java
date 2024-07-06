package de.passwordvault.view.settings.activity_recovery;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.security.login.SecurityQuestion;


/**
 * Class implements the view model for the activity used to configure the master password recovery.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class RecoveryViewModel extends ViewModel {

    /**
     * Attribute stores the security questions used for master password recovery.
     */
    private ArrayList<SecurityQuestion> securityQuestions;


    /**
     * Constructor instantiates a new view model.
     */
    public RecoveryViewModel() {
        securityQuestions = null;
    }


    /**
     * Method returns the security questions. This is {@code null} if the security questions have
     * not been loaded. In this case, call {@link #loadSecurityQuestions()}.
     *
     * @return  List of security questions.
     */
    public ArrayList<SecurityQuestion> getSecurityQuestions() {
        return securityQuestions;
    }


    /**
     * Method loads the security questions.
     */
    public void loadSecurityQuestions() {
        securityQuestions = Account.getInstance().getSecurityQuestions();
    }


    /**
     * Method saves the changes to the security questions.
     */
    public void save() {
        Account.getInstance().setSecurityQuestions(securityQuestions);
        Account.getInstance().save();
    }

}
