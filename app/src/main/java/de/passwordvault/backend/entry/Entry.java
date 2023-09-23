package de.passwordvault.backend.entry;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Class models an extended entry which contains all detailed information about an entry. This
 * information is only loaded from memory on demand, as to speed up the applications response and
 * startup time.
 *
 * @author  Christian-2003
 * @version 1.0.0
 */
public class Entry extends AbbreviatedEntry {

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
        super();
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
        super(entry);
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
        super(uuid, name, description);
        created = Calendar.getInstance();
        changed = created;
        details = new ArrayList<>();
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
     * @param detail    Detail to be added to the list of details.
     * @return          Whether the detail was successfully added to the list of details.
     */
    public boolean add(Detail detail) {
        if (containsUuid(detail.getUuid()) == -1) {
            return details.add(detail);
        }
        return false;
    }

    /**
     * Method removes the detail of the passed UUID from the list of details. If no detail with the
     * specified UUID exists, {@code null} is returned. Otherwise the removed detail is returned.
     *
     * @param uuid  UUID whose detail shall be removed.
     * @return      Removed detail.
     */
    public Detail remove(String uuid) {
        int index = containsUuid(uuid);
        if (index != -1) {
            return details.remove(index);
        }
        return null;
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
