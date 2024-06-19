package de.passwordvault.viewmodel.activities;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.Collections;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.security.login.SecurityQuestion;


/**
 * Class implements the view model for the activity through which to enter the answers to security
 * questions in order to restore the master password.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class SecurityQuestionViewModel extends ViewModel {

    /**
     * Attribute stores the security questions that are being displayed to the user.
     */
    private ArrayList<SecurityQuestion> questions;


    /**
     * Constructor instantiates a new view model.
     */
    public SecurityQuestionViewModel() {
        questions = null;
    }


    /**
     * Method returns a list of randomly selected security questions for the user to answer.
     *
     * @return  List of selected questions.
     */
    public ArrayList<String> getQuestions() {
        if (questions == null) {
            ArrayList<SecurityQuestion> allQuestions = new ArrayList<>(Account.getInstance().getSecurityQuestions());
            Collections.shuffle(allQuestions);
            if (allQuestions.size() > Account.REQUIRED_SECURITY_QUESTIONS) {
                for (int i = 0; i < Account.REQUIRED_SECURITY_QUESTIONS; i++) {
                    questions.add(allQuestions.get(i));
                }
            }
            questions = allQuestions;
        }
        ArrayList<String> questionStrings = new ArrayList<>();
        String[] allQuestionStrings = SecurityQuestion.getAllQuestions();
        for (SecurityQuestion q : questions) {
            questionStrings.add(allQuestionStrings[q.getQuestion()]);
        }
        return questionStrings;
    }

}
