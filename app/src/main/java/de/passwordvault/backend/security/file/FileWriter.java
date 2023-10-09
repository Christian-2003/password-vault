package de.passwordvault.backend.security.file;

import android.content.Context;
import androidx.security.crypto.EncryptedFile;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import de.passwordvault.backend.security.SecurityException;


/**
 * Class models a FileWriter which can write to an encrypted file.
 *
 * @author  Christian-2003
 * @version 2.0.0
 */
public class FileWriter extends FileAccessor {

    /**
     * Constructor instantiates a new FileWriter with the specified Context.
     *
     * @param context               Context for the FileWriter.
     * @throws NullPointerException The specified Context is {@code null}.
     */
    public FileWriter(Context context) throws NullPointerException {
        super(context);
    }


    /**
     * Method writes the content to the specified file.
     *
     * @param file                      Name of the file to be written to.
     * @param content                   Content to write to the file.
     * @throws NullPointerException     The specified file name is {@code null}.
     * @throws SecurityException        The file could not be written to due to encryption issues.
     * @throws IOException              The file could not be written to.
     */
    public void write(String file, String content) throws NullPointerException, SecurityException, IOException {
        EncryptedFile encryptedFile = getEncryptedFile(file);

        try (OutputStream outputStream = encryptedFile.openFileOutput()) {
            outputStream.write(content.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SecurityException(e.getMessage());
        }
    }

}
