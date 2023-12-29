package de.passwordvault.model.storage.backup;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.BufferedWriter;
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


/**
 * Class implements a functionality which creates an XML backup to the shared storage of the user's
 * Android device. The created backup will not be encrypted!
 *
 * @author  Christian-2003
 * @version 3.1.0
 */
public class CreateXmlBackup extends XmlBackupConfiguration {

    /**
     * Attribute stores the URI to a file to which the backup shall be created.
     */
    private final Uri uri;


    /**
     * Constructor instantiates a new {@link CreateXmlBackup} instance which can create a manual
     * disk backup of all data through the method {@link #createBackup()}.
     * The backup will be saved to the file of the passed URI. Please make sure that the application
     * has access (and permission) to write to that file, before calling.
     *
     * @param uri                   URI of the file to which the backup shall be created.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public CreateXmlBackup(Uri uri) throws NullPointerException {
        if (uri == null) {
            throw new NullPointerException("Null is invalid URI");
        }
        this.uri = uri;
    }


    /**
     * Method saves all entries to the external storage in JSON-format.
     * <b>IMPORTANT: In doing so, all encryption will be lost. The data can then be accessed by
     * everyone!</b>
     */
    public void createBackup() throws BackupException {
        ParcelFileDescriptor xml;
        try {
            xml = App.getContext().getContentResolver().openFileDescriptor(uri, "w");
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
        catch (IOException e) {
            throw new BackupException(e.getMessage());
        }
    }


    /**
     * Method generates the XML which shall be stored to the file.
     *
     * @param writer        Writer to the file to which the XML shall be written.
     * @throws IOException  The XML could not be written to the file.
     */
    private void createXML(BufferedWriter writer) throws IOException {
        InstanceToDTOConverter converter = new InstanceToDTOConverter();
        converter.generateDTOs();
        ArrayList<EntryDTO> entryDTOs = converter.getEntryDTOs();
        ArrayList<DetailDTO> detailDTOs = converter.getDetailDTOs();

        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

        insertTag(writer, TAG_PASSWORD_VAULT, 0, false, true);
        insertTag(writer, TAG_DATA, 4, false, true);
        insertTag(writer, TAG_ENTRIES, 8, false, true);

        for (int i = 0; i < entryDTOs.size(); i++) {
            writer.write(entryDTOs.get(i).getCsv());
            writer.write("\n");
        }

        insertTag(writer, TAG_ENTRIES, 8, true, true);
        insertTag(writer, TAG_DETAILS, 8, false, true);

        for (int i = 0; i < detailDTOs.size(); i++) {
            writer.write(detailDTOs.get(i).getCsv());
            writer.write("\n");
        }

        insertTag(writer, TAG_DETAILS, 8, true, true);
        insertTag(writer, TAG_DATA, 4, true, true);
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

}
