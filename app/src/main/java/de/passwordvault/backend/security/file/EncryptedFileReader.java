package de.passwordvault.backend.security.file;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import de.passwordvault.backend.security.encryption.AES;
import de.passwordvault.backend.security.encryption.EncryptionException;


/**
 * Class models a FileReader which can read an encrypted file.
 *
 * @author  Christian-2003
 * @version 2.2.1
 */
public class EncryptedFileReader extends EncryptedFileAccessor {


    /**
     * Constructor instantiates a new FileReader with the specified Context.
     *
     * @param context               Context for the FileReader.
     * @throws NullPointerException The specified Context is {@code null}.
     */
    public EncryptedFileReader(Context context) throws NullPointerException {
        super(context);
    }


    /**
     * Method reads the content of the specified file.
     *
     * @param filename                  Name of the file to be read.
     * @return                          Content of the read file.
     * @throws NullPointerException     The specified file name is {@code null}.
     * @throws EncryptionException      The file could not be read due to encryption issues.
     */
    public String read(String filename) throws NullPointerException, EncryptionException {
        if (filename == null || filename.isEmpty()) {
            throw new NullPointerException("Null is invalid filename");
        }

        //Setup AES encryption:
        AES aes = new AES();

        //Read file:
        File file = new File(context.getFilesDir(), filename);
        if (!file.exists()) {
            //File does not exist:
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(filename)))) {
            StringBuilder content = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                content.append(line);
                line = reader.readLine();
            }
            Log.d("Security", "Loaded from file " + filename + ": " + content);
            String decrypted = aes.decrypt(content.toString());
            Log.d("Security", "Decrypted from file " + filename + ": " + decrypted);
            return decrypted;
        }
        catch (Exception e) {
            //Could not read the file:
            if (e == null) {
                Log.e("Security", "Unknown error during decryption");
            }
            else {
                Log.e("Security", e.getMessage());
            }
            return null;
        }
    }

}
