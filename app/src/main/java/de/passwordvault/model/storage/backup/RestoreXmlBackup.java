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
import de.passwordvault.model.detail.DetailDTO;
import de.passwordvault.model.entry.Entry;
import de.passwordvault.model.entry.EntryDTO;
import de.passwordvault.model.entry.EntryHandle;
import de.passwordvault.model.storage.app.DTOToInstanceConverter;


/**
 * Class implements a functionality which restores an XML backup from the shared storage of the user's
 * Android device. The restored backup cannot be encrypted! Furthermore, this will replace all
 * handled entries with the restored entries!!!
 *
 * @author  Christian-2003
 * @version 3.1.0
 */
public class RestoreXmlBackup extends XmlBackupConfiguration{

    /**
     * Attribute stores the URI to a file from which the backup shall be restored.
     */
    private final Uri uri;


    /**
     * Constructor instantiates a new {@link CreateXmlBackup} instance which can create a manual
     * disk backup of all data through the method {@link #restoreBackup()}  RestoreXmlBackup()}.
     * The backup will be saved to the file of the passed URI. Please make sure that the application
     * has access (and permission) to write to that file, before calling.
     *
     * @param uri                   URI of the file to which the backup shall be created.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public RestoreXmlBackup(Uri uri) throws NullPointerException {
        if (uri == null) {
            throw new NullPointerException("Null is invalid URI");
        }
        this.uri = uri;
    }


    /**
     * Method restores a created XML backup which can be found at the provided {@link #uri}. If the
     * backup cannot be restored, a {@link BackupException} will be thrown.
     *
     * @throws BackupException  The provided backup cannot be restored.
     */
    public void restoreBackup() throws BackupException, XmlException {
        Document xml = getXmlDocument();
        NodeList dataNodes = xml.getElementsByTagName(TAG_DATA).item(0).getChildNodes();

        //Read the data:
        ArrayList<EntryDTO> entries = null;
        ArrayList<DetailDTO> details = null;
        for (int i = 0; i < dataNodes.getLength(); i++) {
            Node currentDataNode = dataNodes.item(i);
            if (currentDataNode == null) {
                continue;
            }
            if (currentDataNode.getNodeName().equals(TAG_ENTRIES)) {
                entries = getEntries(currentDataNode);
            }
            else if (currentDataNode.getNodeName().equals(TAG_DETAILS)) {
                details = getDetails(currentDataNode);
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
     * @return                  XML document at the specified URI.
     * @throws BackupException  The XML document could not be retrieved.
     * @throws XmlException     Some Error regarding the retrieved XML occurred.
     */
    private Document getXmlDocument() throws BackupException, XmlException {
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
     * @param detailsNode   Node from which to retrieve the details.
     * @return              List of {@link DetailDTO}-instances.
     */
    private ArrayList<DetailDTO> getDetails(Node detailsNode) {
        String content = detailsNode.getTextContent();
        if (content == null) {
            return null;
        }
        String[] details = content.split("\n");
        ArrayList<DetailDTO> detailDTOs = new ArrayList<>(details.length);
        for (String s : details) {
            if (s == null || s.isEmpty()) {
                continue;
            }
            detailDTOs.add(new DetailDTO(s));
        }
        return detailDTOs;
    }

    /**
     * Method returns the entries as data transfer objects that were retrieved from the specified
     * node.
     *
     * @param entriesNode   Node from which to retrieve the entries.
     * @return              List of {@link EntryDTO}-instances.
     */
    private ArrayList<EntryDTO> getEntries(Node entriesNode) {
        String content = entriesNode.getTextContent();
        if (content == null) {
            return null;
        }
        String[] entries = content.split("\n");
        ArrayList<EntryDTO> entryDTOs = new ArrayList<>(entries.length);
        for (String s : entries) {
            if (s == null || s.isEmpty()) {
                continue;
            }
            entryDTOs.add(new EntryDTO(s));
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

}
