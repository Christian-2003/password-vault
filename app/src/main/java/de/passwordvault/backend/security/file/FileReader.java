package de.passwordvault.backend.security.file;

import android.content.Context;
import androidx.security.crypto.EncryptedFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import de.passwordvault.backend.security.SecurityException;


/**
 * Class models a FileReader which can read an encrypted file.
 *
 * @author  Christian-2003
 * @version 2.0.0
 */
public class FileReader extends FileAccessor {


    /**
     * Constructor instantiates a new FileReader with the specified Context.
     *
     * @param context               Context for the FileReader.
     * @throws NullPointerException The specified Context is {@code null}.
     */
    public FileReader(Context context) throws NullPointerException {
        super(context);
    }


    /**
     * Method reads the content of the specified file.
     *
     * @param file                      Name of the file to be read.
     * @return                          Content of the read file.
     * @throws NullPointerException     The specified file name is {@code null}.
     * @throws SecurityException        The file could not be read due to encryption issues.
     * @throws IOException              The file could not be read.
     */
    public String read(String file) throws NullPointerException, SecurityException, IOException {
        StringBuilder fileContent = new StringBuilder();

        EncryptedFile encryptedFile = getEncryptedFile(file);

        //Read file content with BufferedReader:
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(encryptedFile.openFileInput()))) {
            String line = reader.readLine();
            while (line != null) {
                fileContent.append(line);
                fileContent.append(System.lineSeparator());
                line = reader.readLine();
            }
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SecurityException(e.getMessage());
        }

        return fileContent.toString();
    }

}
