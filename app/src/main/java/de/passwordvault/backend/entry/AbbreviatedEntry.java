package de.passwordvault.backend.entry;

import java.util.Objects;
import java.util.UUID;


/**
 * Class models an abbreviated entry which contains the most basic information about an entry.
 * Further information about an entry is modeled through {@linkplain Entry}, which is only loaded
 * from memory on demand.
 *
 * @author  Christian-2003
 * @version 1.0.0
 */
public class AbbreviatedEntry {

    /**
     * Attribute stores type 4 UUID of the entry.
     */
    protected String uuid;

    /**
     * Attribute stores name of the entry.
     */
    protected String name;

    /**
     * Attribute stores description of the entry.
     */
    protected String description;

    /**
     * Attribute stores whether the entry shall be visible.
     */
    protected boolean visible;


    /**
     * Constructor instantiates a new AbbreviatedEntry with a random type 4 UUID.
     */
    public AbbreviatedEntry() {
        uuid = UUID.randomUUID().toString();
        name = "";
        description = "";
        visible = true;
    }

    /**
     * Constructor instantiates a new AbbreviatedEntry and copies the attributes of the passed
     * AbbreviatedEntry to this instance.
     *
     * @param entry AbbreviatedEntry whose values shall be copied to this instance.
     */
    public AbbreviatedEntry(AbbreviatedEntry entry) {
        this.uuid = entry.uuid;
        this.name = entry.name;
        this.description = entry.description;
        this.visible = entry.visible;
    }

    /**
     * Constructor constructs a new AbbreviatedEntry with the specified arguments.
     *
     * @param uuid          Type 4 UUID for the entry.
     * @param name          Name for the entry.
     * @param description   Description for the entry.
     */
    public AbbreviatedEntry(String uuid, String name, String description) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        visible = true;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Method tests whether the UUID of the passed entry is identical to the UUID of this entry.
     *
     * @param obj   Entry whose UUID shall be compared to the UUID of this entry.
     * @return      Whether both UUIDs are identical.
     */
    public boolean equals(Object obj) {
        if (obj instanceof AbbreviatedEntry) {
            AbbreviatedEntry entry = (AbbreviatedEntry)obj;
            if (entry.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Generates a hash for this entry based on the UUID.
     *
     * @return  Generated hash for the entry.
     */
    public int hashCode() {
        return Objects.hash(uuid);
    }

}
