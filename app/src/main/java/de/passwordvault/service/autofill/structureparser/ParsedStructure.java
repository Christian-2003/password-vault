package de.passwordvault.service.autofill.structureparser;

import android.view.autofill.AutofillId;


/**
 * Class models a parsed assist structure and contains the IDs for the remote views in which username
 * and password can be entered.
 *
 * @author  Christian-2003
 * @version 3.5.0
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
     * Attribute stores the ID of thr view in which the password can be entered.
     */
    private AutofillId passwordId;


    /**
     * Constructor instantiates a new parsed structure without username or password IDs.
     */
    public ParsedStructure(String packageName) {
        this.packageName = packageName == null ? "" : packageName;
        usernameId = null;
        passwordId = null;
    }

    /**
     * Constructor instantiates a new parsed structure with the passed username and password IDs.
     *
     * @param packageName   Name of the package from which the assist structure originates.
     * @param usernameId    ID for the view in which a username can be entered.
     * @param passwordId    ID for the view in which a password can be entered.
     */
    public ParsedStructure(String packageName, AutofillId usernameId, AutofillId passwordId) {
        this.packageName = packageName == null ? "" : packageName;
        this.usernameId = usernameId;
        this.passwordId = passwordId;
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

}
