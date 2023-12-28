package de.passwordvault.model.storage.file;

import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import de.passwordvault.App;
import de.passwordvault.model.storage.encryption.AES;
import de.passwordvault.model.storage.encryption.EncryptionException;


/**
 * Class models a FileReader which can read an encrypted file.
 *
 * @author  Christian-2003
 * @version 2.2.2
 */
public class EncryptedFileReader {

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
        File file = new File(App.getContext().getFilesDir(), filename);
        if (!file.exists()) {
            //File does not exist:
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(App.getContext().openFileInput(filename)))) {
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
            //For some strange reason (despite what the IDE says), e apparently can be null. If not
            //tested, accessing e would crash the entire application.
            if (e == null) {
                Log.e("Security", "Unknown error during decryption");
            }
            else {
                Log.e("Security", e.getMessage() != null ? e.getMessage() : "Unknown error during decryption");
            }
            return null;
        }
    }

}
