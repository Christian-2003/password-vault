package de.passwordvault.model.security.login;

import androidx.annotation.StringRes;
import de.passwordvault.App;
import de.passwordvault.R;


/**
 * Enum contains all available security questions.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public enum SecurityQuestions {

    /**
     * Field stores the 1st security question.
     */
    QUESTION_1(R.string.security_question_1),

    /**
     * Field stores the 2nd security question.
     */
    QUESTION_2(R.string.security_question_2),

    /**
     * Field stores the 3rd security question.
     */
    QUESTION_3(R.string.security_question_3),

    /**
     * Field stores the 4th security question.
     */
    QUESTION_4(R.string.security_question_4),

    /**
     * Field stores the 5th security question.
     */
    QUESTION_5(R.string.security_question_5),

    /**
     * Field stores the 6th security question.
     */
    QUESTION_6(R.string.security_question_6),

    /**
     * Field stores the 7th security question.
     */
    QUESTION_7(R.string.security_question_7),

    /**
     * Field stores the 8th security question.
     */
    QUESTION_8(R.string.security_question_8),

    /**
     * Field stores the 9th security question.
     */
    QUESTION_9(R.string.security_question_9),

    /**
     * Field stores the 10th security question.
     */
    QUESTION_10(R.string.security_question_10),

    /**
     * Field stores the 11th security question.
     */
    QUESTION_11(R.string.security_question_11),

    /**
     * Field stores the 12th security question.
     */
    QUESTION_12(R.string.security_question_12),

    /**
     * Field stores the 13th security question.
     */
    QUESTION_13(R.string.security_question_13);


    /**
     * Attribute stores the ID of the string of the security question.
     */
    @StringRes
    private final int question;


    /**
     * Constructor instantiates a new security question.
     *
     * @param question  ID of the string resource which contains the security question.
     */
    SecurityQuestions(@StringRes int question) {
        this.question = question;
    }


    /**
     * Method returns the question.
     *
     * @return  Question.
     */
    public String getQuestion() {
        return App.getContext().getString(question);
    }

}
