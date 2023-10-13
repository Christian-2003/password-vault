package de.passwordvault.backend.security.file;

import android.content.Context;
import androidx.security.crypto.EncryptedFile;


/**
 * Class models a FileAccessor which provides basic methods for subclasses to read from and write to
 * {@linkplain EncryptedFile}s.
 *
 * @author  Christian-2003
 * @version 2.0.1
 */
public abstract class EncryptedFileAccessor {

    /**
     * Attribute stores the {@linkplain Context} in which the {@link EncryptedFileAccessor} operates.
     */
    protected Context context;


    /**
     * Constructor instantiates a new {@link EncryptedFileAccessor} which operates within the specified
     * {@linkplain Context}.
     *
     * @param context               Context for the FileAccessor.
     * @throws NullPointerException The passed Context is {@code null}.
     */
    public EncryptedFileAccessor(Context context) throws NullPointerException {
        if (context == null) {
            throw new NullPointerException("Null is invalid Context");
        }
        this.context = context;
    }

}
