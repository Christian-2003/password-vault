package de.passwordvault.model.storage.encryption;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Class implements the Advanced Encryption Standard AES using the {@linkplain Cipher}-class.
 * This class can be used to encrypt and decrypt messages using the AES algorithm.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class AES {

    /**
     * Constant stores the provider for the key generation / retrieval.
     */
    private static final String PROVIDER = "AndroidKeyStore";

    /**
     * Constant stores the alias with which the {@linkplain SecretKey} is stored within the
     * {@linkplain KeyStore}.
     */
    private static final String KEY_ALIAS = "secret_key";


    /**
     * Attribute stores a key that was generated from a seed (provided by the user). If this key
     * is provided ({@code userProvidedKey != null}), this key shall be used for encryption and
     * decryption instead of the keys that are retrieved from the {@linkplain KeyStore}.
     */
    private final SecretKey userProvidedKey;


    /**
     * Constructor instantiates a new {@link AES}-instance which can encrypt and decrypt provided
     * data.
     * The key which is used for encryption / decryption is retrieved from the device's KeyStore. If
     * no key is present, a new key will be generated.
     */
    public AES() {
        userProvidedKey = null;
    }

    /**
     * Constructor instantiates a new {@link AES}-instance which can encrypt and decrypt provided
     * data.
     * The key which is used for encryption / decryption is generated from the passed seed. Pass
     * {@code null} if a key from the device's KeyStore shall be used instead.
     *
     * @param seed  Seed from which to generate a key. If this is {@code null}, a key retrieved from
     *              the KeyStore will be used instead.
     */
    public AES(String seed) {
        if (seed == null) {
            userProvidedKey = null;
            return;
        }
        //Hash the passed seed:
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            userProvidedKey = null;
            return;
        }
        byte[] hash = md.digest(seed.getBytes(StandardCharsets.UTF_8));
        userProvidedKey = new SecretKeySpec(hash, 0, 16, "AES/GCM/NoPadding");
    }


    /**
     * Method encrypts the passed plain text with the AES algorithm. The required
     * {@linkplain SecretKey} is automatically retrieved from the {@linkplain KeyStore}. If no key
     * exists, a new one is automatically generated.
     *
     * @param plainText             Plain text that shall be encrypted.
     * @return                      Encrypted text.
     * @throws EncryptionException  The plain text could not be encrypted.
     */
    public String encrypt(String plainText) throws EncryptionException {
        try {
            KeyStore keyStore = KeyStore.getInstance(PROVIDER);
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER);
                KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build();
                keyGenerator.init(spec);
                keyGenerator.generateKey();
            }

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), new SecureRandom());

            byte[] data = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] iv = cipher.getIV();
            byte[] encrypted = new byte[iv.length + data.length];
            System.arraycopy(iv, 0, encrypted, 0, iv.length);
            System.arraycopy(data, 0, encrypted, iv.length, data.length);

            return Base64.getEncoder().encodeToString(encrypted);
        }
        catch (Exception e) {
            throw new EncryptionException(e.getMessage());
        }
    }


    /**
     * Method decrypts the specified encrypted text with the AES algorithm. The required
     * {@linkplain SecretKey} is automatically retrieved from the {@linkplain KeyStore}. If no key
     * exists, an exception will be thrown.
     *
     * @param encryptedText         Encrypted text that shall be decrypted.
     * @return                      Decrypted text.
     * @throws EncryptionException  The encrypted text could not be decrypted.
     */
    public String decrypt(String encryptedText) throws EncryptionException {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] iv = new byte[12];
            System.arraycopy(encryptedBytes, 0, iv, 0, iv.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, getKey(), new GCMParameterSpec(128, iv));

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes, iv.length, encryptedBytes.length - iv.length);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new EncryptionException(e.getMessage());
        }
    }


    /**
     * Method returns the {@linkplain SecretKey} that shall be used for encryption and decryption.
     * This is either the {@link #userProvidedKey} or a key retrieved from the {@linkplain KeyStore}.
     *
     * @return                      Secret key retrieved that shall be used for encryption / decryption.
     * @throws EncryptionException  The key could not be retrieved from the KeyStore.
     */
    private SecretKey getKey() throws EncryptionException {
        if (userProvidedKey != null) {
            //Use key that was generated through a seed for encryption / decryption:
            return userProvidedKey;
        }
        try {
            KeyStore keyStore = KeyStore.getInstance(PROVIDER);
            keyStore.load(null);
            return (SecretKey) keyStore.getKey(KEY_ALIAS, null);
        }
        catch (Exception e) {
            throw new EncryptionException(e.getMessage());
        }
    }

}
