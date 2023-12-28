package de.passwordvault.model.analysis;


/**
 * Class implements an algorithm which determines the security of a password.
 *
 * @author  Christian-2003
 * @version 1.0.0
 */
public class PasswordSecurity {

    /**
     * Constant stores all special characters that are regarded when checking a password for special
     * characters as a quality gate.
     */
    private final static char[] SPECIAL_CHARACTERS = { '!', '"', '\'', '§', '$', '%', '%', '&', '/',
            '{', '(', '[', ')', ']', '=', '}', '?', '\\', '°', '^', '*', '+', '~', '#', ',', ';',
            '.', ':', '-', '_', '€', '>', '<', '|' };


    /**
     * Method analyzes the security of the specified password (in a very simple way). Afterwards,
     * a security score is returned which is determined as follows:
     * <ul>
     *     <li>+1 Point if password length >= 12</li>
     *     <li>+1 Point if password length >= 18</li>
     *     <li>+1 Point if password contains special characters</li>
     *     <li>+1 Point if password contains numbers</li>
     *     <li>+1 Point if password contains capital letters</li>
     * </ul>
     * A higher score (max: 5) determines a safer password.
     *
     * @param password  Password to be analyzed.
     * @return          Security score for the password.
     */
    public static int PERFORM_SECURITY_ANALYSIS(String password) {
        if (password == null) {
            return 0;
        }

        int passedQualityGates = 0; //Stores the number of quality gates the password passed.

        //Quality gate 1: Length:
        if (password.length() >= 12) {
            passedQualityGates++;
        }
        if (password.length() >= 18) {
            passedQualityGates++;
        }


        //Quality gate 2: Special Characters:
        for (char specialCharacter : SPECIAL_CHARACTERS) {
            if (password.contains(String.valueOf(specialCharacter))) {
                passedQualityGates++;
                break;
            }
        }

        //Quality gates 3 - 4: Numbers, Capital letters:
        boolean numberEncountered = false;
        boolean capitalLetterEncountered = false;
        for (int i = 0; i < password.length(); i++) {
            char current = password.charAt(i);
            if (!numberEncountered && current >= 48 && current <= 57) {
                numberEncountered = true;
                passedQualityGates++;
            }
            else if (!capitalLetterEncountered && current >= 65 && current <= 90) {
                capitalLetterEncountered = true;
                passedQualityGates++;
            }
        }

        return passedQualityGates;
    }

}
