package de.passwordvault.service.autofill.structureparser;

import android.view.autofill.AutofillId;


/**
 * Class models a parsed assist structure and contains the IDs for the remote views in which username
 * and password can be entered.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class ParsedStructure {

    /**
     * Attribute store the package name of the app from which the parsed structure originates.
     */
    private final String packageName;

    /**
     * Attribute stores the ID of the view in which a username can be entered.
     */
    private AutofillId usernameId;

    /**
     * Attribute stores the autofill hint used for the text input with which the username is entered.
     */
    private String usernameHint;

    /**
     * Attribute stores the entered text from the node used to enter the username.
     */
    private String usernameText;

    /**
     * Attribute stores the ID of thr view in which the password can be entered.
     */
    private AutofillId passwordId;

    /**
     * Attribute stores the autofill hint used for the text input with which the password is entered.
     */
    private String passwordHint;

    /**
     * Attribute stores the entered text from the node used to enter the password.
     */
    private String passwordText;


    /**
     * Constructor instantiates a new parsed structure without username or password IDs.
     */
    public ParsedStructure(String packageName) {
        this.packageName = packageName == null ? "" : packageName;
        usernameId = null;
        passwordId = null;
        usernameHint = "";
        passwordHint = "";
        usernameText = null;
        passwordText = null;
    }


    /**
     * Method returns the package name of the app from which the parsed assist structure originates.
     * This can never be {@code null}. In the worst case, this is an empty string.
     *
     * @return  Package name of the app.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Method returns the ID of the view in which a username can be entered. This can be {@code null}
     * if no view was found.
     *
     * @return  ID of the view in which a username can be entered.
     */
    public AutofillId getUsernameId() {
        return usernameId;
    }

    /**
     * Method changes the ID of the view in which a username can be entered.
     *
     * @param usernameId    New ID of the view.
     */
    public void setUsernameId(AutofillId usernameId) {
        this.usernameId = usernameId;
    }

    /**
     * Method returns the autofill hint used for the text input with which the username
     * is entered.
     *
     * @return  Autofill hint used to enter the username.
     */
    public String getUsernameHint() {
        return usernameHint;
    }

    /**
     * Method changes the autofill hint used for the text input with which the username
     * is entered.
     *
     * @param usernameHint          Autofill hint used to enter the username.
     * @throws NullPointerException The passed autofill hint is {@code null}.
     */
    public void setUsernameHint(String usernameHint) throws NullPointerException {
        if (usernameHint == null) {
            throw new NullPointerException();
        }
        this.usernameHint = usernameHint;
    }

    /**
     * Method returns the text entered within the text input used to enter the username.
     *
     * @return  Entered username.
     */
    public String getUsernameText() {
        return usernameText;
    }

    /**
     * Method changes the text entered within the text input used to enter the password.
     *
     * @param usernameText  Entered username.
     */
    public void setUsernameText(String usernameText) {
        this.usernameText = usernameText;
    }

    /**
     * Method returns the ID of the view in which a password can be entered. This can be {@code null}
     * if no view was found.
     *
     * @return  ID of the view in which a password can be entered.
     */
    public AutofillId getPasswordId() {
        return passwordId;
    }

    /**
     * Method changes the ID of the view in which a password can be entered.
     *
     * @param passwordId    New ID of the view.
     */
    public void setPasswordId(AutofillId passwordId) {
        this.passwordId = passwordId;
    }

    /**
     * Method returns the autofill hint used for the text input with which the password
     * is entered.
     *
     * @return  Autofill hint used to enter the password.
     */
    public String getPasswordHint() {
        return passwordHint;
    }

    /**
     * Method changes the autofill hint used for the text input with which the password
     * is entered.
     *
     * @param passwordHint          Autofill hint used to enter the password.
     * @throws NullPointerException The passed autofill hint is {@code null}.
     */
    public void setPasswordHint(String passwordHint) throws NullPointerException {
        if (passwordHint == null) {
            throw new NullPointerException();
        }
        this.passwordHint = passwordHint;
    }

    /**
     * Method returns the text entered within the text input used to enter the password.
     *
     * @return  Entered password.
     */
    public String getPasswordText() {
        return passwordText;
    }

    /**
     * Method changes the text entered within the text input used to enter the password.
     *
     * @param passwordText  Entered password.
     */
    public void setPasswordText(String passwordText) {
        this.passwordText = passwordText;
    }

}
