package de.passwordvault.service.autofill;


/**
 * Class models user data that can be fetched from the app for the autofill service.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class UserData {

    /**
     * Attribute stores the username of the user data.
     * This does not necessarily need to be a username and can be an email address or whatever is
     * used for identification.
     */
    private final String username;

    /**
     * Attribute stores the password of the user data.
     */
    private final String password;


    /**
     * Constructor instantiates a new user data instance with the passed username and password. Both
     * values can explicitly be {@code null}.
     *
     * @param username  Username for the user data.
     * @param password  Password for the user data.
     */
    public UserData(String username, String password) {
        this.username = username;
        this.password = password;
    }


    /**
     * Method returns the username of the user data. This can return {@code null}.
     *
     * @return  Username of the user data.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Method returns the password of the user data. This can return {@code null}.
     *
     * @return  Password of the user data.
     */
    public String getPassword() {
        return password;
    }

}
