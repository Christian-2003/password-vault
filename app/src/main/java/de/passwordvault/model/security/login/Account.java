package de.passwordvault.model.security.login;

import android.content.Context;
import android.content.SharedPreferences;
import de.passwordvault.App;


/**
 * Class models an account for the application login. The class uses singleton-pattern, since only
 * one account can exist within the scope of the application.
 *
 * @author  Christian-2003
 * @version 3.2.0
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
     * Attribute stores whether the user wants to login with biometrics.
     */
    private boolean biometrics;

    /**
     * Attribute stores the password of the account.
     */
    private final Password password;


    /**
     * Constructor instantiates a new {@link Account}-instance.
     */
    private Account() {
        load();
        password = new Password(PREFERENCES_FILE);
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
        editor.apply();
        password.save();
    }


    /**
     * Method loads all data from {@linkplain SharedPreferences}.
     */
    private void load() {
        SharedPreferences preferences = App.getContext().getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        biometrics = preferences.getBoolean(KEY_BIOMETRICS, false);
    }

}
