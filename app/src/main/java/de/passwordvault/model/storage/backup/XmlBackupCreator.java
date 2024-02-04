package de.passwordvault.model.storage.backup;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import de.passwordvault.App;
import de.passwordvault.model.detail.DetailDTO;
import de.passwordvault.model.entry.EntryDTO;
import de.passwordvault.model.storage.app.InstanceToDTOConverter;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.model.tags.TagManager;


/**
 * Class implements a functionality which creates an XML backup to the shared storage of the user's
 * Android device.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class XmlBackupCreator extends XmlBackupConfiguration {

    /**
     * Attribute stores the name of the file into which the backup shall be saved.
     */
    private final String filename;


    /**
     * Constructor instantiates a new {@link XmlBackupCreator}-instance. Please make sure that
     * the application has access (and permission) to the directory specified in the provided URI
     * before calling. If the provided {@code encryptionKeySeed} is not {@code null}, the backup is
     * considered to be encrypted. Otherwise, it is not encrypted.
     *
     * @param uri                   URI of the file to which the backup shall be created.
     * @param filename              Name of the file into which the backup is stored.
     * @param encryptionSeed        Seed for the key with which to encrypt the backup. Pass {@code null}
     *                              if the backup shall not be encrypted.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public XmlBackupCreator(Uri uri, String filename, String encryptionSeed) throws NullPointerException {
        super(uri, encryptionSeed);
        if (filename == null) {
            throw new NullPointerException("Null is invalid filename");
        }
        this.filename = filename;
    }


    /**
     * Method saves all entries to the external storage in JSON-format.
     * <b>IMPORTANT: In doing so, all encryption will be lost. The data can then be accessed by
     * everyone!</b>
     *
     * @throws BackupException  The backup could not be created.
     */
    public void createBackup() throws BackupException {
        DocumentFile directory = DocumentFile.fromTreeUri(App.getContext(), uri);
        DocumentFile file = directory.createFile("text/plain", filename);
        Uri fileUri = file.getUri();

        ParcelFileDescriptor xml;
        try {
            xml = App.getContext().getContentResolver().openFileDescriptor(fileUri, "w");
        }
        catch (FileNotFoundException e) {
            throw new BackupException(e.getMessage());
        }
        FileDescriptor fs = xml.getFileDescriptor();
        if (fs == null) {
            throw new BackupException("Null is invalid file descriptor");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fs)));
            createXML(writer);
            writer.close();
            xml.close();
        }
        catch (IOException | EncryptionException e) {
            throw new BackupException(e.getMessage());
        }
    }


    /**
     * Method generates the XML which shall be stored to the file.
     *
     * @param writer                Writer to the file to which the XML shall be written.
     * @throws IOException          The XML could not be written to the file.
     * @throws EncryptionException  The backup could not be encrypted.
     */
    private void createXML(BufferedWriter writer) throws IOException, EncryptionException {
        InstanceToDTOConverter converter = new InstanceToDTOConverter();
        converter.generateDTOs();
        ArrayList<EntryDTO> entryDTOs = converter.getEntryDTOs();
        ArrayList<DetailDTO> detailDTOs = converter.getDetailDTOs();

        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

        insertTag(writer, TAG_PASSWORD_VAULT, 0, false, true);

        //Add version number:
        insertTag(writer, TAG_VERSION, 4, false, false);
        writer.write(Versions.VERSION_LATEST);
        insertTag(writer, TAG_VERSION, 4, true, true);

        //Add encryption-related data:
        if (encryptionAlgorithm != null) {
            insertTag(writer, TAG_ENCRYPTION, 4, false, true);
            //Do not put newLine with next tag to prevent 'Illegal base64 character'-exception when decrypting the checksum!
            insertTag(writer, TAG_ENCRYPTION_CHECKSUM, 8, false, false);
            writer.write(encrypt(encryptionKeySeed));
            //Use indentation=0 for next tag to put tag right behind checksum!
            insertTag(writer, TAG_ENCRYPTION_CHECKSUM, 0, true, true);
            insertTag(writer, TAG_ENCRYPTION, 4, true, true);
        }

        insertTag(writer, TAG_DATA, 4, false, true);
        insertTag(writer, TAG_ENTRIES, 8, false, true);

        //Add all (optionally encrypted) entries:
        for (int i = 0; i < entryDTOs.size(); i++) {
            writer.write(encrypt(entryDTOs.get(i).getCsv()));
            writer.write("\n");
        }

        insertTag(writer, TAG_ENTRIES, 8, true, true);
        insertTag(writer, TAG_DETAILS, 8, false, true);

        //Add all (optionally encrypted) details:
        for (int i = 0; i < detailDTOs.size(); i++) {
            writer.write(encrypt(detailDTOs.get(i).getCsv()));
            writer.write("\n");
        }

        insertTag(writer, TAG_DETAILS, 8, true, true);
        insertTag(writer, TAG_DATA, 4, true, true);
        insertTag(writer, TAG_TAGS, 4, false, true);

        //Add all tags:
        writer.write(TagManager.getInstance().toCsv());

        insertTag(writer, TAG_TAGS, 4, true, true);
        insertTag(writer, TAG_PASSWORD_VAULT, 0, true, true);
    }


    /**
     * Method adds the specified Tag to the document.
     *
     * @param writer        Writer for the document into which the tag shall be inserted.
     * @param tag           Name of the tag to be inserted.
     * @param indentation   Number of spaces with which the tag shall be indented.
     * @param endTag        Whether the tag to be added resembles an end tag.
     * @param newLine       Whether a new line shall be added after the inserted tag.
     * @throws IOException  The tag could not be added to the file.
     */
    private void insertTag(BufferedWriter writer, String tag, int indentation, boolean endTag, boolean newLine) throws IOException {
        //Add indentation:
        for (int i = 0; i < indentation; i++) {
            writer.write(" ");
        }
        //Add tag:
        writer.write("<");
        if (endTag) {
            writer.write("/");
        }
        writer.write(tag);
        writer.write(">");
        //Add new line:
        if (newLine) {
            writer.write("\n");
        }
    }


    /**
     * Method encrypts the specified string using {@link #encryptionAlgorithm}. If the backup shall
     * not be encrypted (i.e. {@code encryptionAlgorithm == null}), the passed argument is returned.
     *
     * @param data                  Data to be encrypted.
     * @return                      Encrypted data (Or passed argument if backup shall not be
     *                              encrypted).
     * @throws EncryptionException  The data could not be encrypted.
     */
    private String encrypt(String data) throws EncryptionException {
        if (encryptionAlgorithm != null) {
            return encryptionAlgorithm.encrypt(data);
        }
        return data;
    }

}
