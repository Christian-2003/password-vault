package de.passwordvault.view.settings.activity_recovery.dialog_security_question;

import android.os.Bundle;
import androidx.lifecycle.ViewModel;
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class implements the view model for the dialog through which to configure a security question with
 * which to recover the master password.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class SecurityQuestionViewModel extends ViewModel {

    /**
     * Attribute stores the callback listener for the dialog.
     */
    private DialogCallbackListener callbackListener;

    /**
     * Attribute stores the security question that is edited by the user.
     */
    private SecurityQuestion securityQuestion;

    /**
     * Attribute stores a list of all available security questions. This should include the question
     * within {@link #securityQuestion}. However, all other security questions that are used should
     * NOT be included in this array.
     */
    private String[] availableQuestions;

    /**
     * Attribute stores a list of all security questions that exist.
     */
    private final String[] allQuestions;

    /**
     * Attribute stores the question that is currently selected by the user from
     * {@link #availableQuestions}.
     */
    private int selectedQuestionIndex;


    /**
     * Constructor instantiates a new view model.
     */
    public SecurityQuestionViewModel() {
        callbackListener = null;
        securityQuestion = null;
        availableQuestions = null;
        allQuestions = SecurityQuestion.getAllQuestions();
        selectedQuestionIndex = -1;
    }


    /**
     * Method returns the callback listener for the dialog.
     *
     * @return  Callback listener.
     */
    public DialogCallbackListener getCallbackListener() {
        return callbackListener;
    }

    /**
     * Method returns the security question that is being edited by the dialog.
     *
     * @return  Security question.
     */
    public SecurityQuestion getSecurityQuestion() {
        return securityQuestion;
    }

    /**
     * Method returns an array of all available security questions.
     *
     * @return  Array of all available security questions.
     */
    public String[] getAvailableQuestions() {
        return availableQuestions;
    }

    /**
     * Method returns an array containing all security questions.
     *
     * @return  Array containing all security questions.
     */
    public String[] getAllQuestions() {
        return allQuestions;
    }

    /**
     * Method returns the index of the selected question within {@link #availableQuestions}.
     *
     * @return  Index of the selected question.
     */
    public int getSelectedQuestionIndex() {
        return selectedQuestionIndex;
    }

    /**
     * Method changes the index of the selected question within {@link #availableQuestions}.
     *
     * @param selectedQuestionIndex New index of the selected question.
     */
    public void setSelectedQuestionIndex(int selectedQuestionIndex) {
        this.selectedQuestionIndex = selectedQuestionIndex;
    }


    /**
     * Method processes the arguments passed to the dialog.
     *
     * @param args  Arguments to process.
     * @return      Whether the arguments were successfully processed.
     */
    public boolean processArguments(Bundle args) {
        if (args == null) {
            return false;
        }

        //Get callback listener:
        if (args.containsKey(SecurityQuestionDialog.KEY_CALLBACK_LISTENER)) {
            try {
                callbackListener = (DialogCallbackListener)args.getSerializable(SecurityQuestionDialog.KEY_CALLBACK_LISTENER);
            }
            catch (ClassCastException e) {
                return false;
            }
        }
        else {
            return false;
        }

        //Get available security questions:
        if (args.containsKey(SecurityQuestionDialog.KEY_QUESTIONS)) {
            try {
                availableQuestions = args.getStringArray(SecurityQuestionDialog.KEY_QUESTIONS);
                if (availableQuestions == null) {
                    return false;
                }
            }
            catch (ClassCastException e) {
                return false;
            }
        }
        else {
            return false;
        }

        //Get security question:
        if (args.containsKey(SecurityQuestionDialog.KEY_QUESTION)) {
            try {
                securityQuestion = (SecurityQuestion)args.getSerializable(SecurityQuestionDialog.KEY_QUESTION);
                if (securityQuestion == null) {
                    return false;
                }
                if (securityQuestion.getQuestion() != SecurityQuestion.NO_QUESTION) {
                    String question = allQuestions[securityQuestion.getQuestion()];
                    for (int i = 0; i < availableQuestions.length; i++) {
                        if (availableQuestions[i].equals(question)) {
                            selectedQuestionIndex = i;
                            break;
                        }
                    }
                }
            }
            catch (ClassCastException e) {
                securityQuestion = new SecurityQuestion();
            }
        }
        else {
            securityQuestion = new SecurityQuestion();
        }

        return true;
    }


    /**
     * Method determines the index of the security question within {@link #allQuestions} based on the
     * passed index of the selected question within {@link #availableQuestions}.
     *
     * @param index Index of the selected question in {@link #availableQuestions}.
     * @return      Index of the selected question within {@link #allQuestions}.
     */
    public int getIndexFromSelectedQuestion(int index) {
        if (index < 0 || index >= availableQuestions.length) {
            return SecurityQuestion.NO_QUESTION;
        }
        String selectedQuestion = availableQuestions[index];
        for (int i = 0; i < allQuestions.length; i++) {
            if (allQuestions[i].equals(selectedQuestion)) {
                return i;
            }
        }
        return SecurityQuestion.NO_QUESTION;
    }

}
