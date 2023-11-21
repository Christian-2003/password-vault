package de.passwordvault.backend.entry;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.Contract;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;


/**
 * Class models an entry which contains all (login) information for any type of account.
 *
 * @author  Christian-2003
 * @version 2.2.2
 */
public class Entry {

    /**
     * Static method generates a new {@link Entry} instance with basic attribute values, as defined
     * with {@link #Entry()}.
     *
     * @return  New Entry instance.
     */
    @NonNull
    @Contract(" -> new")
    public static Entry getInstance() {
        return new Entry();
    }

    /**
     * Static method generates a new {@link Entry} instance with the passed UUID.
     *
     * @param uuid                  UUID for the returned Entry.
     * @return                      New Entry instance with the passed UUID.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    @NonNull
    @Contract("null -> fail")
    public static Entry getInstance(String uuid) {
        if (uuid == null) {
            throw new NullPointerException("Null is invalid UUID");
        }
        Entry entry = new Entry();
        entry.setUuid(uuid);
        return entry;
    }


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
     * Attribute stores the date on which the entry was created.
     */
    private Calendar created;

    /**
     * Attribute stores the date on which the entry was changed the last time.
     */
    private Calendar changed;

    /**
     * Attribute stores a list of all {@linkplain Detail}s of the entry.
     */
    private ArrayList<Detail> details;


    /**
     * Constructor instantiates a new entry without any contents.
     */
    public Entry() {
        uuid = UUID.randomUUID().toString();
        name = "";
        description = "";
        visible = true;
        created = Calendar.getInstance();
        changed = created;
        details = new ArrayList<>();
    }

    /**
     * Constructor instantiates a new Entry and copies the attributes of the passed Entry to this
     * instance.
     *
     * @param entry Entry whose values shall be copied to this instance.
     */
    public Entry(Entry entry) {
        this.uuid = entry.uuid;
        this.name = entry.name;
        this.description = entry.description;
        this.visible = entry.visible;
        this.created = entry.created;
        this.changed = entry.changed;
        this.details = new ArrayList<>(entry.getDetails());
    }

    /**
     * Constructor instantiates a new entry with the passed arguments.
     *
     * @param uuid          Type 4 UUID for the entry.
     * @param name          Name for the entry.
     * @param description   Description for the entry.
     */
    public Entry(String uuid, String name, String description) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        visible = true;
        created = Calendar.getInstance();
        changed = created;
        details = new ArrayList<>();
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

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public Calendar getChanged() {
        return changed;
    }

    public void setChanged(Calendar changed) {
        this.changed = changed;
    }

    public ArrayList<Detail> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<Detail> details) {
        this.details = details;
    }


    /**
     * Method returns an {@linkplain ArrayList} of visible {@linkplain Detail} instances that are
     * handled by this Entry. The returned list contains all details whose {@linkplain Detail#isVisible()}
     * method returns {@code true}.
     *
     * @return  List of visible details.
     */
    public ArrayList<Detail> getVisibleDetails() {
        ArrayList<Detail> visibleDetails = new ArrayList<>();
        for (Detail detail : details) {
            if (detail.isVisible()) {
                visibleDetails.add(detail);
            }
        }
        return visibleDetails;
    }


    /**
     * Method returns an {@linkplain ArrayList} of invisible {@linkplain Detail} instances that are
     * handled by this Entry. The returned list contains all details whose {@linkplain Detail#isVisible()}
     * method returns {@code false}.
     *
     * @return  List of invisible details.
     */
    public ArrayList<Detail> getInvisibleDetails() {
        ArrayList<Detail> invisibleDetails = new ArrayList<>();
        for (Detail detail : details) {
            if (!detail.isVisible()) {
                invisibleDetails.add(detail);
            }
        }
        return invisibleDetails;
    }


    /**
     * Method adds the passed detail to the list of handled details. If a detail with the passed
     * detail's UUID already exists, nothing happens and {@code false} is returned.
     *
     * @param detail Detail to be added to the list of details.
     */
    public void add(Detail detail) {
        if (containsUuid(detail.getUuid()) == -1) {
            details.add(detail);
        }
    }

    /**
     * Method removes the detail of the passed UUID from the list of details. If no detail with the
     * specified UUID exists, {@code null} is returned. Otherwise the removed detail is returned.
     *
     * @param uuid UUID whose detail shall be removed.
     */
    public void remove(String uuid) {
        int index = containsUuid(uuid);
        if (index != -1) {
            details.remove(index);
        }
    }

    /**
     * Method returns the detail of the passed UUID. If no detail with the specified UUID exists,
     * {@code null} is returned.
     *
     * @param uuid  UUID whose detail shall be returned.
     * @return      Detail of the passed UUID.
     */
    public Detail get(String uuid) {
        int index = containsUuid(uuid);
        if (index != -1) {
            return details.get(index);
        }
        return null;
    }

    /**
     * Method changes the detail with the same UUID with the passed detail. If no detail is replaced,
     * {@code false} is returned. Otherwise, {@code true} is returned.
     *
     * @param detail    Detail to replace the detail with the same UUID.
     * @return          Whether a detail was successfully replaced.
     */
    public boolean set(Detail detail) {
        int index = containsUuid(detail.getUuid());
        if (index == -1) {
            //Detail with UUID does not already exist:
            return false;
        }
        details.set(index, detail);
        return true;
    }

    /**
     * Method tests whether the entry contains a detail of the passed UUID.
     *
     * @param uuid  UUID to be tested.
     * @return      Whether a detail of the passed UUID exists.
     */
    public boolean contains(String uuid) {
        return containsUuid(uuid) != -1;
    }

    /**
     * Method returns the number of details that are managed by this entry.
     *
     * @return  Number of handled details.
     */
    public int size() {
        return details.size();
    }


    /**
     * Notifies this Entry that some of its data was changed. This will update the value of
     * {@linkplain #changed} to the current date and time.
     */
    public void notifyDataChange() {
        changed = Calendar.getInstance();
    }


    /**
     * Method tests whether the passed argument is contained within the {@link #name} or
     * {@link #description} of the {@link Entry}.
     *
     * @param s Substring to be tested if present anywhere in this entry.
     * @return  Whether the passed string is contained within this instance.
     */
    public boolean matchesFilter(CharSequence s) {
        return name.toLowerCase().contains(s) || description.toLowerCase().contains(s);
    }


    /**
     * Method tests whether the UUID of the passed entry is identical to the UUID of this entry.
     *
     * @param obj   Entry whose UUID shall be compared to the UUID of this entry.
     * @return      Whether both UUIDs are identical.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Entry) {
            Entry entry = (Entry)obj;
            return entry.getUuid().equals(uuid);
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


    /**
     * Method tests whether a detail with the specified UUID exists in the list of details. If no
     * detail with the specified UUID exists, {@code -1} is returned.
     *
     * @param uuid  UUID to be tested.
     * @return      Index of the detail with the specified UUID.
     */
    private int containsUuid(String uuid) {
        for (int i = 0; i < details.size(); i++) {
            if (details.get(i).getUuid().equals(uuid)) {
                return i;
            }
        }
        return -1;
    }

}
