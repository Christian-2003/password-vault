package de.passwordvault.view.settings.dialog_security_question;

import android.os.Bundle;
import androidx.lifecycle.ViewModel;
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements the view model for the dialog through which to configure a security question with
 * which to recover the master password.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class SecurityQuestionViewModel extends ViewModel {

    /**
     * Attribute stores the callback listener for the dialog.
     */
    private PasswordVaultBottomSheetDialog.Callback callback;

    /**
     * Attribute stores the security question that is edited by the user.
     */
    private SecurityQuestion question;

    /**
     * Attribute stores a list of all available security questions. This should include the question
     * within {@link #question}. However, all other security questions that are used should
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
        callback = null;
        question = null;
        availableQuestions = null;
        allQuestions = SecurityQuestion.getAllQuestions();
        selectedQuestionIndex = -1;
    }


    /**
     * Method returns the callback for the dialog.
     *
     * @return  Callback.
     */
    public PasswordVaultBottomSheetDialog.Callback getCallback() {
        return callback;
    }

    /**
     * Method returns the security question that is being edited by the dialog.
     *
     * @return  Security question.
     */
    public SecurityQuestion getQuestion() {
        return question;
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
    public void processArguments(Bundle args) {
        if (args == null) {
            return;
        }

        //Get callback:
        if (args.containsKey(SecurityQuestionDialog.ARG_CALLBACK)) {
            try {
                callback = (PasswordVaultBottomSheetDialog.Callback)args.getSerializable(SecurityQuestionDialog.ARG_CALLBACK);
            }
            catch (ClassCastException e) {
                //Ignore...
            }
        }

        //Get available security questions:
        if (args.containsKey(SecurityQuestionDialog.ARG_QUESTIONS)) {
            try {
                availableQuestions = args.getStringArray(SecurityQuestionDialog.ARG_QUESTIONS);
            }
            catch (ClassCastException e) {
                //Ignore...
            }
        }
        if (availableQuestions == null) {
            availableQuestions = new String[0];
        }

        //Get security question:
        if (args.containsKey(SecurityQuestionDialog.ARG_QUESTION)) {
            try {
                question = (SecurityQuestion)args.getSerializable(SecurityQuestionDialog.ARG_QUESTION);
                if (question == null) {
                    question = new SecurityQuestion();
                }
                if (question.getQuestion() != SecurityQuestion.NO_QUESTION) {
                    String question = allQuestions[this.question.getQuestion()];
                    for (int i = 0; i < availableQuestions.length; i++) {
                        if (availableQuestions[i].equals(question)) {
                            selectedQuestionIndex = i;
                            break;
                        }
                    }
                }
            }
            catch (ClassCastException e) {
                //Ignore...
            }
        }
        if (question == null) {
            question = new SecurityQuestion();
        }
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
