package de.passwordvault.view.authentication.activity_security_question;

import androidx.annotation.Nullable;
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
 * @version 3.7.1
 */
public class SecurityQuestionViewModel extends ViewModel {

    /**
     * Attribute stores the security questions that are being displayed to the user.
     */
    private ArrayList<SecurityQuestion> questions;

    /**
     * Attribute stores the security questions the user needs to answer as string.
     */
    @Nullable
    private ArrayList<String> questionStrings;

    /**
     * Attribute stores whether the answers are valid.
     */
    private boolean answersValid;

    /**
     * Attribute stores whether {@link #areAnswersValid(ArrayList)} has ever been called.
     */
    private boolean answersTested;


    /**
     * Constructor instantiates a new view model.
     */
    public SecurityQuestionViewModel() {
        questions = null;
        questionStrings = null;
        answersValid = false;
        answersTested = false;
    }


    /**
     * Method returns a list of randomly selected security questions for the user to answer.
     *
     * @return  List of selected questions.
     */
    public ArrayList<String> getQuestions() {
        if (questions == null || questionStrings == null) {
            ArrayList<SecurityQuestion> allQuestions = new ArrayList<>(Account.getInstance().getSecurityQuestions());
            Collections.shuffle(allQuestions);
            if (allQuestions.size() > Account.REQUIRED_SECURITY_QUESTIONS) {
                questions = new ArrayList<>();
                for (int i = 0; i < Account.REQUIRED_SECURITY_QUESTIONS; i++) {
                    questions.add(allQuestions.get(i));
                }
            }
            else {
                questions = allQuestions;
            }
            questionStrings = new ArrayList<>();
            String[] allQuestionStrings = SecurityQuestion.getAllQuestions();
            for (SecurityQuestion q : questions) {
                questionStrings.add(allQuestionStrings[q.getQuestion()]);
            }
        }
        return this.questionStrings;
    }


    /**
     * Method tests whether the provided answers are correct. The index of each answer within the
     * passed list must correspond to the index of it's respective question within the list returned
     * by {@link #getQuestions()}.
     *
     * @param answers   List of answers to test.
     * @return          Whether the answers are correct.
     */
    public boolean areAnswersValid(ArrayList<String> answers) {
        if (answers.size() != questions.size()) {
            answersValid = false;
            answersTested = true;
            return false;
        }
        int numberOfCorrectAnswers = 0;
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i) == null) {
                continue;
            }
            if (answers.get(i).equalsIgnoreCase(questions.get(i).getAnswer())) {
                numberOfCorrectAnswers++;
            }
        }
        answersValid = numberOfCorrectAnswers >= Account.REQUIRED_CORRECT_ANSWERS;
        answersTested = true;
        return answersValid;
    }

    /**
     * Method returns whether the answers tested previously through {@link #areAnswersValid(ArrayList)}
     * are valid.
     *
     * @return  Whether the answers are valid.
     */
    public boolean areAnswersValid() {
        return answersValid;
    }

    /**
     * Method returns whether the validity of answers has ever been tested through calling
     * {@link #areAnswersValid(ArrayList)}.
     *
     * @return  Whether answers have ever been tested.
     */
    public boolean areAnswersTested() {
        return answersTested;
    }

}
