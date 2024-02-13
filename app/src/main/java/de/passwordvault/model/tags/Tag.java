package de.passwordvault.model.tags;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import de.passwordvault.model.Identifiable;


/**
 * Class models a tag for {@link de.passwordvault.model.entry.EntryAbbreviated}-instances.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class Tag implements Serializable, Identifiable {

    /**
     * Attribute stores the name of the tag.
     */
    private String name;

    /**
     * Attribute stores the UUID of the tag.
     */
    private String uuid;


    /**
     * Constructor instantiates a new tag with the passed name. The generated UUID will be random.
     *
     * @param name                  Name for the tag.
     * @throws NullPointerException The name is {@code null}.
     */
    public Tag(String name) throws NullPointerException {
        setName(name);
        setUuid(UUID.randomUUID().toString());
    }

    /**
     * Constructor instantiates a new tag with the passed name and UUID.
     *
     * @param name                  Name for the tag.
     * @param uuid                  UUID for the tag.
     * @throws NullPointerException One of the passed arguments is {@code null}.
     */
    public Tag(String name, String uuid) throws NullPointerException {
        setName(name);
        setUuid(uuid);
    }


    /**
     * Method returns the name of the tag.
     *
     * @return  Name of the tag.
     */
    public String getName() {
        return name;
    }

    /**
     * Method changes the name of the tag.
     *
     * @param name                  New name for the tag.
     * @throws NullPointerException The passed name is {@code null}.
     */
    public void setName(String name) throws NullPointerException {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
    }

    /**
     * Method returns the UUID of the tag.
     *
     * @return  UUID of the tag.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Method changes the UUID of the tag.
     *
     * @param uuid                  New UUID for the tag.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    public void setUuid(String uuid) throws NullPointerException {
        if (uuid == null) {
            throw new NullPointerException();
        }
        this.uuid = uuid;
    }


    /**
     * Method tests whether the passed object's UUID (if it is an instance of {@link Tag}) is
     * identical to the UUID of this tag.
     *
     * @param obj   Object which shall be compared to this instance.
     * @return      Whether the UUIDs of both instances are identical.
     */
    @Override
    public boolean equals(Identifiable obj) {
        return obj.getUuid().equals(uuid);
    }


    /**
     * Method returns a hash code for this instance.
     *
     * @return  Hash code for this instance.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, uuid);
    }


    /**
     * Method converts this instance into a JSON-string of the following format:
     * {@code {"name":"<name>", "uuid":"<uuid>"}}.
     * This method is intended for debugging purposes.
     *
     * @return  JSON-representation of this instance.
     */
    @NonNull
    @Override
    public String toString() {
        return "{\"name\":\"" + name + "\", \"uuid\":\"" + uuid + "\"}";
    }

}
