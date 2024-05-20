package de.passwordvault.model.storage.backup;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import androidx.documentfile.provider.DocumentFile;
import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import de.passwordvault.App;
import de.passwordvault.BuildConfig;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.detail.DetailBackupDTO;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.storage.csv.CsvConfiguration;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.model.tags.TagManager;


/**
 * Class implements a functionality which creates an XML backup to the shared storage of the user's
 * Android device.
 *
 * @author      Christian-2003
 * @version     3.3.0
 * @deprecated  Use {@link XmlBackupCreator2} instead.
 */
public class XmlBackupCreator extends XmlBackupConfiguration {

    /**
     * Attribute stores the name of the file into which the backup shall be saved.
     */
    private final String filename;

    /**
     * Attribute stores whether the backup is created automatically (= {@code true}) or manually by
     * the use (= {@code false}).
     */
    private final boolean autoCreated;


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
        this.autoCreated = false;
    }

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
     * @param autoCreated           Indicates whether the backup is created automatically.s
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public XmlBackupCreator(Uri uri, String filename, String encryptionSeed, boolean autoCreated) throws NullPointerException {
        super(uri, encryptionSeed);
        if (filename == null) {
            throw new NullPointerException("Null is invalid filename");
        }
        this.filename = filename;
        this.autoCreated = autoCreated;
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
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        insertTopComment(writer);

        insertTag(writer, XmlConfiguration.TAG_PASSWORD_VAULT.getValue(), 0, false, true);
        insertTag(writer, XmlConfiguration.TAG_METADATA.getValue(), 4, false, true);

        //Add version number:
        insertTag(writer, XmlConfiguration.TAG_VERSION.getValue(), 8, false, false);
        writer.write(XmlConfiguration.VERSION_LATEST.getValue());
        insertTag(writer, XmlConfiguration.TAG_VERSION.getValue(), 0, true, true);

        //Add app version number:
        insertTag(writer, XmlConfiguration.TAG_APP_VERSION.getValue(), 8, false, false);
        writer.write(BuildConfig.VERSION_NAME);
        insertTag(writer, XmlConfiguration.TAG_APP_VERSION.getValue(), 0, true, true);

        //Add current date:
        insertTag(writer, XmlConfiguration.TAG_BACKUP_CREATED.getValue(), 8, false, false);
        writer.write("" + Calendar.getInstance().getTimeInMillis());
        insertTag(writer, XmlConfiguration.TAG_BACKUP_CREATED.getValue(), 0, true, true);

        //Add whether backup is automatically created:
        insertTag(writer, XmlConfiguration.TAG_AUTO_CREATED.getValue(), 8, false, false);
        writer.write("" + autoCreated);
        insertTag(writer, XmlConfiguration.TAG_AUTO_CREATED.getValue(), 0, true, true);

        insertTag(writer, XmlConfiguration.TAG_METADATA.getValue(), 4, true, true);

        //Add encryption-related data:
        if (encryptionAlgorithm != null) {
            insertTag(writer, XmlConfiguration.TAG_ENCRYPTION.getValue(), 4, false, true);
            //Do not put newLine with next tag to prevent 'Illegal base64 character'-exception when decrypting the checksum!
            insertTag(writer, XmlConfiguration.TAG_ENCRYPTION_CHECKSUM.getValue(), 8, false, false);
            writer.write(encrypt(encryptionKeySeed));
            //Use indentation=0 for next tag to put tag right behind checksum!
            insertTag(writer, XmlConfiguration.TAG_ENCRYPTION_CHECKSUM.getValue(), 0, true, true);
            insertTag(writer, XmlConfiguration.TAG_ENCRYPTION.getValue(), 4, true, true);
        }

        insertTag(writer, XmlConfiguration.TAG_DATA.getValue(), 4, false, true);
        insertTag(writer, XmlConfiguration.TAG_ENTRIES_HEADER.getValue(), 8, false, false);
        writer.write(EntryAbbreviated.getStorableAttributes());
        insertTag(writer, XmlConfiguration.TAG_ENTRIES_HEADER.getValue(), 0, true, true);
        insertTag(writer, XmlConfiguration.TAG_ENTRIES.getValue(), 8, false, true);

        //Add all (optionally encrypted) entries:
        for (EntryAbbreviated abbreviated : EntryManager.getInstance().getData()) {
            writer.write(encrypt(abbreviated.toStorable()));
            writer.write("" + CsvConfiguration.ROW_DIVIDER);
        }

        insertTag(writer, XmlConfiguration.TAG_ENTRIES.getValue(), 8, true, true);
        insertTag(writer, XmlConfiguration.TAG_DETAILS_HEADER.getValue(), 8, false, false);
        writer.write(DetailBackupDTO.getStorableAttributes());
        insertTag(writer, XmlConfiguration.TAG_DETAILS_HEADER.getValue(), 0, true, true);
        insertTag(writer, XmlConfiguration.TAG_DETAILS.getValue(), 8, false, true);

        //Add all (optionally encrypted) details:
        for (EntryAbbreviated abbreviated : EntryManager.getInstance().getData()) {
            EntryExtended extended = EntryManager.getInstance().get(abbreviated.getUuid(), false);
            if (extended == null) {
                continue;
            }
            for (Detail detail : extended.getDetails()) {
                DetailBackupDTO detailDTO = new DetailBackupDTO(detail, extended.getUuid());
                writer.write(encrypt(detailDTO.toStorable()));
                writer.write("" + CsvConfiguration.ROW_DIVIDER);
            }
        }

        insertTag(writer, XmlConfiguration.TAG_DETAILS.getValue(), 8, true, true);
        insertTag(writer, XmlConfiguration.TAG_DATA.getValue(), 4, true, true);
        insertTag(writer, XmlConfiguration.TAG_TAGS.getValue(), 4, false, true);

        //Add all tags:
        writer.write(TagManager.getInstance().toCsv());

        insertTag(writer, XmlConfiguration.TAG_TAGS.getValue(), 4, true, true);
        insertTag(writer, XmlConfiguration.TAG_PASSWORD_VAULT.getValue(), 0, true, true);
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
     * Method adds an informative comment to the XML document.
     *
     * @param writer        Writer for the document into which the comment shall be written.
     * @throws IOException  The comment could not be added to the file.
     */
    private void insertTopComment(BufferedWriter writer) throws IOException {
        writer.write("<!--\n");
        writer.write("This is a backup file generated by PasswordVault (https://github.com/Christian-2003/password-vault).\n");
        writer.write("Do not modify the contents of this file under any circumstances, as this can result in a complete loss of your data when restoring this backup.\n");
        writer.write("-->\n");
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
