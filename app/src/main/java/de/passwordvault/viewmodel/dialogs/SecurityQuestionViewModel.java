package de.passwordvault.viewmodel.dialogs;

import android.os.Bundle;
import androidx.lifecycle.ViewModel;
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.model.security.login.SecurityQuestions;
import de.passwordvault.view.dialogs.SecurityQuestionDialog;
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
     * Attribute stores a list of all available security question. This should include all unused
     * questions as well as the currently edited question.
     */
    private SecurityQuestions[] availableQuestions;

    /**
     * Attribute stores the question that is currently selected by the user.
     */
    private int selectedQuestionIndex;


    /**
     * Constructor instantiates a new view model.
     */
    public SecurityQuestionViewModel() {
        callbackListener = null;
        securityQuestion = null;
        availableQuestions = null;
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
    public SecurityQuestions[] getAvailableQuestions() {
        return availableQuestions;
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

        //Get security question:
        if (args.containsKey(SecurityQuestionDialog.KEY_QUESTION)) {
            try {
                securityQuestion = (SecurityQuestion)args.getSerializable(SecurityQuestionDialog.KEY_QUESTION);
            }
            catch (ClassCastException e) {
                securityQuestion = new SecurityQuestion();
            }
        }
        else {
            securityQuestion = new SecurityQuestion();
        }

        //Get available security questions:
        if (args.containsKey(SecurityQuestionDialog.KEY_QUESTIONS)) {
            int[] ordinals;
            try {
                ordinals = args.getIntArray(SecurityQuestionDialog.KEY_QUESTIONS);
            }
            catch (ClassCastException e) {
                return false;
            }
            if (ordinals == null) {
                return false;
            }
            availableQuestions = new SecurityQuestions[ordinals.length];
            SecurityQuestions[] allQuestions = SecurityQuestions.values();
            for (int i = 0; i < ordinals.length; i++) {
                if (ordinals[i] >= 0 && ordinals[i] < allQuestions.length) {
                    availableQuestions[i] = allQuestions[ordinals[i]];
                }
            }
        }
        else {
            return false;
        }

        return true;
    }

}
