package de.passwordvault.model.security.login;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import de.passwordvault.App;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.csv.CsvConfiguration;
import de.passwordvault.model.storage.encryption.AES;
import de.passwordvault.model.storage.encryption.EncryptionException;


/**
 * Class models an account for the application login. The class uses singleton-pattern, since only
 * one account can exist within the scope of the application.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class Account {

    /**
     * Attribute stores the instance of this account for the singleton-pattern.
     */
    private static Account singleton;

    /**
     * Field stores the file which is used to store the account data within
     * {@linkplain SharedPreferences}.
     */
    private static final String PREFERENCES_FILE = "security";

    /**
     * Field stores the key which shall be used when storing {@link #biometrics} within
     * {@linkplain SharedPreferences}.
     */
    private static final String KEY_BIOMETRICS = "biometrics";

    /**
     * Field stores the key which shall be used when storing {@link #securityQuestions} within
     * {@linkplain SharedPreferences}.
     */
    private static final String KEY_SECURITY_QUESTIONS = "security_questions";


    /**
     * Attribute stores whether the user wants to login with biometrics.
     */
    private boolean biometrics;

    /**
     * Attribute stores the password of the account.
     */
    private final Password password;

    /**
     * Attribute stores the security questions that were provided by the user to restore the master
     * password. If no security questions are available, this is {@code null} or empty.
     */
    private ArrayList<SecurityQuestion> securityQuestions;


    /**
     * Constructor instantiates a new {@link Account}-instance.
     */
    private Account() {
        load();
        password = new Password(PREFERENCES_FILE);
        securityQuestions = null;
    }


    /**
     * Static method returns the singleton-instance of the {@link Account}-class.
     *
     * @return  Singleton-instance of the class.
     */
    public static Account getInstance() {
        if (singleton == null) {
            singleton = new Account();
        }
        return singleton;
    }


    /**
     * Method returns whether the login shall be done through biometrics.
     *
     * @return  Whether the login shall be done through biometrics.
     */
    public boolean useBiometrics() {
        return biometrics;
    }

    /**
     * Method changes whether the login shall be done through biometrics.
     *
     * @param biometrics    Whether the login shall be done through biometrics.
     */
    public void setBiometrics(boolean biometrics) {
        this.biometrics = biometrics;
    }

    /**
     * Method changes the password of the account to the specified argument.
     *
     * @param s                     New password for the account.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setPassword(String s) throws NullPointerException {
        password.setPassword(s);
    }

    /**
     * Method returns the security questions of the account. If no security questions are available,
     * an empty list is returned.
     *
     * @return  Security questions of the account.
     */
    public ArrayList<SecurityQuestion> getSecurityQuestions() {
        if (securityQuestions == null) {
            securityQuestions = new ArrayList<>();
            SharedPreferences preferences = App.getContext().getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
            if (preferences.contains(KEY_SECURITY_QUESTIONS)) {
                String encryptedQuestions = preferences.getString(KEY_SECURITY_QUESTIONS, "");
                AES cipher = new AES();
                String decryptedQuestions = null;
                try {
                    decryptedQuestions = cipher.decrypt(encryptedQuestions);
                }
                catch (EncryptionException e) {
                    //Ignore...
                }
                if (decryptedQuestions != null) {
                    String[] rows = decryptedQuestions.split("" + CsvConfiguration.ROW_DIVIDER);
                    for (String row : rows) {
                        SecurityQuestion securityQuestion = new SecurityQuestion();
                        try {
                            securityQuestion.fromStorable(row);
                        }
                        catch (StorageException e) {
                            continue;
                        }
                        securityQuestions.add(securityQuestion);
                    }
                }
            }
        }
        return securityQuestions;
    }


    /**
     * Method changes the security questions for the account to the passed argument. Pass an empty
     * list of {@code null} to remove all security questions.
     *
     * @param securityQuestions List of security questions for the account.
     */
    public void setSecurityQuestions(ArrayList<SecurityQuestion> securityQuestions) {
        if (securityQuestions == null) {
            this.securityQuestions = new ArrayList<>();
        }
        else {
            this.securityQuestions = securityQuestions;
        }
    }


    /**
     * Method returns whether the account has a password.
     *
     * @return  Whether the application has a password.
     */
    public boolean hasPassword() {
        return password.hasPassword();
    }


    /**
     * Method removes the account from the device. This cannot be undone since this change is
     * immediately applied to the {@linkplain SharedPreferences}.
     */
    public void removeAccount() {
        password.removePassword();
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.remove(KEY_BIOMETRICS);
        editor.apply();
        password.save();
    }


    /**
     * Method tests whether the provided String matches the password of the account.
     *
     * @param s String to be tested.
     * @return  Whether the passed String is equal to the password.
     */
    public boolean isPassword(String s) {
        return password.isPassword(s);
    }


    /**
     * Method saves all data to {@linkplain SharedPreferences}.
     */
    public void save() {
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_BIOMETRICS, biometrics);
        password.save();
        if (securityQuestions != null) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < securityQuestions.size(); i++) {
                builder.append(securityQuestions.get(i).toStorable());
                if (i < securityQuestions.size() - 1) {
                    builder.append(CsvConfiguration.ROW_DIVIDER);
                }
            }
            AES cipher = new AES();
            String encryptedSecurityQuestion;
            try {
                encryptedSecurityQuestion = cipher.encrypt(builder.toString());
            }
            catch (EncryptionException e) {
                return;
            }
            editor.putString(KEY_SECURITY_QUESTIONS, encryptedSecurityQuestion);
        }
        editor.apply();
    }


    /**
     * Method loads all data from {@linkplain SharedPreferences}.
     */
    private void load() {
        SharedPreferences preferences = App.getContext().getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        biometrics = preferences.getBoolean(KEY_BIOMETRICS, false);
    }

}
