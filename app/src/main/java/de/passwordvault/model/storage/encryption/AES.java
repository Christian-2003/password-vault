package de.passwordvault.model.storage.encryption;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;


/**
 * Class implements the Advanced Encryption Standard AES using the {@linkplain Cipher}-class.
 * This class can be used to encrypt and decrypt messages using the AES algorithm.
 *
 * @author  Christian-2003
 * @version 2.2.1
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
     * Constant stores the tag with which messages shall be logged within the {@linkplain Log}.
     */
    private static final String TAG = "Security";


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
                Log.i(TAG, "Generated new secret key");
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
     * Method returns thw {@linkplain SecretKey} from the {@linkplain KeyStore}.
     *
     * @return                      Secret key retrieved from the key store.
     * @throws EncryptionException  The key could not be retrieved.
     */
    private SecretKey getKey() throws EncryptionException {
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
