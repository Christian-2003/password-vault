package de.passwordvault.backend.security.file;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import de.passwordvault.backend.security.encryption.AES;
import de.passwordvault.backend.security.encryption.EncryptionException;


/**
 * Class models a FileWriter which can write to an encrypted file.
 *
 * @author  Christian-2003
 * @version 2.2.1
 */
public class EncryptedFileWriter extends EncryptedFileAccessor {

    /**
     * Constructor instantiates a new FileWriter with the specified Context.
     *
     * @param context               Context for the FileWriter.
     * @throws NullPointerException The specified Context is {@code null}.
     */
    public EncryptedFileWriter(Context context) throws NullPointerException {
        super(context);
    }


    /**
     * Method writes the content to the specified file.
     *
     * @param filename                  Name of the file to be written to.
     * @param content                   Content to write to the file.
     * @throws NullPointerException     The specified file name is {@code null}.
     * @throws EncryptionException      The file could not be written to due to encryption issues.
     */
    public boolean write(String filename, String content) throws NullPointerException, EncryptionException {
        if (filename == null || filename.isEmpty()) {
            throw new NullPointerException("Null is invalid filename");
        }

        //Setup AES encryption:
        AES aes = new AES();

        Log.d("Security", "Writing to file " + filename + ": " + content);
        content = aes.encrypt(content);
        Log.d("Security", "Encrypted to file " + filename + ": " + content);

        //Write file:
        File file = new File(context.getFilesDir(), filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                //Could not create new file:
                return false;
            }
        }
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
        }
        catch (IOException e) {
            //Could not write to file:
            return false;
        }
        return true;
    }

}
