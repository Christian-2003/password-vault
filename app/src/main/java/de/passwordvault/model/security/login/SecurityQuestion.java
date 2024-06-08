package de.passwordvault.model.security.login;

import androidx.annotation.IdRes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import de.passwordvault.model.Identifiable;
import de.passwordvault.model.storage.app.Storable;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;


/**
 * Class implements a security question which can be provided by the user in case they forget the
 * master password.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class SecurityQuestion implements Identifiable, Storable, Serializable {

    /**
     * Attribute stores the UUID of the security question.
     */
    private String uuid;

    /**
     * Attribute stores the question of the security question.
     */
    private SecurityQuestions question;

    /**
     * Attribute stores the answer of the security question.
     */
    private String answer;

    /**
     * Attribute stores whether the security question is expanded while all security questions are
     * being shown.
     */
    private boolean expanded;


    /**
     * Constructor instantiates a new empty security question.
     */
    public SecurityQuestion() {
        uuid = UUID.randomUUID().toString();
        question = null;
        answer = "";
        expanded = false;
    }

    /**
     * Constructor instantiates a new security question with the passed question and answer.
     *
     * @param question  Question.
     * @param answer    Answer.
     */
    public SecurityQuestion(SecurityQuestions question, String answer) {
        uuid = UUID.randomUUID().toString();
        this.question = question;
        this.answer = answer;
        expanded = false;
    }


    /**
     * Method returns the string-representation of the UUID of the instance.
     *
     * @return  String-representation of the UUID.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Method returns the question.
     *
     * @return  Question.
     */
    public SecurityQuestions getQuestion() {
        return question;
    }

    /**
     * Method changes the question.
     *
     * @param question              New question.
     * @throws NullPointerException The passed question is {@code null}.
     */
    public void setQuestion(SecurityQuestions question) throws NullPointerException {
        if (question == null) {
            throw new NullPointerException();
        }
        this.question = question;
    }

    /**
     * Method returns the answer to the question.
     *
     * @return  Answer.
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Method changes the answer to the question.
     *
     * @param answer                New answer.
     * @throws NullPointerException The passed answer is {@code null}.
     */
    public void setAnswer(String answer) throws NullPointerException {
        if (answer == null) {
            throw new NullPointerException();
        }
        this.answer = answer;
    }


    /**
     * Method returns whether the security question is expanded when shown during configuration.
     *
     * @return  Whether the question is expanded.
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * Method changes whether the security question is expanded when shown during configuration.
     *
     * @param expanded  Whether the question is expanded.
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }


    /**
     * Method tests whether the passed answer matches the answer of the security question.
     *
     * @param answer    Answer to be tested.
     * @return          Whether both answers match.
     */
    public boolean matches(String answer) {
        if (answer == null) {
            return false;
        }
        return this.answer.equalsIgnoreCase(answer);
    }


    /**
     * Method converts this instance into a string-representation which can be used for persistent
     * storage. The generated string can later be parsed into a storable through
     * {@link #fromStorable(String)}.
     *
     * @return  String-representation of this instance.
     */
    @Override
    public String toStorable() {
        CsvBuilder builder = new CsvBuilder();
        builder.append(uuid);
        if (question == null) {
            builder.append("-1");
        }
        else {
            builder.append(question.ordinal());
        }
        builder.append(answer);
        return builder.toString();
    }

    /**
     * Method creates the instance from it's passed string-representation. The passed string - which
     * is a storable's string-representation - must be generated by {@link #toStorable()} beforehand.
     *
     * @param s                     String-representation from which to create the instance.
     * @throws StorageException     The passed string could not be converted into an instance.
     * @throws NullPointerException The passed string is {@code null}.
     */
    @Override
    public void fromStorable(String s) throws StorageException, NullPointerException {
        if (s == null) {
            throw new NullPointerException();
        }
        CsvParser parser = new CsvParser(s);
        ArrayList<String> columns = parser.parseCsv();
        if (columns.size() != 3) {
            throw new StorageException();
        }
        for (int i = 0; i < columns.size(); i++) {
            switch (i) {
                case 0:
                    uuid = columns.get(i);
                    break;
                case 1:
                    try {
                        int index = Integer.parseInt(columns.get(i));
                        if (index >= 0 && index < SecurityQuestions.values().length) {
                            question = SecurityQuestions.values()[index];
                        }
                    }
                    catch (NumberFormatException e) {
                        question = null;
                    }
                    break;
                case 2:
                    answer = columns.get(i);
                    break;
            }
        }
    }


    /**
     * Method explicitly tests whether the UUID of this instance is identical to the UUID
     * of the passed object (if it is an instance of {@link Identifiable}).
     *
     * @param obj   Object to be tested.
     * @return      If the passed object is an instance of Identifiable and it's UUID is identical
     *              to the UUID of this instance.
     */
    @Override
    public boolean equals(Identifiable obj) {
        return obj != null && obj.getUuid().equals(uuid);
    }

}
