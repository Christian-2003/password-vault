package de.passwordvault.service.autofill.caching;

import de.passwordvault.model.storage.encryption.AES;
import de.passwordvault.model.storage.encryption.EncryptionException;


/**
 * Class models a cache item for the {@link ContentCache}. Items of the content cache are of the
 * following format:<br/>
 * ---------<br/>
 * <code>
 *     &lt;uuid&gt;;&lt;username&gt;,&lt;password&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;^^^^^^^^^^^^^^^^^^^^^ <- encrypted<br/>
 * </code>
 * ---------<br/>
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class ContentCacheItem extends CacheItem {

    /**
     * Attribute stores the username of the cache item.
     */
    private String username;

    /**
     * Attribute stores the password of the cache item.
     */
    private String password;

    /**
     * Attribute stores the name of the entry whose data is stored within this cache item.
     */
    private String entryName;


    /**
     * Constructor instantiates a new cache item for the passed identifier and content.
     *
     * @param identifier            Identifier for the cache item.
     * @param content               Content for the cache item.
     * @throws NullPointerException The passed identifier or content is {@code null}.
     */
    public ContentCacheItem(String identifier, String content) throws NullPointerException {
        super(identifier, content);
        username = null;
        password = null;
    }

    /**
     * Constructor instantiates a new cache item from the passed string representation. The passed
     * string representation must be generated through {@link #toString()} beforehand.
     *
     * @param s                     String representation of the cache item.
     * @throws NullPointerException The passed string representation is {@code null.}
     */
    public ContentCacheItem(String s) throws NullPointerException {
        super(s);
        username = null;
        password = null;
        entryName = null;
    }

    /**
     * Constructor instantiates a new cache item from the passed identifier, username and password.
     * Username and password can be {@code null}, in which case they will be set to an empty string
     * internally.
     *
     * @param identifier            Identifier for the cache item.
     * @param username              Username for the cache item.
     * @param password              Password for the cache item.
     * @param entryName             Name of the entry whose data is being stored in this cache item.
     * @throws NullPointerException The passed identifier or entry name is {@code null}.
     */
    public ContentCacheItem(String identifier, String username, String password, String entryName) throws NullPointerException {
        super(identifier, "");
        if (entryName == null) {
            throw new NullPointerException();
        }
        this.username = username == null ? "" : username;
        this.password = password == null ? "" : password;
        this.entryName = entryName;
        encryptCredentials();
    }


    /**
     * Method returns the password of the item.
     *
     * @return  Password of the item.
     */
    public String getPassword() {
        if (password == null) {
            parseCredentials();
        }
        return password;
    }

    /**
     * Method returns the username of the item.
     *
     * @return  Username of the item.
     */
    public String getUsername() {
        if (username == null) {
            parseCredentials();
        }
        return username;
    }

    public String getEntryName() {
        if (entryName == null) {
            parseCredentials();
        }
        return entryName;
    }

    /**
     * Method changes the username of the cache item.
     *
     * @param username              New username for the cache item.
     * @throws NullPointerException The passed username is {@code null}.
     */
    public void setUsername(String username) throws NullPointerException {
        if (username == null) {
            throw new NullPointerException();
        }
        this.username = username;
        encryptCredentials();
    }

    /**
     * Method changes the password of the cache item.
     *
     * @param password              New password for the cache item.
     * @throws NullPointerException The passed password is {@code null}.
     */
    public void setPassword(String password) throws NullPointerException {
        if (password == null) {
            throw new NullPointerException();
        }
        this.password = password;
        encryptCredentials();
    }

    /**
     * Method changes the name of the entry whose data is stored in this cache item.
     *
     * @param entryName             New entry name for the cache item.
     * @throws NullPointerException The passed entry name is {@code null}.
     */
    public void setEntryName(String entryName) throws NullPointerException {
        if (entryName == null) {
            throw new NullPointerException();
        }
        this.entryName = entryName;
        encryptCredentials();
    }

    /**
     * Method changes the credentials (i.e. username and password), as well as the name of the entry
     * whose data is being stored, of the cache item.
     *
     * @param username              New username for the cache item.
     * @param password              New password for the cache item.
     * @param entryName             New entry name for the cache item.
     * @throws NullPointerException The passed username or password is {@code null}.
     */
    public void setCredentials(String username, String password, String entryName) throws NullPointerException {
        if (username == null || password == null || entryName == null) {
            throw new NullPointerException();
        }
        this.username = username;
        this.password = password;
        this.entryName = entryName;
        encryptCredentials();
    }


    /**
     * Method encrypts the username and password and informs the superclass about the change in the
     * content. If either username or password are {@code null}, the old value is used instead.
     */
    public void encryptCredentials() {
        if (username == null && password == null && entryName == null) {
            return;
        }
        if (username == null || password == null || entryName == null) {
            String oldUsername = username;
            String oldPassword = password;
            String oldEntryName = entryName;
            parseCredentials();
            if (oldUsername != null) {
                username = oldUsername;
            }
            if (oldPassword != null) {
                password = oldPassword;
            }
            if (oldEntryName != null) {
                entryName = oldEntryName;
            }
        }
        AES aes = new AES();
        String decryptedCredentials = username + ',' + password + ',' + entryName;
        String encryptedCredentials = "";
        try {
            encryptedCredentials = aes.encrypt(decryptedCredentials);
        }
        catch (EncryptionException e) {
            return;
        }
        setContent(encryptedCredentials);
    }


    /**
     * Method parses the credentials and stores the result in {@link #username} and {@link #password}.
     * Both attributes are not {@code null} after the method finishes!
     */
    private void parseCredentials() {
        AES aes = new AES();
        String decryptedCredentials = "";
        try {
            decryptedCredentials = aes.decrypt(getContent());
        }
        catch (EncryptionException e) {
            //Ignore...
        }
        String[] parts = decryptedCredentials.split(",");
        if (parts.length >= 3) {
            username = parts[0];
            password = parts[1];
            entryName = parts[2];
        }
        else {
            username = "";
            password = "";
            entryName = "";
        }
    }

}
