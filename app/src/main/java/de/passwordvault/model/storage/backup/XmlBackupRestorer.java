package de.passwordvault.model.storage.backup;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
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
import de.passwordvault.model.detail.DetailDTO;
import de.passwordvault.model.entry.Entry;
import de.passwordvault.model.entry.EntryDTO;
import de.passwordvault.model.entry.EntryHandle;
import de.passwordvault.model.storage.app.DTOToInstanceConverter;
import de.passwordvault.model.storage.encryption.EncryptionException;


/**
 * Class implements a functionality which restores an XML backup from the shared storage of the user's
 * Android device. The restored backup cannot be encrypted! Furthermore, this will replace all
 * handled entries with the restored entries!!!
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class XmlBackupRestorer extends XmlBackupConfiguration{

    /**
     * Constructor instantiates a new {@link XmlBackupCreator} instance which can create a manual
     * disk backup of all data through the method {@link #restoreBackup()}  RestoreXmlBackup()}.
     * The backup will be saved to the file of the passed URI. Please make sure that the application
     * has access (and permission) to write to that file, before calling.
     *
     * @param uri                   URI of the file to which the backup shall be created.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public XmlBackupRestorer(Uri uri) throws NullPointerException {
        super(uri, null);
    }

    /**
     * Constructor instantiates a new {@link XmlBackupCreator} instance which can create a manual
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
        NodeList encryptionTags = xml.getElementsByTagName(TAG_ENCRYPTION);
        if (encryptionTags != null && encryptionTags.getLength() != 0) {
            return true;
        }
        return false;
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
            NodeList encryptionNodes = xml.getElementsByTagName(TAG_ENCRYPTION).item(0).getChildNodes();
            boolean encryptionChecksumFound = false;
            for (int i = 0; i < encryptionNodes.getLength(); i++) {
                Node currentEncryptionNode = encryptionNodes.item(i);
                if (currentEncryptionNode == null) {
                    continue;
                }
                if (currentEncryptionNode.getNodeName().equals(TAG_ENCRYPTION_CHECKSUM)) {
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
        //Read the data:
        NodeList dataNodes = xml.getElementsByTagName(TAG_DATA).item(0).getChildNodes();
        ArrayList<EntryDTO> entries = null;
        ArrayList<DetailDTO> details = null;
        for (int i = 0; i < dataNodes.getLength(); i++) {
            Node currentDataNode = dataNodes.item(i);
            if (currentDataNode == null) {
                continue;
            }
            try {
                if (currentDataNode.getNodeName().equals(TAG_ENTRIES)) {
                    entries = getEntries(currentDataNode);
                } else if (currentDataNode.getNodeName().equals(TAG_DETAILS)) {
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
     * @return                      List of {@link DetailDTO}-instances.
     * @throws EncryptionException  The data could not be decrypted.
     */
    private ArrayList<DetailDTO> getDetails(Node detailsNode) throws EncryptionException {
        String content = detailsNode.getTextContent();
        if (content == null) {
            return null;
        }
        String[] details = content.split("\n");
        ArrayList<DetailDTO> detailDTOs = new ArrayList<>(details.length - 1);
        //Ignore last line in for-loop, as it only contains spacing for the closing tag!
        for (int i = 0; i < details.length - 1; i++) {
            String s = details[i];
            if (s == null || s.isEmpty()) {
                continue;
            }
            Log.d("RESTORE", "DetailContent='" + s + "'");
            detailDTOs.add(new DetailDTO(decrypt(s)));
        }
        return detailDTOs;
    }

    /**
     * Method returns the entries as data transfer objects that were retrieved from the specified
     * node.
     *
     * @param entriesNode           Node from which to retrieve the entries.
     * @return                      List of {@link EntryDTO}-instances.
     * @throws EncryptionException  The data could not be decrypted.
     */
    private ArrayList<EntryDTO> getEntries(Node entriesNode) throws EncryptionException {
        String content = entriesNode.getTextContent();
        if (content == null) {
            return null;
        }
        String[] entries = content.split("\n");
        ArrayList<EntryDTO> entryDTOs = new ArrayList<>(entries.length - 1);
        //Ignore last line in for-loop, as it only contains spacing for the closing tag!
        for (int i = 0; i < entries.length - 1; i++) {
            String s = entries[i];
            if (s == null || s.isEmpty()) {
                continue;
            }
            Log.d("RESTORE", "EntryContent='" + s + "'");
            entryDTOs.add(new EntryDTO(decrypt(s)));
        }
        return entryDTOs;
    }


    /**
     * Method replaces the handled entries within {@link EntryHandle} with the passe DTOs.
     *
     * @param entries   List of {@link EntryDTO}-instances which shall replace the handled entries.
     * @param details   List of {@link DetailDTO}-instances which shall replace the handled details.
     */
    private void restoreEntries(ArrayList<EntryDTO> entries, ArrayList<DetailDTO> details) {
        DTOToInstanceConverter converter = new DTOToInstanceConverter(entries, details);
        ArrayList<Entry> generated = converter.generateInstances();
        EntryHandle.getInstance().replaceAll(generated);
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
