package de.passwordvault.model.storage.backup;

import android.net.Uri;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.Calendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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


public class XmlBackupCreator2 extends XmlBackupConfiguration2 {

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
     * @param uri                   URI of the backup file.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public XmlBackupCreator2(Uri uri) throws NullPointerException {
        super(uri, true);
        autoCreated = true;
    }

    /**
     * Constructor instantiates a new XML backup creator to create a backup at the specified URI.
     * If the passed {@code encryptionSeed} is not {@code null}, the seed will be used to generate
     * a key with which to encrypt the backup. Otherwise, the backup is not encrypted.
     * This constructor is intended to be used when creating backups manually.
     *
     * @param uri                   URI of the backup file.
     * @param encryptionSeed        Seed for the encryption key or {@code null}.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public XmlBackupCreator2(Uri uri, String encryptionSeed) throws NullPointerException {
        super(uri, encryptionSeed);
        autoCreated = false;
    }


    public void createBackup(BackupConfig config) throws BackupException, EncryptionException {
        if (config == null) {
            this.config = new BackupConfig();
        }
        else {
            this.config = config;
        }
        createDocument();
        Element rootElement = xmlDocument.createElement(TAG_PASSWORD_VAULT);

        Element metadataElement = createMetadataElement();
        rootElement.appendChild(metadataElement);

        Element dataElement = createDataElement();
        rootElement.appendChild(dataElement);
    }


    /**
     * Method generates the element which contains all metadata.
     *
     * @return  Element containing all data.
     */
    private Element createMetadataElement() {
        Element metadataElement = xmlDocument.createElement(TAG_METADATA);

        Element versionElement = xmlDocument.createElement(TAG_VERSION);
        versionElement.setTextContent(Versions.VERSION_LATEST);
        metadataElement.appendChild(metadataElement);

        Element appVersionElement = xmlDocument.createElement(TAG_APP_VERSION);
        appVersionElement.setTextContent(BuildConfig.VERSION_NAME);
        metadataElement.appendChild(appVersionElement);

        Element createdElement = xmlDocument.createElement(TAG_BACKUP_CREATED);
        createdElement.setTextContent("" + Calendar.getInstance().getTimeInMillis());
        metadataElement.appendChild(createdElement);

        Element autoCreatedElement = xmlDocument.createElement(TAG_AUTO_CREATED);
        autoCreatedElement.setTextContent("" + autoCreated);
        metadataElement.appendChild(autoCreatedElement);

        return metadataElement;
    }


    /**
     * Method generates the element which contains all data (i.e. tags, entries and details).
     *
     * @return                      Element containing all data.
     * @throws EncryptionException  The data could not be encrypted.
     */
    private Element createDataElement() throws EncryptionException {
        Element dataElement = xmlDocument.createElement(TAG_DATA);

        if (encryptionAlgorithm != null && !autoCreated && encryptionKeySeed != null) {
            String encryptedSeed = encryptIfNecessary(encryptionKeySeed);
            dataElement.setAttribute(ATTRIBUTE_CHECKSUM, encryptedSeed);
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
        Element entriesElement = xmlDocument.createElement(TAG_ENTRIES);
        entriesElement.setAttribute(ATTRIBUTE_HEADER, EntryAbbreviated.getStorableAttributes());

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
        Element detailsElement = xmlDocument.createElement(TAG_DETAILS);
        detailsElement.setAttribute(ATTRIBUTE_HEADER, DetailBackupDTO.getStorableAttributes());

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
        Element tagsElement = xmlDocument.createElement(TAG_TAGS);
        tagsElement.setAttribute(ATTRIBUTE_HEADER, TagManager.getStorableAttributes());
        tagsElement.setTextContent(encryptIfNecessary(TagManager.getInstance().toCsv()));
        return tagsElement;
    }


    private Element createSettingsElement() {
        Element settingsElement = xmlDocument.createElement(TAG_SETTINGS);

        if (config.getIncludeQualityGates()) {

        }

        if (config.getIncludeSettings()) {
            Element autofillCachingElement = xmlDocument.createElement(TAG_SETTINGS_ITEM);
            autofillCachingElement.setAttribute(ATTRIBUTE_SETTINGS_NAME, SETTING_AUTOFILL_CACHING);
            autofillCachingElement.setAttribute(ATTRIBUTE_SETTINGS_VALUE, "" + Configuration.useAutofillCaching());
            settingsElement.appendChild(autofillCachingElement);

            Element autofillAuthenticationElement = xmlDocument.createElement(TAG_SETTINGS_ITEM);
            autofillAuthenticationElement.setAttribute(ATTRIBUTE_SETTINGS_NAME, SETTING_AUTOFILL_AUTHENTICATION);
            autofillAuthenticationElement.setAttribute(ATTRIBUTE_SETTINGS_VALUE, "" + Configuration.useAutofillAuthentication());
            settingsElement.appendChild(autofillAuthenticationElement);

            Element darkmodeElement = xmlDocument.createElement(TAG_SETTINGS_ITEM);
            darkmodeElement.setAttribute(ATTRIBUTE_SETTINGS_NAME, SETTING_DARKMODE);
            darkmodeElement.setAttribute(ATTRIBUTE_SETTINGS_VALUE, "" + Configuration.getDarkmode());
            settingsElement.appendChild(darkmodeElement);

            Element recentlyEditedElement = xmlDocument.createElement(TAG_SETTINGS_ITEM);
            recentlyEditedElement.setAttribute(ATTRIBUTE_SETTINGS_NAME, SETTING_NUM_RECENTLY_EDITED);
            recentlyEditedElement.setAttribute(ATTRIBUTE_SETTINGS_VALUE, "" + Configuration.getNumberOfRecentlyEdited());
            settingsElement.appendChild(recentlyEditedElement);

            Element detailSwipeLeftElement = xmlDocument.createElement(TAG_SETTINGS_ITEM);
            detailSwipeLeftElement.setAttribute(ATTRIBUTE_SETTINGS_NAME, SETTING_DETAIL_SWIPE_LEFT);
            detailSwipeLeftElement.setAttribute(ATTRIBUTE_SETTINGS_VALUE, Configuration.getDetailLeftSwipeAction().getPreferencesValue());
            settingsElement.appendChild(detailSwipeLeftElement);

            Element detailSwipeRightElement = xmlDocument.createElement(TAG_SETTINGS_ITEM);
            detailSwipeRightElement.setAttribute(ATTRIBUTE_SETTINGS_NAME, SETTING_DETAIL_SWIPE_RIGHT);
            detailSwipeRightElement.setAttribute(ATTRIBUTE_SETTINGS_VALUE, Configuration.getDetailRightSwipeAction().getPreferencesValue());
            settingsElement.appendChild(detailSwipeRightElement);
        }

        return settingsElement;
    }


    private Element createQualityGatesElement() {
        Element qualityGatesElement = xmlDocument.createElement(TAG_QUALITY_GATES);

        StringBuilder builder = new StringBuilder();
        for (QualityGate gate : QualityGateManager.getInstance().getData()) {
            if (!gate.isEditable()) {
                continue;
            }
        }

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
