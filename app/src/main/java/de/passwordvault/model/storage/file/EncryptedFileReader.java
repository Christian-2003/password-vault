package de.passwordvault.model.storage.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import de.passwordvault.App;
import de.passwordvault.model.storage.encryption.AES;
import de.passwordvault.model.storage.encryption.EncryptionException;


/**
 * Class models a FileReader which can read an encrypted file.
 *
 * @author  Christian-2003
 * @version 3.6.0
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
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                content.append(line);
                line = reader.readLine();
            }
            //I do not remember why I created this redundant variable, but I remember that the app
            //crashes when the generated result is returned directly...
            String decrypted = aes.decrypt(content.toString());
            return decrypted;
        }
        catch (Exception e) {
            return null;
        }
    }

}
