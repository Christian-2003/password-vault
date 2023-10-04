package de.passwordvault.backend.security.encryption;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


/**
 * Class implements the Advanced Encryption Standard AES using the {@linkplain Cipher}-class.
 * This class can be used to encrypt and decrypt messages using the AES algorithm.
 *
 * @author  Christian-2003
 * @version 2.0.0
 */
public class AES implements Encryptable {

    /**
     * Attribute stores the secret key that is used to encrypt / decrypt.
     */
    private SecretKey key;


    /**
     * Constructor constructs a new AES instance and generates a new {@linkplain SecretKey} for the
     * encryption.
     *
     * @throws EncryptionException  A new key could not be generated.
     */
    public AES() throws EncryptionException {
        generateKey();
    }

    /**
     * Constructor constructs a new AES instance which encrypts and decrypts with the specified
     * {@linkplain SecretKey}.
     *
     * @param key                   Key which shall be used for encryption and decryption.
     * @throws EncryptionException  They passed key could not be used.
     */
    public AES(SecretKey key) throws EncryptionException {
        setKey(key);
    }


    /**
     * Method returns the key that is used for encryption.
     *
     * @return  Key.
     */
    public SecretKey getKey() {
        return key;
    }

    /**
     * Method changes the key that is used for encryption.
     *
     * @param key                   New key.
     * @throws EncryptionException  The key could not be changed.
     */
    public void setKey(SecretKey key) throws EncryptionException {
        if (key == null) {
            throw new EncryptionException("Null is an invalid key");
        }
        this.key = key;
    }


    /**
     * Method encrypts the passed plain text through the respective encryption algorithm and returns
     * the encrypted text.
     *
     * @param plainText             Text to be encrypted.
     * @return                      Encrypted text.
     * @throws EncryptionException  The plain text could not be encrypted.
     */
    public String encrypt(String plainText) throws EncryptionException {
        if (plainText == null) {
            throw new EncryptionException("Null cannot be encrypted");
        }
        Cipher cipher;
        byte[] encrypted;
        try {
            cipher = getCipherInstance();
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(plainText.getBytes());
        }
        catch (Exception e) {
            throw new EncryptionException(e.getMessage());
        }
        return Base64.getEncoder().encodeToString(encrypted);
    }


    /**
     * Method decrypts the passed encrypted text through the respective decryption algorithm and
     * returns the decrypted text.
     *
     * @param encryptedText         Text to be decrypted.
     * @return                      Decrypted text.
     * @throws EncryptionException  The encrypted text could not be decrypted.
     */
    public String decrypt(String encryptedText) throws EncryptionException {
        if (encryptedText == null) {
            throw new EncryptionException("Null cannot be encrypted");
        }
        Cipher cipher;
        byte[] encrypted;
        try {
            cipher = getCipherInstance();
            cipher.init(Cipher.DECRYPT_MODE, key);
            encrypted = cipher.doFinal(encryptedText.getBytes());
        }
        catch (Exception e) {
            throw new EncryptionException(e.getMessage());
        }
        return Base64.getEncoder().encodeToString(encrypted);
    }


    /**
     * Method initiates the generation of a new key.
     *
     * @throws EncryptionException  A new key could not be generated.
     */
    public void generateKey() throws EncryptionException {
        try {
            key = KeyGenerator.getInstance("AES").generateKey();
        }
        catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(e.getMessage());
        }
    }


    /**
     * Method returns an instance of a {@linkplain Cipher} that is used to encrypt and decrypt
     * messages.
     *
     * @return                      Cipher-instance for encryption / decryption.
     * @throws EncryptionException  An instance could not be created.
     */
    private Cipher getCipherInstance() throws EncryptionException {
        try {
            return Cipher.getInstance("AES/GCM/NoPadding");
        }
        catch (Exception e) {
            throw new EncryptionException(e.getMessage());
        }
    }

}
