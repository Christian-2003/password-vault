package de.passwordvault.model.storage.backup;


/**
 * Enum contains all tag names, attribute names, backup versions and exportable setting names that
 * can be used within XML backups.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public enum XmlConfiguration {

    /**
     * Field stores the latest version number.
     * <b>IMPORTANT: Update this to the newest version number every time a new version is
     * introduced!</b>
     */
    VERSION_LATEST("2"),

    /**
     * Field stores the version number for the second generation of XML backups.
     */
    VERSION_2("2"),

    /**
     * Field stores the version number for the first generation of XML backups.
     */
    VERSION_1("1"),


    /**
     * Field stores the XML tag which encapsulates everything within the XML document.
     */
    TAG_PASSWORD_VAULT("password_vault"),

    /**
     * Field stores the XML tag which encapsulates the metadata of the XML backup.
     */
    TAG_METADATA("metadata"),

    /**
     * Field stores the XML tag which encapsulates the version number of the XML backup.
     */
    TAG_VERSION("version"),

    /**
     * Field stores the XML tag which encapsulates the app version with which the XML backup was created.
     */
    TAG_APP_VERSION("app_version"),

    /**
     * Field stores the XML tag which encapsulates the date on which the backup was created.
     */
    TAG_BACKUP_CREATED("created"),

    /**
     * Field stores the XML tag which stores whether the backup was automatically created.
     */
    TAG_AUTO_CREATED("auto_created"),

    /**
     * Field stores the XML tag which encapsulates all data regarding the encryption.
     */
    TAG_ENCRYPTION("encryption"),

    /**
     * Field stores the XML tag which contains the encrypted key seed. This is
     * required during backup restoration to test whether a correct seed was provided.
     */
    TAG_ENCRYPTION_CHECKSUM("checksum"),

    /**
     * Field stores the XML tag which contains the data which is backed up.
     */
    TAG_DATA("data"),

    /**
     * Field stores the XML tag which contains the entries column names.
     */
    TAG_ENTRIES_HEADER("entries_header"),

    /**
     * Field stores the XML tag which contains the backed up entries.
     */
    TAG_ENTRIES("entries"),

    /**
     * Field stores the XML tag which contains the entries column names.
     */
    TAG_DETAILS_HEADER("details_header"),

    /**
     * Field stores the XML tag which contains the backed up details.
     */
    TAG_DETAILS("details"),

    /**
     * Field stores the XML tag which contains the backed up tags.
     */
    TAG_TAGS("tags"),

    /**
     * Field stores the XML tag which contains the backed up settings.
     */
    TAG_SETTINGS("settings"),

    /**
     * Field stores the XML tag which contains backed up settings items.
     */
    TAG_SETTINGS_ITEM("item"),

    /**
     * Field stores the XML tag which contains the backed up quality gates.
     */
    TAG_QUALITY_GATES("quality_gates"),


    /**
     * Field stores the name of the attribute containing the encryption checksum.
     */
    ATTRIBUTE_CHECKSUM("checksum"),

    /**
     * Field stores the name of the attribute containing the header for entries and details.
     */
    ATTRIBUTE_HEADER("header"),

    /**
     * Field stores the name of the attribute containing the name of a specific setting.
     */
    ATTRIBUTE_SETTINGS_NAME("setting"),

    /**
     * Field stores the name of the attribute containing the value of a specific setting.
     */
    ATTRIBUTE_SETTINGS_VALUE("value"),


    /**
     * Field stores the name for the setting regarding autofill caching.
     */
    SETTING_AUTOFILL_CACHING("autofill_caching"),

    /**
     * Field stores the name for the setting regarding autofill caching.
     */
    SETTING_AUTOFILL_AUTHENTICATION("autofill_authentication"),

    /**
     * Field stores the name for the setting regarding darkmode.
     */
    SETTING_DARKMODE("darkmode"),

    /**
     * Field stores the name for the setting regarding recently edited entries.
     */
    SETTING_NUM_RECENTLY_EDITED("recently_edited"),

    /**
     * Field stores the name for the setting regarding left swiping details.
     */
    SETTING_DETAIL_SWIPE_LEFT("detail_swipe_left"),

    /**
     * Field stores the name for the setting regarding right swiping details.
     */
    SETTING_DETAIL_SWIPE_RIGHT("detail_swipe_right"),

    /**
     * Field stores the name for the setting regarding whether backups should include settings.
     */
    SETTING_BACKUP_INCLUDE_SETTINGS("backup_include_settings"),

    /**
     * Field stores the name for the setting regarding whether backups should include quality
     * gates.
     */
    SETTING_BACKUP_INCLUDE_QUALITY_GATES("backup_include_quality_gates"),

    /**
     * Field stores the name for the setting regarding whether backups should be encrypted.
     */
    SETTING_BACKUP_ENCRYPT("backup_encrypt"),

    /**
     * Field stores the name for the setting regarding whether screenshots of sensitive data can be
     * created.
     */
    SETTING_PREVENT_SCREENSHOT("prevent_screenshot");


    /**
     * Attribute stores the value of the enum field.
     */
    private final String value;


    /**
     * Constructor instantiates a new field.
     *
     * @param value Value for the field. This corresponds to the name within XML.
     */
    XmlConfiguration(String value) {
        this.value = value;
    }


    /**
     * Method returns the value of the field, which corresponds to the name within XML.
     *
     * @return  Value of the field.
     */
    public String getValue() {
        return value;
    }

    /**
     * Method tests whether the passed string equals the value of the field.
     *
     * @param s String to test.
     * @return  Whether the passed string equals the value of the field.
     */
    public boolean equals(String s) {
        return s != null && s.equals(value);
    }

}
