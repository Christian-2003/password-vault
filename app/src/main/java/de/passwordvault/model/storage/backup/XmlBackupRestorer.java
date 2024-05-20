package de.passwordvault.model.storage.backup;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import de.passwordvault.App;
import de.passwordvault.model.detail.DetailBackupDTO;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.csv.CsvConfiguration;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.model.tags.TagManager;


/**
 * Class implements a functionality which restores an XML backup from the shared storage of the user's
 * Android device. The restored backup cannot be encrypted! Furthermore, this will replace all
 * handled entries with the restored entries!!!
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class XmlBackupRestorer extends XmlBackupConfiguration {

    /**
     * Constructor instantiates a new {@link XmlBackupRestorer} instance which can restore
     * disk backup of all data through the method {@link #restoreBackup()}.
     * The backup will be saved to the file of the passed URI. Please make sure that the application
     * has access (and permission) to write to that file, before calling.
     *
     * @param uri                   URI of the file to which the backup shall be created.
     * @param encryptionSeed        Seed for the key with which to encrypt the backup. Pass {@code null}
     *                              if the backup shall not be encrypted.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public XmlBackupRestorer(Uri uri, String encryptionSeed) throws NullPointerException {
        super(uri, encryptionSeed);
    }


    /**
     * Method tests whether the backup of the specified URI is encrypted.
     *
     * @return  Whether the backup is encrypted.
     */
    public static boolean isBackupEncrypted(Uri uri) {
        Document xml;
        try {
            xml = getXmlDocument(uri);
        }
        catch (BackupException | XmlException e) {
            return false;
        }
        NodeList encryptionTags = xml.getElementsByTagName(XmlConfiguration.TAG_ENCRYPTION.getValue());
        return encryptionTags != null && encryptionTags.getLength() != 0;
    }


    /**
     * Method restores a created XML backup which can be found at the provided {@link #uri}. If the
     * backup cannot be restored, a {@link BackupException} will be thrown.
     *
     * @throws BackupException      The provided backup cannot be restored.
     * @throws XmlException         The encountered XML is incorrect.
     * @throws EncryptionException  The backup could not be decrypted.
     */
    public void restoreBackup() throws BackupException, XmlException, EncryptionException {
        Document xml = getXmlDocument(uri);

        //Get encryption-related data:
        if (isBackupEncrypted(uri)) {
            NodeList encryptionNodes = xml.getElementsByTagName(XmlConfiguration.TAG_ENCRYPTION.getValue()).item(0).getChildNodes();
            boolean encryptionChecksumFound = false;
            for (int i = 0; i < encryptionNodes.getLength(); i++) {
                Node currentEncryptionNode = encryptionNodes.item(i);
                if (currentEncryptionNode == null) {
                    continue;
                }
                if (currentEncryptionNode.getNodeName().equals(XmlConfiguration.TAG_ENCRYPTION_CHECKSUM.getValue())) {
                    String decryptedSeed = decrypt(currentEncryptionNode.getTextContent());
                    if (!decryptedSeed.equals(encryptionKeySeed)) {
                        //Incorrect seed provided:
                        throw new EncryptionException("Data could not be decrypted.");
                    }
                    encryptionChecksumFound = true;
                    break;
                }
            }
            if (!encryptionChecksumFound) {
                //No checksum provided:
                throw new BackupException("No encryption checksum provided.");
            }
        }

        //Restore tags:
        Node tagNode = xml.getElementsByTagName(XmlConfiguration.TAG_TAGS.getValue()).item(0);
        if (tagNode != null) {
            String content = tagNode.getTextContent();
            if (content != null && !content.isEmpty()) {
                TagManager.getInstance().fromCsv(content);
                TagManager.getInstance().save(true);
            }
        }

        //Read the data:
        NodeList dataNodes = xml.getElementsByTagName(XmlConfiguration.TAG_DATA.getValue()).item(0).getChildNodes();
        ArrayList<EntryAbbreviated> entries = null;
        ArrayList<DetailBackupDTO> details = null;
        for (int i = 0; i < dataNodes.getLength(); i++) {
            Node currentDataNode = dataNodes.item(i);
            if (currentDataNode == null) {
                continue;
            }
            try {
                if (currentDataNode.getNodeName().equals(XmlConfiguration.TAG_ENTRIES.getValue())) {
                    entries = getEntries(currentDataNode);
                }
                else if (currentDataNode.getNodeName().equals(XmlConfiguration.TAG_DETAILS.getValue())) {
                    details = getDetails(currentDataNode);
                }
            }
            catch (EncryptionException e) {
                e.printStackTrace();
                throw e;
            }
        }

        //Restore entries:
        if (entries != null && details != null) {
            restoreEntries(entries, details);
        }
    }


    /**
     * Method returns the XML document at the specified URI.
     *
     * @param uri               URI to the file of the backup.
     * @return                  XML document at the specified URI.
     * @throws BackupException  The XML document could not be retrieved.
     * @throws XmlException     Some Error regarding the retrieved XML occurred.
     */
    private static Document getXmlDocument(Uri uri) throws BackupException, XmlException {
        ParcelFileDescriptor parcelFileDescriptor;
        try {
            parcelFileDescriptor = App.getContext().getContentResolver().openFileDescriptor(uri, "r");
        }
        catch (FileNotFoundException e) {
            throw new BackupException(e.getMessage());
        }
        try {
            if (parcelFileDescriptor == null) {
                throw new BackupException("Null is invalid ParcelFileDescriptor");
            }
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            if (fileDescriptor == null) {
                throw new BackupException("Null is invalid FileDescriptor");
            }
            FileInputStream inputStream = new FileInputStream(fileDescriptor);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            dbf.setValidating(false);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            parcelFileDescriptor.close();
            return document;
        }
        catch (SAXException | ParserConfigurationException e) {
            throw new XmlException(e.getMessage());
        }
        catch (IOException e) {
            throw new BackupException(e.getMessage());
        }
    }


    /**
     * Method returns the details as data transfer objects that were retrieved from the specified
     * node.
     *
     * @param detailsNode           Node from which to retrieve the details.
     * @return                      List of {@link DetailBackupDTO}-instances.
     * @throws EncryptionException  The data could not be decrypted.
     */
    private ArrayList<DetailBackupDTO> getDetails(Node detailsNode) throws EncryptionException {
        String content = detailsNode.getTextContent();
        if (content == null) {
            return null;
        }
        String[] lines = content.split("" + CsvConfiguration.ROW_DIVIDER);
        ArrayList<DetailBackupDTO> details = new ArrayList<>(lines.length - 1);
        //Ignore last line in for-loop, as it only contains spacing for the closing tag!
        for (String s : lines) {
            DetailBackupDTO detail;
            try {
                detail = new DetailBackupDTO(decrypt(s));
            }
            catch (StorageException | NullPointerException | EncryptionException e) {
                //Data corrupt:
                continue;
            }
            details.add(detail);
        }
        return details;
    }

    /**
     * Method returns the entries as data transfer objects that were retrieved from the specified
     * node.
     *
     * @param entriesNode           Node from which to retrieve the entries.
     * @return                      List of {@link EntryAbbreviated}-instances.
     * @throws EncryptionException  The data could not be decrypted.
     */
    private ArrayList<EntryAbbreviated> getEntries(Node entriesNode) throws EncryptionException {
        String content = entriesNode.getTextContent();
        if (content == null) {
            return null;
        }
        String[] lines = content.split("" + CsvConfiguration.ROW_DIVIDER);
        ArrayList<EntryAbbreviated> entries = new ArrayList<>(lines.length - 1);
        //Ignore last line in for-loop, as it only contains spacing for the closing tag!
        for (String s : lines) {
            EntryAbbreviated abbreviated = new EntryAbbreviated();
            if (s.isEmpty() || s == lines[lines.length - 1]) {
                //Comparison by reference in condition above is wanted!
                //Ignore last line since last line only contains the spaces to the "</entries>"-tag:
                continue;
            }
            try {
                abbreviated.fromStorable(decrypt(s));
            }
            catch (StorageException | EncryptionException e) {
                //Data corrupt:
                continue;
            }
            entries.add(abbreviated);
        }
        return entries;
    }


    /**
     * Method replaces the handled entries within {@link EntryManager} with the passe DTOs.
     *
     * @param entries   List of {@link EntryAbbreviated}-instances which shall replace the handled
     *                  entries.
     * @param details   List of {@link DetailBackupDTO}-instances which shall replace the handled
     *                  details.
     */
    private void restoreEntries(ArrayList<EntryAbbreviated> entries, ArrayList<DetailBackupDTO> details) {
        for (EntryAbbreviated abbreviated : entries) {
            EntryExtended extended = new EntryExtended(abbreviated);
            for (DetailBackupDTO detail : details) {
                if (extended.getUuid().equals(detail.getEntryUuid())) {
                    extended.add(detail.toDetail());
                }
            }
            EntryManager.getInstance().add(extended);
        }
        try {
            EntryManager.getInstance().save(true);
        }
        catch (StorageException e) {
            //Ignore...
        }
    }


    /**
     * Method decrypts the specified string using {@link #encryptionAlgorithm}. If the backup is
     * not encrypted (i.e. {@code encryptionAlgorithm == null}), the passed argument is returned.
     *
     * @param data                  Data to be decrypted.
     * @return                      Decrypted data (Or passed argument if backup is not encrypted).
     * @throws EncryptionException  The data could not be decrypted.
     */
    private String decrypt(String data) throws EncryptionException {
        if (encryptionAlgorithm != null) {
            return encryptionAlgorithm.decrypt(data);
        }
        return data;
    }

}
