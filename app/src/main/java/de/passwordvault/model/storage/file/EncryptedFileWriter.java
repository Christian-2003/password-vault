package de.passwordvault.model.storage.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import de.passwordvault.App;
import de.passwordvault.model.storage.encryption.AES;
import de.passwordvault.model.storage.encryption.EncryptionException;


/**
 * Class models a FileWriter which can write to an encrypted file.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class EncryptedFileWriter {

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

        content = aes.encrypt(content);

        //Write file:
        File file = new File(App.getContext().getFilesDir(), filename);
        if (!file.exists()) {
            try {
                File parentDir = file.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
                file.createNewFile();
            }
            catch (IOException | SecurityException e) {
                //Could not create new file:
                return false;
            }
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes());
        }
        catch (IOException e) {
            //Could not write to file:
            return false;
        }
        return true;
    }

}
