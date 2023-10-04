package de.passwordvault.backend.security.file;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKeys;
import java.io.File;
import de.passwordvault.backend.security.GenericSecurityException;


/**
 * Class models a FileAccessor which provides basic methods for subclasses to read from and write to
 * {@linkplain EncryptedFile}s.
 *
 * @author  Christian-2003
 * @version 2.0.0
 */
public abstract class FileAccessor {

    /**
     * Attribute stores the {@linkplain Context} in which the {@link FileAccessor} operates.
     */
    protected Context context;


    /**
     * Constructor instantiates a new {@link FileAccessor} which operates within the specified
     * {@linkplain Context}.
     *
     * @param context               Context for the FileAccessor.
     * @throws NullPointerException The passed Context is {@code null}.
     */
    public FileAccessor(Context context) throws NullPointerException {
        if (context == null) {
            throw new NullPointerException("Null is invalid Context");
        }
        this.context = context;
    }


    /**
     * Method returns the main key alias for the encryption keys.
     *
     * @return                          Main key alias.
     * @throws GenericSecurityException The main key alias could not be generated.
     */
    protected String getMainKeyAlias() throws GenericSecurityException {
        try {
            KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
            return MasterKeys.getOrCreate(keyGenParameterSpec);
        }
        catch (Exception e) {
            throw new GenericSecurityException(e.getMessage());
        }
    }


    /**
     * Method returns the {@linkplain EncryptedFile} that is used for encrypted data storage.
     *
     * @param filename                  Name of the EncryptedFile to be returned.
     * @param mainKeyAlias              Main key alias for the EncryptedFile.
     * @return                          Generated EncryptedFile.
     * @throws NullPointerException     One of the passed arguments is {@code null}.
     * @throws GenericSecurityException The EncryptedFile could not be generated.
     */
    protected EncryptedFile getEncryptedFile(String filename, String mainKeyAlias) throws NullPointerException, GenericSecurityException {
        if (filename == null) {
            throw new NullPointerException("Filename cannot be null");
        }
        if (mainKeyAlias == null) {
            throw new NullPointerException("MainKeyAlias cannot be null");
        }
        try {
            return new EncryptedFile.Builder(new File(context.getFilesDir(), filename), context, mainKeyAlias, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB).build();
        }
        catch (Exception e) {
            throw new GenericSecurityException(e.getMessage());
        }
    }


    /**
     * Method returns the {@linkplain EncryptedFile} that is used for encrypted data storage.
     * The main key alias will be automatically generated through {@link #getMainKeyAlias()}.
     *
     * @param filename                  Name of the EncryptedFile to be returned.
     * @return                          Generated EncryptedFile.
     * @throws NullPointerException     The passed filename is {@code null}.
     * @throws GenericSecurityException The EncryptedFile could not be generated.
     */
    protected EncryptedFile getEncryptedFile(String filename) throws NullPointerException, GenericSecurityException {
        return getEncryptedFile(filename, getMainKeyAlias());
    }

}
