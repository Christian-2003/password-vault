package de.passwordvault.model.security.login;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Base64; //Do NOT use 'android.util.Base64', since this class is kinda broken. Crashes with strings of specific lengths for some reason!
import androidx.annotation.NonNull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import de.passwordvault.App;


/**
 * Class models a password.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class Password {

    /**
     * Field stores the hashing algorithm that shall be used when hashing passwords.
     */
    private static final String HASHING_ALGORITHM = "SHA-256";

    /**
     * Field stores the key with which the {@link #hashedPassword} is stored within
     * {@linkplain SharedPreferences}.
     */
    private static final String KEY_PASSWORD = "pw_hash";

    /**
     * Field stores the key with which the {@link #salt} is stored within
     * {@linkplain SharedPreferences}.
     */
    private static final String KEY_SALT = "pw_salt";

    /**
     * Field stores the length for the salt that shall be used when hashing a password.
     */
    private static final int SALT_LENGTH = 20;


    /**
     * Attribute stores the {@linkplain SharedPreferences}-file in which the password shall be stored.
     */
    private final String preferencesFile;

    /**
     * Attribute stores the hashed password.
     */
    private String hashedPassword;

    /**
     * Attribute stores the salt.
     */
    private String salt;


    /**
     * Constructor instantiates a new {@link Password}-instance. The data of the password will be
     * loaded from the specified {@linkplain SharedPreferences}.
     *
     * @param preferencesFile       Name of the SharedPreferences-file.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public Password(String preferencesFile) throws NullPointerException {
        if (preferencesFile == null) {
            throw new NullPointerException("Null is invalid file for SharedPreferences");
        }
        this.preferencesFile = preferencesFile;
        load();
    }


    /**
     * Method changes the password to the provided string. Calling this method cannot be undone.
     *
     * @param s                     New password.
     * @throws NullPointerException The provided password is {@code null}.
     */
    public void setPassword(String s) throws NullPointerException {
        if (s == null) {
            throw new NullPointerException("Null is invalid password");
        }
        salt = generateSalt();
        hashedPassword = hash(s, salt);
    }


    /**
     * Method returns whether this instance has a password.
     *
     * @return  Whether the instance has a password.
     */
    public boolean hasPassword() {
        return hashedPassword != null && salt != null;
    }


    /**
     * Method removes the password. The removal will be finished once {@link #save()} is called.
     */
    public void removePassword() {
        hashedPassword = null;
        salt = null;
    }


    /**
     * Method tests whether the passed String is identical to the password.
     *
     * @param s String to be tested with the password.
     * @return  Whether the passed String is identical to the password.
     */
    public boolean isPassword(String s) {
        if (hashedPassword == null || salt == null) {
            return false;
        }
        String hashed = hash(s, salt);
        return hashedPassword.equals(hashed);
    }


    /**
     * Method saves the password to the specified {@linkplain SharedPreferences}' file. If no password
     * is used, the data will be removed from the SharedPreferences.
     */
    public void save() {
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(preferencesFile, Context.MODE_PRIVATE).edit();

        if (hashedPassword != null && salt != null) {
            editor.putString(KEY_PASSWORD, hashedPassword);
            editor.putString(KEY_SALT, salt);
        }
        else {
            editor.remove(KEY_PASSWORD);
            editor.remove(KEY_SALT);
        }

        editor.apply();
    }


    /**
     * Method hashes the provided password with the provided salt. The hash is returned as String.
     *
     * @param s     Password to be hashed.
     * @param salt  Salt which shall be used to hash the password.
     * @return      Hashed password.
     */
    private String hash(String s, String salt) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(HASHING_ALGORITHM);
        }
        catch (NoSuchAlgorithmException e) {
            return s;
        }
        md.reset();
        md.update(toByteArray(salt));
        return toString(md.digest(s.getBytes(StandardCharsets.UTF_8)));
    }



    /**
     * Method generates a random salt for the password.
     *
     * @return  Salt for the password.
     */
    @NonNull
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[SALT_LENGTH];
        random.nextBytes(bytes);
        return toString(bytes);
    }


    /**
     * Method loads all data of the password from the {@linkplain SharedPreferences}.
     * If no password was stored previously, {@link #hashedPassword} and {@link #salt} will be
     * {@code null}.
     */
    private void load() {
        SharedPreferences preferences = App.getContext().getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);
        String password = preferences.getString(KEY_PASSWORD, null);
        String salt = preferences.getString(KEY_SALT, null);
        if (password != null && salt != null) {
            hashedPassword = password;
            this.salt = salt;
        }
        else {
            hashedPassword = null;
            this.salt = null;
        }
    }


    /**
     * Method converts the passed Base64-String into a byte-array.
     *
     * @param s Base64-String to be converted.
     * @return  Byte-array.
     */
    private byte[] toByteArray(String s) {
        return Base64.getDecoder().decode(s);
    }


    /**
     * Method converts the passed byte-array to a Base64-String.
     *
     * @param b Byte-array to be converted.
     * @return  Base64-String converted from the byte-array.
     */
    private String toString(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }

}
