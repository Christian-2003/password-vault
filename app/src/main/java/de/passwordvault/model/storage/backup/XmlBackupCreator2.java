package de.passwordvault.model.storage.backup;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import androidx.documentfile.provider.DocumentFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.util.Calendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import de.passwordvault.App;
import de.passwordvault.BuildConfig;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.detail.DetailBackupDTO;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.storage.Configuration;
import de.passwordvault.model.storage.csv.CsvConfiguration;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.model.tags.TagManager;


/**
 * Class implements the XML backup creator which can create XML backups of version 2.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class XmlBackupCreator2 extends XmlBackupConfiguration {

    /**
     * Class models a configuration for the backup creator.
     */
    public static class BackupConfig {

        /**
         * Attribute indicates whether settings shall be included when creating the backup.
         */
        private boolean includeSettings;

        /**
         * Attribute indicates whether the quality gates shall be included when creating the backup.
         */
        private boolean includeQualityGates;


        /**
         * Constructor instantiates a new backup config.
         */
        public BackupConfig() {
            includeSettings = false;
            includeQualityGates = false;
        }

        /**
         * Method returns whether settings are included in the backup.
         *
         * @return  Whether the settings are included in the backup.
         */
        public boolean getIncludeSettings() {
            return includeSettings;
        }

        /**
         * Method changes whether the settings are included in the backup.
         *
         * @param includeSettings   Whether the settings are included in the backup.
         */
        public void setIncludeSettings(boolean includeSettings) {
            this.includeSettings = includeSettings;
        }

        /**
         * Method returns whether the quality gates are included in the backup.
         *
         * @return  Whether the quality gates are included in the backup.
         */
        public boolean getIncludeQualityGates() {
            return includeQualityGates;
        }

        /**
         * Method changes whether the quality gates are included in the backup.
         *
         * @param includeQualityGates   Whether quality gates are included in the backup.
         */
        public void setIncludeQualityGates(boolean includeQualityGates) {
            this.includeQualityGates = includeQualityGates;
        }

    }


    /**
     * Attribute stores the name of the file for the backup.
     */
    private final String filename;

    /**
     * Attribute stores the XML document.
     */
    private Document xmlDocument;

    /**
     * Attribute stores the config used for creating the backup.
     */
    private BackupConfig config;

    /**
     * Attribute stores whether the backup is created automatically by the app (= {@code true}) or
     * manually by the user (= {@code false}).
     */
    private final boolean autoCreated;


    /**
     * Constructor instantiates a new XML backup creator to create a backup at the specified URI.
     * The backup will be encrypted with the default key used by the app.
     * This constructor is intended to be used by the automatic backup creation.
     *
     * @param directory             URI of the directory, in which to create the backup file.
     * @param filename              Name of the backup file.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public XmlBackupCreator2(Uri directory, String filename) throws NullPointerException {
        super(directory, true);
        if (filename == null) {
            throw new NullPointerException();
        }
        this.filename = filename;
        autoCreated = true;
    }

    /**
     * Constructor instantiates a new XML backup creator to create a backup at the specified URI.
     * If the passed {@code encryptionSeed} is not {@code null}, the seed will be used to generate
     * a key with which to encrypt the backup. Otherwise, the backup is not encrypted.
     * This constructor is intended to be used when creating backups manually.
     *
     * @param directory             URI of the directory, in which to create the backup file.
     * @param filename              Name of the backup file.
     * @param encryptionSeed        Seed for the encryption key or {@code null}.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public XmlBackupCreator2(Uri directory, String filename, String encryptionSeed) throws NullPointerException {
        super(directory, encryptionSeed);
        if (filename == null) {
            throw new NullPointerException();
        }
        this.filename = filename;
        autoCreated = false;
    }


    /**
     * Method creates the XML backup for the passed configuration. If {@code null} is passed, the
     * default configuration is used.
     *
     * @param config                Configuration for the backup of {@code null}.
     * @throws BackupException      The backup cannot be created.
     * @throws EncryptionException  The backup data cannot be encrypted.
     */
    public void createBackup(BackupConfig config) throws BackupException, EncryptionException {
        if (config == null) {
            this.config = new BackupConfig();
        }
        else {
            this.config = config;
        }
        createDocument();

        Element rootElement = createRootElement();
        xmlDocument.appendChild(rootElement);

        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(xmlDocument);
            if (uri.getPath() == null) {
                throw new BackupException("Invalid file");
            }

            DocumentFile directory = DocumentFile.fromTreeUri(App.getContext(), uri);
            if (directory == null) {
                throw new BackupException("Invalid directory");
            }
            DocumentFile file = directory.createFile("text/plain", filename);
            if (file == null) {
                throw new BackupException("Invalid backup file");
            }
            Uri fileUri = file.getUri();
            ParcelFileDescriptor xml = App.getContext().getContentResolver().openFileDescriptor(fileUri, "w");
            if (xml == null) {
                throw new BackupException("Invalid ParcelFileDescriptor for backup");
            }
            FileDescriptor fs = xml.getFileDescriptor();

            StreamResult result = new StreamResult(new FileOutputStream(fs));
            transformer.transform(source, result);
            xml.close();
        }
        catch (Exception e) {
            throw new BackupException(e.getMessage());
        }
    }


    /**
     * Method generates the root element for the XML.
     *
     * @return                      Root element.
     * @throws EncryptionException  The data could not be encrypted.
     */
    private Element createRootElement() throws EncryptionException {
        Element rootElement = xmlDocument.createElement(XmlConfiguration.TAG_PASSWORD_VAULT.getValue());

        Element metadataElement = createMetadataElement();
        rootElement.appendChild(metadataElement);

        Element dataElement = createDataElement();
        rootElement.appendChild(dataElement);

        if (this.config.getIncludeSettings() || this.config.getIncludeQualityGates()) {
            Element settingsElement = createSettingsElement();
            rootElement.appendChild(settingsElement);
        }

        return rootElement;
    }


    /**
     * Method generates the element which contains all metadata.
     *
     * @return  Element containing all data.
     */
    private Element createMetadataElement() {
        Element metadataElement = xmlDocument.createElement(XmlConfiguration.TAG_METADATA.getValue());

        Element versionElement = xmlDocument.createElement(XmlConfiguration.TAG_VERSION.getValue());
        versionElement.setTextContent(XmlConfiguration.VERSION_LATEST.getValue());
        metadataElement.appendChild(versionElement);

        Element appVersionElement = xmlDocument.createElement(XmlConfiguration.TAG_APP_VERSION.getValue());
        appVersionElement.setTextContent(BuildConfig.VERSION_NAME);
        metadataElement.appendChild(appVersionElement);

        Element createdElement = xmlDocument.createElement(XmlConfiguration.TAG_BACKUP_CREATED.getValue());
        createdElement.setTextContent("" + Calendar.getInstance().getTimeInMillis());
        metadataElement.appendChild(createdElement);

        Element autoCreatedElement = xmlDocument.createElement(XmlConfiguration.TAG_AUTO_CREATED.getValue());
        autoCreatedElement.setTextContent("" + autoCreated);
        metadataElement.appendChild(autoCreatedElement);

        Element settingsElement = xmlDocument.createElement(XmlConfiguration.TAG_SETTINGS.getValue());
        settingsElement.setTextContent("" + config.getIncludeSettings());
        metadataElement.appendChild(settingsElement);

        Element qualityGatesElement = xmlDocument.createElement(XmlConfiguration.TAG_QUALITY_GATES.getValue());
        qualityGatesElement.setTextContent("" + config.getIncludeQualityGates());
        metadataElement.appendChild(qualityGatesElement);

        return metadataElement;
    }


    /**
     * Method generates the element which contains all data (i.e. tags, entries and details).
     *
     * @return                      Element containing all data.
     * @throws EncryptionException  The data could not be encrypted.
     */
    private Element createDataElement() throws EncryptionException {
        Element dataElement = xmlDocument.createElement(XmlConfiguration.TAG_DATA.getValue());

        if (encryptionAlgorithm != null && !autoCreated && encryptionKeySeed != null) {
            String encryptedSeed = encryptIfNecessary(encryptionKeySeed);
            dataElement.setAttribute(XmlConfiguration.ATTRIBUTE_CHECKSUM.getValue(), encryptedSeed);
        }

        Element tagsElement = createTagsElement();
        dataElement.appendChild(tagsElement);

        Element entriesElement = createEntriesElement();
        dataElement.appendChild(entriesElement);

        Element detailsElement = createDetailsElement();
        dataElement.appendChild(detailsElement);

        return dataElement;
    }


    /**
     * Method generates the element which contains all entries.
     *
     * @return                      Element containing all entries.
     * @throws EncryptionException  The entries could not be encrypted.
     */
    private Element createEntriesElement() throws EncryptionException {
        Element entriesElement = xmlDocument.createElement(XmlConfiguration.TAG_ENTRIES.getValue());
        entriesElement.setAttribute(XmlConfiguration.ATTRIBUTE_HEADER.getValue(), EntryAbbreviated.getStorableAttributes());

        StringBuilder builder = new StringBuilder();
        for (EntryAbbreviated entry : EntryManager.getInstance().getData()) {
            builder.append(encryptIfNecessary(entry.toStorable()));
            builder.append("" + CsvConfiguration.ROW_DIVIDER);
        }
        if (builder.length() > 0) {
            //Remove last line break.
            builder.deleteCharAt(builder.length() - 1);
        }

        entriesElement.setTextContent(builder.toString());
        return entriesElement;
    }


    /**
     * Method generates the element which contains all details.
     *
     * @return                      Element containing all details.
     * @throws EncryptionException  The details could not be encrypted.
     */
    private Element createDetailsElement() throws EncryptionException {
        Element detailsElement = xmlDocument.createElement(XmlConfiguration.TAG_DETAILS.getValue());
        detailsElement.setAttribute(XmlConfiguration.ATTRIBUTE_HEADER.getValue(), DetailBackupDTO.getStorableAttributes());

        StringBuilder builder = new StringBuilder();
        for (EntryAbbreviated abbreviated : EntryManager.getInstance().getData()) {
            EntryExtended extended = EntryManager.getInstance().get(abbreviated.getUuid(), false);
            if (extended == null) {
                continue;
            }
            for (Detail detail : extended.getDetails()) {
                DetailBackupDTO detailDto = new DetailBackupDTO(detail, extended.getUuid());
                builder.append(encryptIfNecessary(detailDto.toStorable()));
                builder.append("" + CsvConfiguration.ROW_DIVIDER);
            }
        }
        if (builder.length() > 0) {
            //Remove last line break.
            builder.deleteCharAt(builder.length() - 1);
        }

        detailsElement.setTextContent(builder.toString());
        return detailsElement;
    }


    /**
     * Method generates the element which contains all tags.
     *
     * @return                      Element containing all tags.
     * @throws EncryptionException  The tags could not be encrypted.
     */
    private Element createTagsElement() throws EncryptionException {
        Element tagsElement = xmlDocument.createElement(XmlConfiguration.TAG_TAGS.getValue());
        tagsElement.setAttribute(XmlConfiguration.ATTRIBUTE_HEADER.getValue(), TagManager.getStorableAttributes());
        tagsElement.setTextContent(encryptIfNecessary(TagManager.getInstance().toCsv()));
        return tagsElement;
    }


    /**
     * Method generates the element which contains all settings.
     *
     * @return  Element containing all settings.
     */
    private Element createSettingsElement() {
        Element settingsElement = xmlDocument.createElement(XmlConfiguration.TAG_SETTINGS.getValue());

        if (config.getIncludeQualityGates()) {
            Element qualityGatesElement = createQualityGatesElement();
            settingsElement.appendChild(qualityGatesElement);
        }

        if (config.getIncludeSettings()) {
            Element autofillCachingElement = xmlDocument.createElement(XmlConfiguration.TAG_SETTINGS_ITEM.getValue());
            autofillCachingElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_NAME.getValue(), XmlConfiguration.SETTING_AUTOFILL_CACHING.getValue());
            autofillCachingElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_VALUE.getValue(), "" + Configuration.useAutofillCaching());
            settingsElement.appendChild(autofillCachingElement);

            Element autofillAuthenticationElement = xmlDocument.createElement(XmlConfiguration.TAG_SETTINGS_ITEM.getValue());
            autofillAuthenticationElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_NAME.getValue(), XmlConfiguration.SETTING_AUTOFILL_AUTHENTICATION.getValue());
            autofillAuthenticationElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_VALUE.getValue(), "" + Configuration.useAutofillAuthentication());
            settingsElement.appendChild(autofillAuthenticationElement);

            Element darkmodeElement = xmlDocument.createElement(XmlConfiguration.TAG_SETTINGS_ITEM.getValue());
            darkmodeElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_NAME.getValue(), XmlConfiguration.SETTING_DARKMODE.getValue());
            darkmodeElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_VALUE.getValue(), "" + Configuration.getDarkmode());
            settingsElement.appendChild(darkmodeElement);

            Element recentlyEditedElement = xmlDocument.createElement(XmlConfiguration.TAG_SETTINGS_ITEM.getValue());
            recentlyEditedElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_NAME.getValue(), XmlConfiguration.SETTING_NUM_RECENTLY_EDITED.getValue());
            recentlyEditedElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_VALUE.getValue(), "" + Configuration.getNumberOfRecentlyEdited());
            settingsElement.appendChild(recentlyEditedElement);

            Element detailSwipeLeftElement = xmlDocument.createElement(XmlConfiguration.TAG_SETTINGS_ITEM.getValue());
            detailSwipeLeftElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_NAME.getValue(), XmlConfiguration.SETTING_DETAIL_SWIPE_LEFT.getValue());
            detailSwipeLeftElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_VALUE.getValue(), Configuration.getDetailLeftSwipeAction().getPreferencesValue());
            settingsElement.appendChild(detailSwipeLeftElement);

            Element detailSwipeRightElement = xmlDocument.createElement(XmlConfiguration.TAG_SETTINGS_ITEM.getValue());
            detailSwipeRightElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_NAME.getValue(), XmlConfiguration.SETTING_DETAIL_SWIPE_RIGHT.getValue());
            detailSwipeRightElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_VALUE.getValue(), Configuration.getDetailRightSwipeAction().getPreferencesValue());
            settingsElement.appendChild(detailSwipeRightElement);

            Element backupIncludeSettingsElement = xmlDocument.createElement(XmlConfiguration.TAG_SETTINGS_ITEM.getValue());
            backupIncludeSettingsElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_NAME.getValue(), XmlConfiguration.SETTING_BACKUP_INCLUDE_SETTINGS.getValue());
            backupIncludeSettingsElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_VALUE.getValue(), "" + Configuration.getBackupIncludeSettings());
            settingsElement.appendChild(backupIncludeSettingsElement);

            Element backupIncludeQualityGatesElement = xmlDocument.createElement(XmlConfiguration.TAG_SETTINGS_ITEM.getValue());
            backupIncludeQualityGatesElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_NAME.getValue(), XmlConfiguration.SETTING_BACKUP_INCLUDE_QUALITY_GATES.getValue());
            backupIncludeQualityGatesElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_VALUE.getValue(), "" + Configuration.getBackupIncludeQualityGates());
            settingsElement.appendChild(backupIncludeQualityGatesElement);

            Element backupEncryptElement = xmlDocument.createElement(XmlConfiguration.TAG_SETTINGS_ITEM.getValue());
            backupEncryptElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_NAME.getValue(), XmlConfiguration.SETTING_BACKUP_ENCRYPT.getValue());
            backupEncryptElement.setAttribute(XmlConfiguration.ATTRIBUTE_SETTINGS_VALUE.getValue(), "" + Configuration.getBackupEncrypted());
            settingsElement.appendChild(backupEncryptElement);
        }

        return settingsElement;
    }


    /**
     * Method generates the element which contains all custom quality gates.
     *
     * @return  Element containing all custom quality gates.
     */
    private Element createQualityGatesElement() {
        Element qualityGatesElement = xmlDocument.createElement(XmlConfiguration.TAG_QUALITY_GATES.getValue());

        StringBuilder builder = new StringBuilder();
        for (QualityGate gate : QualityGateManager.getInstance().getData()) {
            if (!gate.isEditable()) {
                continue;
            }
            builder.append(gate.toStorable());
            builder.append("" + CsvConfiguration.ROW_DIVIDER);
        }
        if (builder.length() > 1) {
            builder.deleteCharAt(builder.length() - 1);
        }

        qualityGatesElement.setTextContent(builder.toString());
        return qualityGatesElement;
    }


    /**
     * Method creates the XML document ({@link #xmlDocument}).
     *
     * @throws BackupException  The XML document could not be created.
     */
    private void createDocument() throws BackupException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            xmlDocument = builder.newDocument();
        }
        catch (ParserConfigurationException e) {
            throw new BackupException(e.getMessage());
        }
    }


    /**
     * Method encrypts the passed string if possible. If the backup should not be encrypted, the method
     * returns the passed argument unchanged.
     *
     * @param s                     String to encrypt.
     * @return                      Encrypted string.
     * @throws EncryptionException  The passed argument could not be encrypted.
     */
    private String encryptIfNecessary(String s) throws EncryptionException {
        if (encryptionAlgorithm != null) {
            return encryptionAlgorithm.encrypt(s);
        }
        return s;
    }

}
