package de.passwordvault.backend.security.encryption;


import javax.crypto.SecretKey;

/**
 * Interface defines methods that need to be implemented by all encryption / decryption algorithms
 * for the application.
 * This interface is not suited for the implementation of asymmetrical algorithms!
 *
 * @author  Christian-2003
 * @version 2.0.0
 */
public interface Encryptable {

    /**
     * Method encrypts the passed plain text through the respective encryption algorithm and returns
     * the encrypted text.
     *
     * @param plainText             Text to be encrypted.
     * @return                      Encrypted text.
     * @throws EncryptionException  The plain text could not be encrypted.
     */
    String encrypt(String plainText) throws EncryptionException;


    /**
     * Method decrypts the passed encrypted text through the respective decryption algorithm and
     * returns the decrypted text.
     *
     * @param encryptedText         Text to be decrypted.
     * @return                      Decrypted text.
     * @throws EncryptionException  The encrypted text could not be decrypted.
     */
    String decrypt(String encryptedText) throws EncryptionException;


    /**
     * Method returns the key that is used for encryption.
     *
     * @return  Key.
     */
    SecretKey getKey();

    /**
     * Method changes the key that is used for encryption.
     *
     * @param key                   New key.
     * @throws EncryptionException  The key could not be changed.
     */
    void setKey(SecretKey key) throws EncryptionException;


    /**
     * Method initiates the generation of a new key.
     *
     * @throws EncryptionException  A new key could not be generated.
     */
    void generateKey() throws EncryptionException;

}
