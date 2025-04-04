package de.passwordvault.model.storage.backup;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import de.passwordvault.App;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.detail.DetailBackupDTO;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.csv.CsvConfiguration;
import de.passwordvault.model.storage.encryption.AES;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.model.storage.settings.Config;
import de.passwordvault.model.storage.settings.NoBackup;
import de.passwordvault.model.storage.settings.items.GenericItem;
import de.passwordvault.model.tags.TagManager;


/**
 * Class implements a backup that can be restored.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class Backup {

    /**
     * Class contains the configuration for restoring a backup.
     */
    public static class RestoreConfig {

        /**
         * Field can be used to delete all existing data when restoring.
         * Use this as option for {@link #currentDataOption}.
         */
        public static final int DELETE_ALL_DATA = 0;

        /**
         * Field can be used to overwrite existing data when restoring.
         * Use this as option for {@link #currentDataOption}.
         */
        public static final int OVERWRITE_EXISTING_DATA = 1;

        /**
         * Field can be used to skip (not restore) existing data when restoring.
         * Use this as option for {@link #currentDataOption}.
         */
        public static final int SKIP_EXISTING_DATA = 2;


        /**
         * Attribute stores how to handle existing data while restoring the backup.
         */
        private int currentDataOption;

        /**
         * Attribute stores the seed to generate the key to decrypt the data.
         */
        private String encryptionKeySeed;

        /**
         * Attribute indicates whether to restore quality gates (if available).
         */
        private boolean restoreQualityGates;

        /**
         * Attribute indicates whether to restore settings (if available).
         */
        private boolean restoreSettings;


        /**
         * Constructor instantiates a new restore config.
         */
        public RestoreConfig() {
            currentDataOption = DELETE_ALL_DATA;
            encryptionKeySeed = null;
            restoreQualityGates = false;
            restoreSettings = false;
        }


        /**
         * Method returns the option indicating what to do with existing data.
         *
         * @return  What to do with existing data.
         */
        public int getCurrentDataOption() {
            return currentDataOption;
        }

        /**
         * Method changes the option indicating what to di with existing data.
         *
         * @param currentDataOption What to do with existing data.
         */
        public void setCurrentDataOption(int currentDataOption) {
            this.currentDataOption = currentDataOption;
        }

        /**
         * Method returns the seed used to generate the key to decrypt the data.
         *
         * @return  Seed used to generate the key.
         */
        public String getEncryptionKeySeed() {
            return encryptionKeySeed;
        }

        /**
         * Method changes the seed used to generate the key to decrypt the data.
         *
         * @param encryptionKeySeed Seed used to generate the key.
         */
        public void setEncryptionKeySeed(String encryptionKeySeed) {
            this.encryptionKeySeed = encryptionKeySeed;
        }

        /**
         * Method returns whether to restore quality gates (if available).
         *
         * @return  Whether to restore quality gates.
         */
        public boolean getRestoreQualityGates() {
            return restoreQualityGates;
        }

        /**
         * Method changes whether to restore quality gates (if available).
         *
         * @param restoreQualityGates   Whether to restore quality gates.
         */
        public void setRestoreQualityGates(boolean restoreQualityGates) {
            this.restoreQualityGates = restoreQualityGates;
        }

        /**
         * Method returns whether to restore settings (if available).
         *
         * @return  Whether to restore settings.
         */
        public boolean getRestoreSettings() {
            return restoreSettings;
        }

        /**
         * Method changes whether to restore settings (if available).
         *
         * @param restoreSettings   Whether to restore settings.
         */
        public void setRestoreSettings(boolean restoreSettings) {
            this.restoreSettings = restoreSettings;
        }

    }


    /**
     * Field stores the key with which to retrieve the backup version through
     * {@link #getMetadata(String)}.
     */
    public static final String BACKUP_VERSION = XmlConfiguration.TAG_VERSION.getValue();

    /**
     * Field stores the key with which to retrieve the app version through
     * {@link #getMetadata(String)}.
     */
    public static final String APP_VERSION = XmlConfiguration.TAG_APP_VERSION.getValue();

    /**
     * Field stores the key with which to retrieve the date on which the backup was created through
     * {@link #getMetadata(String)}.
     */
    public static final String CREATED = XmlConfiguration.TAG_BACKUP_CREATED.getValue();

    /**
     * Field stores the key with which to retrieve whether the backup was automatically created through
     * {@link #getMetadata(String)}.
     */
    public static final String AUTO_CREATED = XmlConfiguration.TAG_AUTO_CREATED.getValue();

    /**
     * Field stores the key with which to retrieve whether the backup contains settings through
     * {@link #getMetadata(String)}.
     */
    public static final String INCLUDE_SETTINGS = XmlConfiguration.TAG_SETTINGS.getValue();

    /**
     * Field stores the key with which to retrieve whether the backup contains quality gates through
     * {@link #getMetadata(String)}.
     */
    public static final String INCLUDE_QUALITY_GATES = XmlConfiguration.TAG_QUALITY_GATES.getValue();


    /**
     * Attribute stores the URI to the backup file.
     */
    private final Uri uri;

    /**
     * Attribute stores the filename of the backup file.
     */
    private final String filename;

    /**
     * Attribute stores the metadata of the backup.
     */
    private HashMap<String, String> metadata;

    /**
     * Attribute stores the encryption checksum. If this is {@code null}, the backup is not encrypted.
     */
    private String encryptionChecksum;

    /**
     * Attribute stores the AES cipher used to decrypt the data.
     */
    private AES cipher;

    /**
     * Attribute stores the config used to restore the backup.
     */
    private RestoreConfig config;

    /**
     * Attribute stores the XML document which contains the backup.
     */
    private final Document xmlDocument;


    /**
     * Constructor instantiates a new backup for the passed URI and automatically reads it's metadata.
     * Creating a new instance with this constructor takes some time, so do not execute this on the
     * main thread!
     *
     * @param uri                   URI of the backup file.
     * @throws NullPointerException The passed URI is {@code null}.
     * @throws BackupException      The XML document which contains the backup cannot be retrieved.
     * @throws XmlException         Some error regarding the XML structure occurred.
     */
    public Backup(Uri uri) throws NullPointerException, BackupException, XmlException {
        if (uri == null) {
            throw new NullPointerException();
        }
        this.uri = uri;
        String path = uri.getPath();
        filename = getFilename(path == null ? "" : path);
        metadata = null;
        encryptionChecksum = null;
        xmlDocument = getXmlDocument();
        readMetadata();
    }


    /**
     * Method returns the filename of the backup.
     *
     * @return  Filename of the backup.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Method returns whether the backup is encrypted. If the encryption data was not already read
     * from the XML document, it will automatically be retrieved (which might take some time).
     *
     * @return  Whether the backup is encrypted.
     */
    public boolean isEncrypted() {
        if (metadata == null) {
            readMetadata();
        }
        return encryptionChecksum != null;
    }

    /**
     * Method returns the metadata with the specified key. If the metadata was not already read, this
     * will automatically load metadata (which might take some time). If no metadata with the specified
     * key is available, {@code null} is returned.
     *
     * @param key                   Key of the metadata to return.
     * @return                      Metadata of the specified key.
     * @throws NullPointerException The passed key is {@code null}.
     */
    public String getMetadata(String key) throws NullPointerException {
        if (key == null) {
            throw new NullPointerException();
        }
        if (metadata == null) {
            readMetadata();
        }
        return metadata.get(key);
    }


    /**
     * Method checks whether the passed encryption seed is correct. If the metadata (and thus the
     * checksum) is not already available, it is read automatically (which might take some time).
     *
     * @param encryptionSeed        Seed to be tested.
     * @return                      Whether the passed encryption seed is correct.
     * @throws EncryptionException  The checksum could not be decrypted.
     */
    public boolean isEncryptionSeedValid(String encryptionSeed) throws EncryptionException {
        if (metadata == null) {
            readMetadata();
        }
        if (encryptionChecksum == null || encryptionChecksum.isEmpty()) {
            return true;
        }
        if (encryptionSeed == null) {
            return false;
        }
        cipher = new AES(encryptionSeed);
        String decryptedChecksum = decryptIfNecessary(encryptionChecksum);
        cipher = null;
        return encryptionSeed.equals(decryptedChecksum);
    }


    /**
     * Method restores the backup. The passed config contains information that dictate the behaviour
     * for restoring the backup. Passing {@code null} is possible.
     *
     * @param config                Config used to restore the backup.
     * @throws NullPointerException The backup is encrypted and the seed within the passed config
     *                              is {@code null}.
     * @throws BackupException      The backup cannot be restored.
     * @throws EncryptionException  The data could not be encrypted.
     */
    public void restoreBackup(RestoreConfig config) throws NullPointerException, BackupException, EncryptionException {
        this.config = config != null ? config : new RestoreConfig();

        String version = getMetadata(BACKUP_VERSION);
        if (version == null || version.equals(XmlConfiguration.VERSION_1.getValue())) {
            //Need to use old backup restorer:
            XmlBackupRestorer restorer = new XmlBackupRestorer(uri, this.config.getEncryptionKeySeed());
            try {
                restorer.restoreBackup();
            }
            catch (XmlException e) {
                throw new BackupException(e.getMessage());
            }
            catch (BackupException | EncryptionException e) {
                throw e;
            }
            return;
        }

        if (isEncrypted()) {
            if (this.config.getEncryptionKeySeed() == null) {
                throw new NullPointerException("Backup to restore is encrypted, but no seed is provided.");
            }
            cipher = new AES(this.config.getEncryptionKeySeed());
        }

        NodeList rootNodes = xmlDocument.getChildNodes();
        Node rootNode = null;
        for (int i = 0; i < rootNodes.getLength(); i++) {
            Node currentChild = rootNodes.item(i);
            if (currentChild.getNodeName().equals(XmlConfiguration.TAG_PASSWORD_VAULT.getValue())) {
                rootNode = currentChild;
                break;
            }
        }

        if (rootNode == null) {
            throw new BackupException("No root node found");
        }

        NodeList rootChildren = rootNode.getChildNodes();
        for (int i = 0; i < rootChildren.getLength(); i++) {
            Node currentChild = rootChildren.item(i);
            if (currentChild.getNodeName().equals(XmlConfiguration.TAG_DATA.getValue())) {
                //Data:
                ArrayList<EntryAbbreviated> entries = null;
                ArrayList<DetailBackupDTO> details = null;
                NodeList dataNodes = currentChild.getChildNodes();
                for (int j = 0; j < dataNodes.getLength(); j++) {
                    Node currentDataNode = dataNodes.item(j);
                    if (currentDataNode.getNodeName().equals(XmlConfiguration.TAG_ENTRIES.getValue())) {
                        entries = traverseEntriesNode(currentDataNode);
                    }
                    if (currentDataNode.getNodeName().equals(XmlConfiguration.TAG_DETAILS.getValue())) {
                        details = traverseDetailsNode(currentDataNode);
                    }
                    if (currentDataNode.getNodeName().equals(XmlConfiguration.TAG_TAGS.getValue())) {
                        restoreTags(currentDataNode);
                    }
                }
                if (entries == null || details == null) {
                    throw new NullPointerException("No data to restore");
                }
                restoreEntries(entries, details);
            }
            if (currentChild.getNodeName().equals(XmlConfiguration.TAG_SETTINGS.getValue())) {
                //Settings:
                if (this.config.getRestoreSettings()) {
                    HashMap<String, String> settings = traverseSettingsNode(currentChild);
                    restoreSettings(settings);
                }
                if (this.config.getRestoreQualityGates() && Boolean.parseBoolean(getMetadata(INCLUDE_QUALITY_GATES))) {
                    ArrayList<QualityGate> qualityGates = traverseQualityGatesNode(currentChild);
                    restoreQualityGates(qualityGates);
                }
            }
        }

    }


    /**
     * Method extracts the filename from the passed URI.
     *
     * @param uri   String-representation of the URI whose filename to extract.
     * @return      Filename extracted from the URI.
     */
    private String getFilename(String uri) {
        int index = uri.lastIndexOf('/');
        if (index != -1) {
            return uri.substring(index + 1);
        }
        index = uri.lastIndexOf('\\');
        if (index != -1) {
            return uri.substring(index + 1);
        }
        return uri;
    }


    /**
     * Method reads all available metadata from the XML document. The retrieved metadata is stored
     * within {@link #metadata}. The key for each entry is the corresponding tag name.
     * If encryption data is available, the checksum is stored in {@link #encryptionChecksum}.
     * If metadata was already read, calling this method does nothing.
     */
    private void readMetadata() {
        if (metadata != null) {
            return;
        }
        metadata = new HashMap<>();

        NodeList rootNodes = xmlDocument.getElementsByTagName(XmlConfiguration.TAG_PASSWORD_VAULT.getValue());
        Element root = null;
        if (rootNodes.getLength() >= 1) {
            root = (Element)rootNodes.item(0);
        }
        if (root == null) {
            //For some reason, no root node is available:
            return;
        }

        //Traverse direct child nodes of root:
        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node currentChild = childNodes.item(i);
            if (currentChild.getNodeName().equals(XmlConfiguration.TAG_METADATA.getValue())) {
                traverseMetadataNode(currentChild);
            }
            else if (currentChild.getNodeName().equals(XmlConfiguration.TAG_DATA.getValue())) {
                traverseDataNodeAttributes(currentChild);
            }
            else if (currentChild.getNodeName().equals(XmlConfiguration.TAG_ENCRYPTION.getValue())) {
                traverseEncryptionNode(currentChild); //For version 1 backups.
            }
        }
    }


    /**
     * Method reads the metadata from the passed node.
     *
     * @param node  Metadata node whose children resemble the metadata.
     */
    private void traverseMetadataNode(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node currentChild = childNodes.item(i);
            if (currentChild.getChildNodes().getLength() != 0) {
                metadata.put(currentChild.getNodeName(), currentChild.getTextContent());
            }
        }
    }

    /**
     * Method traverses the attributes of the data node.
     *
     * @param node  Data node of which to traverse the attributes.
     */
    private void traverseDataNodeAttributes(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node currentAttribute = attributes.item(i);
            if (currentAttribute.getNodeName().equals(XmlConfiguration.ATTRIBUTE_CHECKSUM.getValue())) {
                encryptionChecksum = currentAttribute.getNodeValue();
            }
        }
    }


    /**
     * Method reads the encryption data from the passed node.
     * This is only needed to get encryption data from version 1 backups.
     *
     * @param node  Encryption node whose children resemble the encryption data.
     */
    private void traverseEncryptionNode(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node currentChild = childNodes.item(i);
            if (currentChild.getNodeName().equals(XmlConfiguration.TAG_ENCRYPTION_CHECKSUM.getValue())) {
                encryptionChecksum = currentChild.getTextContent();
            }
        }
    }


    /**
     * Method restores the tags from the passed node.
     *
     * @param node  Tags node whose content resembles the tags data.
     */
    private void restoreTags(Node node) throws EncryptionException {
        String value = node.getTextContent();
        if (value != null && !value.isEmpty()) {
            TagManager.getInstance().fromCsv(decryptIfNecessary(value));
            TagManager.getInstance().save(true);
        }
    }


    /**
     * Method reads the entries from the entries node and returns them within an array list.
     *
     * @param node  Node which contains all entries.
     * @return      List of entries retrieved from the passed node.
     */
    private ArrayList<EntryAbbreviated> traverseEntriesNode(Node node) {
        String content = node.getTextContent();
        ArrayList<EntryAbbreviated> entries = new ArrayList<>();
        if (content == null) {
            return entries;
        }
        String[] rows = content.split("" + CsvConfiguration.ROW_DIVIDER);
        for (String s : rows) {
            if (s.isEmpty()) {
                continue;
            }
            EntryAbbreviated abbreviated = new EntryAbbreviated();
            try {
                abbreviated.fromStorable(decryptIfNecessary(s));
            }
            catch (StorageException | EncryptionException e) {
                continue;
            }
            entries.add(abbreviated);
        }
        return entries;
    }


    /**
     * Method reads the details from the details node and returns them within an array list.
     *
     * @param node  Node which contains all details.
     * @return      List of details retrieved from the passed node.
     */
    private ArrayList<DetailBackupDTO> traverseDetailsNode(Node node) {
        String content = node.getTextContent();
        ArrayList<DetailBackupDTO> details = new ArrayList<>();
        if (content == null) {
            return details;
        }
        String[] rows = content.split("" + CsvConfiguration.ROW_DIVIDER);
        for (String s : rows) {
            if (s.isEmpty()) {
                continue;
            }
            DetailBackupDTO detailDto;
            try {
                detailDto = new DetailBackupDTO(decryptIfNecessary(s));
            }
            catch (StorageException | EncryptionException e) {
                continue;
            }
            details.add(detailDto);
        }
        return details;
    }


    /**
     * Method reads the settings within the backup and returns them within a map.
     * The map's key equals the setting name and the map's value equals the setting value.
     *
     * @param node  Node which contains the settings.
     * @return      Map containing the settings.
     */
    private HashMap<String, String> traverseSettingsNode(Node node) {
        HashMap<String, String> settings = new HashMap<>();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node currentChild = children.item(i);
            if (currentChild.getNodeName().equals(XmlConfiguration.TAG_SETTINGS_ITEM.getValue())) {
                NamedNodeMap attributes = currentChild.getAttributes();
                Node nameAttribute = attributes.getNamedItem(XmlConfiguration.ATTRIBUTE_SETTINGS_NAME.getValue());
                Node valueAttribute = attributes.getNamedItem(XmlConfiguration.ATTRIBUTE_SETTINGS_VALUE.getValue());
                if (nameAttribute != null && valueAttribute != null) {
                    String name = nameAttribute.getNodeValue();
                    String value = valueAttribute.getNodeValue();
                    if (name != null && value != null && !name.isEmpty() && !value.isEmpty()) {
                        settings.put(name, value);
                    }
                }
            }
        }
        return settings;
    }


    /**
     * Method traverses the passed settings node and retrieves the quality gates.
     *
     * @param node  Settings node containing all settings (and quality gates).
     * @return      List of quality gates.
     */
    private ArrayList<QualityGate> traverseQualityGatesNode(Node node) {
        NodeList settingsNodes = node.getChildNodes();
        ArrayList<QualityGate> qualityGates = new ArrayList<>();

        for (int i = 0; i < settingsNodes.getLength(); i++) {
            Node currentNode = settingsNodes.item(i);
            if (currentNode.getNodeName().equals(XmlConfiguration.TAG_QUALITY_GATES.getValue())) {
                String content = currentNode.getTextContent();
                String[] rows = content.split("" + CsvConfiguration.ROW_DIVIDER);
                for (String s : rows) {
                    if (s.isEmpty()) {
                        continue;
                    }
                    QualityGate qualityGate = new QualityGate();
                    try {
                        qualityGate.fromStorable(s);
                    }
                    catch (StorageException e) {
                        continue;
                    }
                    qualityGates.add(qualityGate);
                }
                break; //Leave loop after quality gates were found.
            }
        }

        return qualityGates;
    }


    /**
     * Method restores the passed entries and details to the {@link EntryManager} and saves the data
     * to persistent storage.
     *
     * @param entries   Entries to be saved.
     * @param details   Details to be saved.
     */
    private void restoreEntries(ArrayList<EntryAbbreviated> entries, ArrayList<DetailBackupDTO> details) {
        if (config.getCurrentDataOption() == RestoreConfig.DELETE_ALL_DATA) {
            EntryManager.getInstance().clear();
        }

        for (EntryAbbreviated abbreviated : entries) {
            EntryExtended extended = new EntryExtended(abbreviated);
            for (DetailBackupDTO detail : details) {
                if (extended.getUuid().equals(detail.getEntryUuid())) {
                    extended.add(detail.toDetail());
                }
            }
            if (EntryManager.getInstance().contains(extended.getUuid())) {
                if (config.getCurrentDataOption() == RestoreConfig.OVERWRITE_EXISTING_DATA) {
                    EntryManager.getInstance().set(extended, extended.getUuid());
                }
            }
            else {
                EntryManager.getInstance().add(extended);
            }
        }

        try {
            EntryManager.getInstance().save(true);
        }
        catch (StorageException e) {
            //Ignore...
        }
    }


    /**
     * Method restores the settings from the passed map.
     *
     * @param settings  Settings to restore.
     */
    private void restoreSettings(HashMap<String, String> settings) {
        for (String s : settings.values()) {
        }
        Config config = Config.getInstance();
        Class<?> clazz = config.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(NoBackup.class)) {
                //Do not restore setting:
                continue;
            }
            try {
                GenericItem<?> item = (GenericItem<?>) field.get(config);
                if (item == null) {
                    continue;
                }
                String setting = settings.getOrDefault(item.getKey(), null);
                if (setting == null || setting.isEmpty()) {
                    continue;
                }
                Config.changeItemValue(item, setting);
                field.set(config, item);
            }
            catch (Exception e) {
                //Continue with next setting...
            }
        }
    }


    /**
     * Method restores the quality gates from the passed list.
     *
     * @param qualityGates  List of quality gates to restore.
     */
    private void restoreQualityGates(ArrayList<QualityGate> qualityGates) {
        QualityGateManager.getInstance().replaceQualityGates(qualityGates, true);
        QualityGateManager.getInstance().saveAllQualityGates();
    }


    /**
     * Method decrypts the passed string. If the backup is not encrypted or the {@link #cipher} is
     * no available for some reason, the passed string is returned unchanged.
     *
     * @param s                     String to decrypt.
     * @return                      Decrypted string.
     * @throws EncryptionException  The passed string could not be decrypted.
     */
    private String decryptIfNecessary(String s) throws EncryptionException {
        if (isEncrypted() && cipher != null) {
            return cipher.decrypt(s);
        }
        return s;
    }


    /**
     * Method returns the XML document of the {@link #uri}.
     *
     * @return                  XML document of the URI.
     * @throws BackupException  The XML document cannot be retrieved.
     * @throws XmlException     Some error regarding the XML structure occurred.
     */
    private Document getXmlDocument() throws BackupException, XmlException {
        ParcelFileDescriptor pfd;
        try {
            pfd = App.getContext().getContentResolver().openFileDescriptor(uri, "r");
        }
        catch (FileNotFoundException e) {
            throw new BackupException(e.getMessage());
        }
        if (pfd == null) {
            throw new BackupException("No parcel file descriptor available");
        }
        FileDescriptor fileDescriptor = pfd.getFileDescriptor();
        if (fileDescriptor == null) {
            throw new BackupException("No file descriptor available");
        }
        Document xmlDocument;
        try (FileInputStream inputStream = new FileInputStream(fileDescriptor)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            xmlDocument = builder.parse(inputStream);
            pfd.close();
        }
        catch (SAXException | ParserConfigurationException e) {
            throw new XmlException(e.getMessage());
        }
        catch (IOException e) {
            throw new BackupException(e.getMessage());
        }
        return xmlDocument;
    }

}
